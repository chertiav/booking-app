package com.chertiavdev.bookingapp.controller;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_CATEGORY_TABLE_NAME;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_11;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.AMENITY_CATEGORY_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_CATEGORY_IS_MANDATORY;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_AMENITY_CATEGORY_CAN_NOT_UPDATE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_AMENITY_CATEGORY_NOT_FOUND_ID;
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
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.recordExistsInDatabaseById;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chertiavdev.bookingapp.config.TestConfig;
import com.chertiavdev.bookingapp.data.builders.AmenityCategoryTestDataBuilder;
import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.model.AmenityCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
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

@DisplayName("AmenityCategoryControllerTest Integration Test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
class AmenityCategoryControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AmenityCategoryTestDataBuilder amenityCategoryTestDataBuilder;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Sql(
            scripts = {"classpath:database/amenity/category/"
                    + "remove-amenity_categories-where+id_11-from-amenity_categories-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Creating an amenity category should return AmenityCategoryDto "
            + "when a valid data is provided")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void create_ValidData_ShouldReturnAmenityCategoryDto() throws Exception {
        //Given
        CreateAmenityCategoryRequestDto requestDto = amenityCategoryTestDataBuilder
                .getAmenityCategoryNewAmenitiesRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        AmenityCategoryDto expected = amenityCategoryTestDataBuilder
                .getAmenityCategoryNewAmenitiesDto();

        //When
        MvcResult result = mockMvc
                .perform(post(AMENITY_CATEGORY_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        AmenityCategoryDto actual = mapMvcResultToObjectDto(
                result, objectMapper, AmenityCategoryDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Creating an amenity category when an invalid data is provided"
            + "should throw a BadRequest")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void create_InValidData_ShouldThrowBadRequest() throws Exception {
        //Given
        CreateAmenityCategoryRequestDto requestDto = amenityCategoryTestDataBuilder
                .createAmenityCategoryNewAmenitiesBatRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_NANE,
                ERROR_CATEGORY_IS_MANDATORY
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(post(AMENITY_CATEGORY_ENDPOINT)
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
    @DisplayName("Creating an amenity category when an invalid users role is provided"
            + "should throw a Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void create_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        CreateAmenityCategoryRequestDto requestDto = amenityCategoryTestDataBuilder
                .getAmenityCategoryNewAmenitiesRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(AMENITY_CATEGORY_ENDPOINT)
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

    @DisplayName("Get all AmenityCategoryDto")
    @Test
    void getAll_Valid_ShouldReturnListOfAmenityCategoryDto() throws Exception {
        //Given
        List<AmenityCategoryDto> expected = amenityCategoryTestDataBuilder
                .buildAmenityCategoryDtosAllList();

        //When
        MvcResult result = mockMvc
                .perform(get(AMENITY_CATEGORY_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        List<AmenityCategoryDto> actual = parseObjectDtoToList(
                result, objectMapper, AmenityCategoryDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Getting AmenityCategoryDto by id should return AmenityCategoryDto "
            + "when a valid id is provided")
    void getById_ValidId_ShouldReturnAmenityCategoryDto() throws Exception {
        //Given
        AmenityCategoryDto expected = amenityCategoryTestDataBuilder
                .getAmenityCategoryComfortAndConvenienceDto();

        //When
        MvcResult result = mockMvc
                .perform(get(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, expected.getId()
                )).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        AmenityCategoryDto actual = mapMvcResultToObjectDto(
                result, objectMapper, AmenityCategoryDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Getting AmenityCategoryDto by id when an invalid id is provided "
            + "should throw NotFound")
    void getById_InValidId_ShouldThrowNotFound() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_AMENITY_CATEGORY_NOT_FOUND_ID + INVALID_TEST_ID
        );

        //When
        MvcResult result = mockMvc
                .perform(get(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, INVALID_TEST_ID
                )).contentType(MediaType.APPLICATION_JSON))
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
    @DisplayName("Getting AmenityCategoryDto by id when a bad id is provided "
            + "should throw BadRequest")
    void getById_Null_ShouldThrowBadRequest() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                ERROR_MESSAGE_TYPE_JAVA_LANG_LONG_FOR_INPUT_STRING_NULL
        );

        //When
        MvcResult result = mockMvc
                .perform(get(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, null
                )).contentType(MediaType.APPLICATION_JSON))
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

    @Sql(
            scripts = {"classpath:database/amenity/category/"
                    + "add-amenity_categories-into-amenity_categories-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {"classpath:database/amenity/category/"
                    + "remove-amenity_categories-where+id_11-from-amenity_categories-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Updating an amenity category by id successfully when valid data is provided")
    @Test
    void update_ValidData_ShouldReturnAmenityCategoryDto() throws Exception {
        //Given
        AmenityCategory amenityCategoryBefore = amenityCategoryTestDataBuilder
                .getAmenityCategoryNewAmenities();
        CreateAmenityCategoryRequestDto requestDto = amenityCategoryTestDataBuilder
                .getUpdatedAmenityCategoryNewAmenitiesRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult resultBefor = mockMvc
                .perform(get(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, amenityCategoryBefore.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult resultAfter = mockMvc
                .perform(put(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, amenityCategoryBefore.getId()))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        AmenityCategoryDto actualBefor = mapMvcResultToObjectDto(
                resultBefor, objectMapper, AmenityCategoryDto.class);
        AmenityCategoryDto actualAfter = mapMvcResultToObjectDto(
                resultAfter, objectMapper, AmenityCategoryDto.class);
        AmenityCategoryDto expected = amenityCategoryTestDataBuilder
                .getUpdatedAmenityCategoryNewAmenitiesDto();

        assertNotNull(actualAfter, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertNotNull(actualBefor, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actualAfter, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertNotEquals(actualBefor.getName(), actualAfter.getName(),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Updating an amenity category when an invalid data is provided"
            + "should throw BadRequest")
    @Test
    void update_InValidData_ShouldThrowBadRequest() throws Exception {
        //Given
        AmenityCategory amenityCategoryBefore = amenityCategoryTestDataBuilder
                .getAmenityCategoryNewAmenities();
        CreateAmenityCategoryRequestDto requestDto = amenityCategoryTestDataBuilder
                .createAmenityCategoryNewAmenitiesBatRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_NANE,
                ERROR_CATEGORY_IS_MANDATORY
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(put(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, amenityCategoryBefore.getId()))
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Updating an amenity category when an invalid id is provided"
            + "should throw NotFound")
    @Test
    void update_InValidId_ShouldThrowNotFound() throws Exception {
        //Given
        CreateAmenityCategoryRequestDto requestDto = amenityCategoryTestDataBuilder
                .getUpdatedAmenityCategoryNewAmenitiesRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_AMENITY_CATEGORY_CAN_NOT_UPDATE + INVALID_TEST_ID
        );

        //When
        MvcResult result = mockMvc
                .perform(put(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, INVALID_TEST_ID))
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

    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Updating an amenity category when an invalid users role is provided"
            + "should throw Forbidden")
    @Test
    void update_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        Long amenityCategoryId = amenityCategoryTestDataBuilder
                .getAmenityCategoryNewAmenities().getId();
        CreateAmenityCategoryRequestDto requestDto = amenityCategoryTestDataBuilder
                .getUpdatedAmenityCategoryNewAmenitiesRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(put(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, amenityCategoryId))
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

    @DisplayName("Updating an amenity category when a users is unauthorized"
            + "should throw Unauthorized")
    @Test
    void update_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        Long amenityCategoryId = amenityCategoryTestDataBuilder
                .getAmenityCategoryNewAmenities().getId();
        CreateAmenityCategoryRequestDto requestDto = amenityCategoryTestDataBuilder
                .getUpdatedAmenityCategoryNewAmenitiesRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(put(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, amenityCategoryId))
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

    @Sql(
            scripts = {"classpath:database/amenity/category/"
                    + "add-amenity_categories-into-amenity_categories-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {"classpath:database/amenity/category/"
                    + "remove-amenity_categories-where+id_11-from-amenity_categories-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Deleting an amenity category when valid data is provided"
            + "should return NoContent")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void deleteById_ValidData_ShouldReturnNoContent() throws Exception {
        //Given
        boolean amenityCategoryExistsBefore = recordExistsInDatabaseById(
                jdbcTemplate,
                AMENITY_CATEGORY_TABLE_NAME,
                SAMPLE_TEST_ID_11);

        //When
        mockMvc.perform(delete(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_11))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        boolean amenityCategoryExistsAfter = recordExistsInDatabaseById(
                jdbcTemplate,
                AMENITY_CATEGORY_TABLE_NAME,
                SAMPLE_TEST_ID_11);

        assertTrue(amenityCategoryExistsBefore, RECORD_SHOULD_EXIST_BEFORE_DELETION);
        assertFalse(amenityCategoryExistsAfter, RECORD_SHOULD_BE_DELETED);
    }

    @DisplayName("Deleting an amenity category when an invalid users role is provided"
            + "should throw Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    void deleteById_AccessDenied_ShouldThrowForbidden() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_11))
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

    @DisplayName("Deleting an amenity category when a user is unauthorized"
            + "should throw Unauthorized")
    @Test
    void deleteById_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_11))
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

    @DisplayName("Deleting an amenity category when an invalid id is provided"
            + "should throw BadRequest")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void deleteById_InvalidId_ShouldThrowBadRequest() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                ERROR_MESSAGE_TYPE_JAVA_LANG_LONG_FOR_INPUT_STRING_NULL
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(AMENITY_CATEGORY_ENDPOINT + String.format(
                        URL_PARAMETERIZED_TEMPLATE, null))
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
