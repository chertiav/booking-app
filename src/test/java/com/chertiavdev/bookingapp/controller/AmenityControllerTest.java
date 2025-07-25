package com.chertiavdev.bookingapp.controller;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_TABLE_NAME;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.AMENITIES_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_AMENITY_CAN_NOT_UPDATE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_AMENITY_NAME_MANDATORY;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_AMENITY_NOT_FOUND_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_TYPE_JAVA_LANG_LONG_FOR_INPUT_STRING_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.FIELD_NANE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.RECORD_SHOULD_BE_DELETED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.RECORD_SHOULD_EXIST_BEFORE_DELETION;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TIMESTAMP_FIELD;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.URL_PARAMETERIZED_TEMPLATE;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorDetailMap;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorResponse;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.mapMvcResultToObjectDto;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.parseErrorResponseFromMvcResult;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.parseObjectDtoToList;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.executeSqlScripts;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.recordExistsInDatabaseById;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chertiavdev.bookingapp.config.TestConfig;
import com.chertiavdev.bookingapp.data.builders.AmenityTestDataBuilder;
import com.chertiavdev.bookingapp.dto.amenity.AmenityDto;
import com.chertiavdev.bookingapp.dto.amenity.CreateAmenityRequestDto;
import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("AmenityControllerTest Integration Test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
class AmenityControllerTest {
    protected static MockMvc mockMvc;
    private static final String[] SETUP_SCRIPTS = {
            "database/amenity/remove-all-from-amenities-table.sql",
            "database/amenity/category/add-amenity_categories-into-amenity_categories-table.sql",
            "database/amenity/add-three-amenity-into-amenity-table.sql"
    };
    private static final String[] CLEANUP_SCRIPTS = {
            "database/amenity/remove-all-from-amenities-table.sql",
            "database/amenity/category/"
                    + "remove-amenity_categories-id_11-from-amenity_categories-table.sql",
            "database/amenity/add-all-amenities-into-amenities-table.sql",
    };
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AmenityTestDataBuilder amenityTestDataBuilder;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void setUp(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        setupDatabase(dataSource);
    }

    @AfterAll
    static void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection, SETUP_SCRIPTS);
        }
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            executeSqlScripts(connection, CLEANUP_SCRIPTS);
        }
    }

    @Test
    @Sql(
            scripts = {"classpath:database/amenity/remove-all-from-amenities-table.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/amenity/remove-all-from-amenities-table.sql",
                    "classpath:database/amenity/add-three-amenity-into-amenity-table.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Creating an amenity should return AmenityDto when a valid data is provided")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void create_ValidData_ShouldReturnAmenityDto() throws Exception {
        //Given
        CreateAmenityRequestDto requestDto = amenityTestDataBuilder.getAmenityFreeWiFiRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        AmenityDto expected = amenityTestDataBuilder.getAmenityFreeWiFiDto();

        //When
        MvcResult result = mockMvc
                .perform(post(AMENITIES_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        AmenityDto actual = mapMvcResultToObjectDto(result, objectMapper, AmenityDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Creating an amenity when an invalid data is provided"
            + "should throw a BadRequest")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void create_InvalidData_ShouldThrowBadRequest() throws Exception {
        //Given
        CreateAmenityRequestDto requestDto = amenityTestDataBuilder
                .createAmenityFreeWiFiBadRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_NANE,
                ERROR_MESSAGE_AMENITY_NAME_MANDATORY
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(post(AMENITIES_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @Test
    @DisplayName("Creating an amenity when an invalid users role is provided"
            + "should throw a Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void create_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        CreateAmenityRequestDto requestDto = amenityTestDataBuilder
                .getAmenityFreeWiFiRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(AMENITIES_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @Test
    @DisplayName("Creating an amenity when an invalid users is unauthorized"
            + "should throw a Unauthorized")
    void create_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        CreateAmenityRequestDto requestDto = amenityTestDataBuilder
                .getAmenityFreeWiFiRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(AMENITIES_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @DisplayName("Get all Amenities")
    @Test
    void getAll_Valid_ShouldReturnListOfAmenityDto() throws Exception {
        //Given
        List<AmenityDto> expected = amenityTestDataBuilder.buildAllAmenityDtosList();

        //When
        MvcResult result = mockMvc
                .perform(get(AMENITIES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        List<AmenityDto> actual = parseObjectDtoToList(result, objectMapper, AmenityDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Getting Amenity by id should return AmenityDto when a valid id is provided")
    void getById_ValidId_ShouldReturnAmenityDto() throws Exception {
        //Given
        Long amenityId = amenityTestDataBuilder.getAmenityFreeWiFi().getId();
        AmenityDto expected = amenityTestDataBuilder.getAmenityFreeWiFiDto();

        //When
        MvcResult result = mockMvc
                .perform(get(AMENITIES_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, amenityId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        AmenityDto actual = mapMvcResultToObjectDto(result, objectMapper, AmenityDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Getting Amenity by id when an invalid id is provided should throw NotFound")
    void getById_InValidId_ShouldThrowNotFound() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_AMENITY_NOT_FOUND_ID + INVALID_TEST_ID
        );

        //When
        MvcResult result = mockMvc
                .perform(get(AMENITIES_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, INVALID_TEST_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @Test
    @DisplayName("Getting Amenity by id when a bad id is provided should throw BadRequest")
    void getById_IdIsNull_ShouldThrowBadRequest() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                ERROR_MESSAGE_TYPE_JAVA_LANG_LONG_FOR_INPUT_STRING_NULL
        );

        //When
        MvcResult result = mockMvc
                .perform(get(AMENITIES_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = {
                    "classpath:database/amenity/remove-all-from-amenities-table.sql",
                    "classpath:database/amenity/add-three-amenity-into-amenity-table.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void update_ValidData_ShouldReturnAmenityDto() throws Exception {
        //Given
        Long amenityId = amenityTestDataBuilder.getAmenityFreeWiFi().getId();
        CreateAmenityRequestDto requestDto = amenityTestDataBuilder
                .getUpdatedAmenityFreeWiFiRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        AmenityDto expected = amenityTestDataBuilder.getUpdatedAmenityFreeWiFiDto();

        //When
        MvcResult result = mockMvc
                .perform(put(AMENITIES_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, amenityId))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        AmenityDto actual = mapMvcResultToObjectDto(result, objectMapper, AmenityDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Updating an amenity when an invalid data is provided should throw BadRequest")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void update_InValidData_ShouldThrowBadRequest() throws Exception {
        //Given
        Long amenityId = amenityTestDataBuilder.getAmenityFreeWiFi().getId();
        CreateAmenityRequestDto requestDto = amenityTestDataBuilder
                .createAmenityFreeWiFiBadRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_NANE,
                ERROR_MESSAGE_AMENITY_NAME_MANDATORY
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(put(AMENITIES_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, amenityId))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @Test
    @DisplayName("Updating an amenity an invalid id is provided should throw NotFound")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void update_InValidId_ShouldThrowNotFound() throws Exception {
        //Given
        CreateAmenityRequestDto requestDto = amenityTestDataBuilder
                .getUpdatedAmenityFreeWiFiRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_AMENITY_CAN_NOT_UPDATE + INVALID_TEST_ID
        );

        //When
        MvcResult result = mockMvc
                .perform(put(AMENITIES_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, INVALID_TEST_ID))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @Test
    @DisplayName("Updating an amenity an invalid users role is provided should throw Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void update_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        Long amenityId = amenityTestDataBuilder.getAmenityFreeWiFi().getId();
        CreateAmenityRequestDto requestDto = amenityTestDataBuilder
                .getUpdatedAmenityFreeWiFiRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(put(AMENITIES_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, amenityId))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @Test
    @DisplayName("Updating an amenity a user is unauthorized should throw Unauthorized")
    void update_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        Long amenityId = amenityTestDataBuilder.getAmenityFreeWiFi().getId();
        CreateAmenityRequestDto requestDto = amenityTestDataBuilder
                .getUpdatedAmenityFreeWiFiRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(put(AMENITIES_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, amenityId))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        //Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @Test
    @Sql(
            scripts = {
                    "classpath:database/amenity/remove-all-from-amenities-table.sql",
                    "classpath:database/amenity/add-three-amenity-into-amenity-table.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deleting an amenity when valid data is provided should return NoContent")
    void deleteById_ValidData_ShouldReturnNoContent() throws Exception {
        //Given
        boolean amenityExistsBefore = recordExistsInDatabaseById(
                jdbcTemplate,
                AMENITY_TABLE_NAME,
                SAMPLE_TEST_ID_1);

        //When
        mockMvc.perform(delete(AMENITIES_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        boolean amenityExistsAfter = recordExistsInDatabaseById(
                jdbcTemplate,
                AMENITY_TABLE_NAME,
                SAMPLE_TEST_ID_1);

        assertTrue(amenityExistsBefore, RECORD_SHOULD_EXIST_BEFORE_DELETION);
        assertFalse(amenityExistsAfter, RECORD_SHOULD_BE_DELETED);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Deleting an amenity when an invalid users role is provided "
            + "should throw Forbidden")
    void deleteById_AccessDenied_ShouldThrowForbidden() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(AMENITIES_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        // Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @Test
    @DisplayName("Deleting an amenity when a user is unauthorized should throw Unauthorized")
    void deleteById_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(AMENITIES_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deleting an amenity when an invalid id is provided should throw BadRequest")
    void deleteById_IdIsNull_ShouldThrowBadRequest() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                ERROR_MESSAGE_TYPE_JAVA_LANG_LONG_FOR_INPUT_STRING_NULL
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(AMENITIES_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, null))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Then
        CommonApiErrorResponseDto actual = parseErrorResponseFromMvcResult(result, objectMapper);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, TIMESTAMP_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                expected.timestamp().toLocalDate(),
                actual.timestamp().toLocalDate(),
                DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH);
    }
}
