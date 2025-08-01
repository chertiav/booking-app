package com.chertiavdev.bookingapp.controller;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_TABLE_NAME;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.NULL_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACCOMMODATION_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CURRENT_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.DAILY_RATE_FIELD;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCOMMODATION_ALREADY_EXISTS;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCOMMODATION_CAN_NOT_UPDATE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCOMMODATION_NOT_FOUND_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_TYPE_JAVA_LANG_LONG_FOR_INPUT_STRING_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_VALUE_MUST_BE_ANY_OF_HOUSE_APARTMENT_ETC;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.FIELD_TYPE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.RECORD_SHOULD_BE_DELETED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.RECORD_SHOULD_EXIST_BEFORE_DELETION;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TIMESTAMP_FIELD;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.URL_PARAMETERIZED_TEMPLATE;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorDetailMap;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorResponse;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.mapMvcResultToObjectDto;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.parseErrorResponseFromMvcResult;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.parseObjectDtoPageResponse;
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
import com.chertiavdev.bookingapp.data.builders.AccommodationTestDataBuilder;
import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.dto.page.PageResponse;
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

@DisplayName("Accommodation Controller Integration Test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
class AccommodationControllerTest {
    protected static MockMvc mockMvc;
    private static final String DAILY_RATE_FORMAT = "%.2f";
    private static final String[] SETUP_SCRIPTS = {
            "database/accommodation/address/add-address-into-address-table.sql",
            "database/accommodation/add-accommodations-into-accommodations-table.sql",
            "database/accommodation/amenities/add-amenities-into-accommodation_amenities-table.sql"
    };
    private static final String[] CLEANUP_SCRIPTS = {
            "database/accommodation/amenities/"
                    + "remove-all-amenities-from-accommodation_amenities-table.sql",
            "database/accommodation/remove-all-accommodations-from-accommodation-table.sql",
            "database/accommodation/address/remove-all-address-from-address-table.sql"
    };
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AccommodationTestDataBuilder accommodationTestBuilder;
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
    static void afterAll(@Autowired DataSource dataSource) {
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

    @Sql(
            scripts = {
                    "classpath:database/accommodation/amenities/"
                            + "remove-all-amenities-from-accommodation_amenities-table.sql",
                    "classpath:database/accommodation/"
                            + "remove-all-accommodations-from-accommodation-table.sql",
                    "classpath:database/accommodation/address/"
                            + "remove-all-address-from-address-table.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/accommodation/amenities/"
                            + "remove-all-amenities-from-accommodation_amenities-table.sql",
                    "classpath:database/accommodation/"
                            + "remove-all-accommodations-from-accommodation-table.sql",
                    "classpath:database/accommodation/address/"
                            + "remove-all-address-from-address-table.sql",
                    "classpath:database/accommodation/address/add-address-into-address-table.sql",
                    "classpath:database/accommodation/"
                            + "add-accommodations-into-accommodations-table.sql",
                    "classpath:database/accommodation/amenities/"
                            + "add-amenities-into-accommodation_amenities-table.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Creating an accommodation should return AccommodationDto "
            + "when a valid data is provided")
    void create_ValidData_ShouldReturnAccommodationDto() throws Exception {
        //Given
        CreateAccommodationRequestDto requestDto = accommodationTestBuilder
                .getPendingAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        AccommodationDto expected = accommodationTestBuilder.getPendingAccommodationDto();

        //When
        MvcResult result = mockMvc
                .perform(post(ACCOMMODATION_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        AccommodationDto actual = mapMvcResultToObjectDto(
                result, objectMapper, AccommodationDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, DAILY_RATE_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                String.format(DAILY_RATE_FORMAT, expected.getDailyRate()),
                String.format(DAILY_RATE_FORMAT, actual.getDailyRate()),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Creating an accommodation when an invalid data is provided "
            + "should throw a BadRequest")
    void create_InValidData_ShouldThrowBadRequest() throws Exception {
        //Given
        CreateAccommodationRequestDto requestDto = accommodationTestBuilder
                .createPendingAccommodationBadRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_TYPE,
                ERROR_MESSAGE_VALUE_MUST_BE_ANY_OF_HOUSE_APARTMENT_ETC
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(post(ACCOMMODATION_ENDPOINT)
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
    @WithMockUser(username = "user")
    @DisplayName("Creating an accommodation when an invalid users role is provided "
            + "should throw a Forbidden")
    void create_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        CreateAccommodationRequestDto requestDto = accommodationTestBuilder
                .getPendingAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(ACCOMMODATION_ENDPOINT)
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
    @DisplayName("Creating an accommodation when a user is unauthorized "
            + "should throw a Unauthorized")
    void create_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        CreateAccommodationRequestDto requestDto = accommodationTestBuilder
                .getPendingAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(ACCOMMODATION_ENDPOINT)
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Creating an accommodation when an accommodation with the same address is already "
            + "exists should throw Conflict")
    void create_DuplicateData_ShouldThrowConflict() throws Exception {
        //Given
        CreateAccommodationRequestDto requestDto = accommodationTestBuilder
                .getPendingAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.CONFLICT,
                ERROR_MESSAGE_ACCOMMODATION_ALREADY_EXISTS
        );

        //When
        MvcResult result = mockMvc
                .perform(post(ACCOMMODATION_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
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

    @DisplayName("Get all available accommodations should return page of AccommodationDto")
    @Test
    void getAllAvailable_Valid_ShouldReturnPageOfAccommodationDto() throws Exception {
        //Given
        PageResponse<AccommodationDto> expected = accommodationTestBuilder
                .buildAvailableAccommodationDtoPageResponse();

        //When
        MvcResult result = mockMvc
                .perform(get(ACCOMMODATION_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<AccommodationDto> actual = parseObjectDtoPageResponse(
                result, objectMapper, AccommodationDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected.getContent(), actual.getContent(),
                CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getTotalElementCount(),
                actual.getMetadata().getTotalElementCount(),
                TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getTotalPageCount(),
                actual.getMetadata().getTotalPageCount(),
                TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getCurrentPage(),
                actual.getMetadata().getCurrentPage(),
                CURRENT_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getMetadata().getPageSize(),
                actual.getMetadata().getPageSize(),
                PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
    }

    @Test
    @DisplayName("Getting an accommodation by id should return AccommodationDto "
            + "when a valid id is provided")
    void getAvailableById_ValidId_ShouldReturnAccommodationDto() throws Exception {
        //Given
        AccommodationDto expected = accommodationTestBuilder.getPendingAccommodationDto();

        //When
        MvcResult result = mockMvc
                .perform(get(ACCOMMODATION_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        AccommodationDto actual = mapMvcResultToObjectDto(
                result, objectMapper, AccommodationDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Getting an accommodation by id when an invalid id is provided "
            + "should throw NotFound")
    void getAvailableById_InValidId_ShouldThrowNotFound() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_ACCOMMODATION_NOT_FOUND_ID + INVALID_TEST_ID
        );

        //When
        MvcResult result = mockMvc
                .perform(get(ACCOMMODATION_ENDPOINT + String
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
    @DisplayName("Getting an accommodation by id when a bad id is provided "
            + "should throw BadRequest")
    void getAvailableById_IdIsNull_ShouldThrowBadRequest() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                ERROR_MESSAGE_TYPE_JAVA_LANG_LONG_FOR_INPUT_STRING_NULL
        );

        //When
        MvcResult result = mockMvc
                .perform(get(ACCOMMODATION_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, NULL_ID))
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
    @Sql(
            scripts = {
                    "classpath:database/accommodation/amenities/"
                            + "remove-all-amenities-from-accommodation_amenities-table.sql",
                    "classpath:database/accommodation/"
                            + "remove-all-accommodations-from-accommodation-table.sql",
                    "classpath:database/accommodation/address/"
                            + "remove-all-address-from-address-table.sql",
                    "classpath:database/accommodation/address/add-address-into-address-table.sql",
                    "classpath:database/accommodation/"
                            + "add-accommodations-into-accommodations-table.sql",
                    "classpath:database/accommodation/amenities/"
                            + "add-amenities-into-accommodation_amenities-table.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Updating an accommodation by id when a valid data is provided "
            + "should return AccommodationDto")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void update_ValidData_ShouldReturnAccommodationDto() throws Exception {
        //Given
        Long id = accommodationTestBuilder.getPendingAccommodation().getId();
        CreateAccommodationRequestDto requestDto = accommodationTestBuilder
                .getUpdatedPendingAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        AccommodationDto expected = accommodationTestBuilder.getUpdatedPendingAccommodationDto();

        //When
        MvcResult result = mockMvc
                .perform(put(ACCOMMODATION_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, id))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        AccommodationDto actual = mapMvcResultToObjectDto(
                result, objectMapper, AccommodationDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertTrue(
                reflectionEquals(expected, actual, DAILY_RATE_FIELD),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(
                String.format(DAILY_RATE_FORMAT, expected.getDailyRate()),
                String.format(DAILY_RATE_FORMAT, actual.getDailyRate()),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Updating an accommodation by id when an invalid id is provided "
            + "should throw NotFound")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void update_InValidId_ShouldThrowNotFound() throws Exception {
        //Given
        CreateAccommodationRequestDto requestDto = accommodationTestBuilder
                .getUpdatedPendingAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_ACCOMMODATION_CAN_NOT_UPDATE + INVALID_TEST_ID
        );

        //When
        MvcResult result = mockMvc
                .perform(put(ACCOMMODATION_ENDPOINT + String
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
    @DisplayName("Updating an accommodation when an invalid data is provided "
            + "should throw BadRequest")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void update_InValidData_ShouldThrowBadRequest() throws Exception {
        //Given
        Long id = accommodationTestBuilder.getPendingAccommodation().getId();
        CreateAccommodationRequestDto requestDto = accommodationTestBuilder
                .createPendingAccommodationBadRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_TYPE,
                ERROR_MESSAGE_VALUE_MUST_BE_ANY_OF_HOUSE_APARTMENT_ETC
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(put(ACCOMMODATION_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, id))
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
    @WithMockUser(username = "user")
    @DisplayName("Updating an accommodation when an invalid users role is provided "
            + "should throw a Forbidden")
    void update_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        Long id = accommodationTestBuilder.getPendingAccommodation().getId();
        CreateAccommodationRequestDto requestDto = accommodationTestBuilder
                .getUpdatedPendingAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(put(ACCOMMODATION_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, id))
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
    @DisplayName("Updating an accommodation when a user is unauthorized "
            + "should throw a Unauthorized")
    void update_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        Long id = accommodationTestBuilder.getPendingAccommodation().getId();
        CreateAccommodationRequestDto requestDto = accommodationTestBuilder
                .getUpdatedPendingAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(put(ACCOMMODATION_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, id))
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
            scripts = {
                    "classpath:database/accommodation/amenities/"
                            + "remove-all-amenities-from-accommodation_amenities-table.sql",
                    "classpath:database/accommodation/"
                            + "remove-all-accommodations-from-accommodation-table.sql",
                    "classpath:database/accommodation/address/"
                            + "remove-all-address-from-address-table.sql",
                    "classpath:database/accommodation/address/add-address-into-address-table.sql",
                    "classpath:database/accommodation/"
                            + "add-accommodations-into-accommodations-table.sql",
                    "classpath:database/accommodation/amenities/"
                            + "add-amenities-into-accommodation_amenities-table.sql"
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Deleting an accommodation when valid data is provided should return NoContent")
    void deleteById_ValidData_ShouldReturnNoContent() throws Exception {
        //Given
        boolean accommodationExistsBefore = recordExistsInDatabaseById(
                jdbcTemplate,
                ACCOMMODATION_TABLE_NAME,
                SAMPLE_TEST_ID_1);

        //When
        mockMvc.perform(delete(ACCOMMODATION_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        boolean accommodationExistsAfter = recordExistsInDatabaseById(
                jdbcTemplate,
                ACCOMMODATION_TABLE_NAME,
                SAMPLE_TEST_ID_1);

        assertTrue(accommodationExistsBefore, RECORD_SHOULD_EXIST_BEFORE_DELETION);
        assertFalse(accommodationExistsAfter, RECORD_SHOULD_BE_DELETED);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Deleting an accommodation when an invalid users role is provided "
            + "should throw Forbidden")
    void deleteById_InValidRole_ShouldThrowForbidden() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(ACCOMMODATION_ENDPOINT + String
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
    @DisplayName("Deleting an accommodation when a user is unauthorized should throw Unauthorized")
    void deleteById_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(ACCOMMODATION_ENDPOINT + String
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
    @DisplayName("Deleting an accommodation when an invalid id is provided should throw BadRequest")
    void deleteById_IdIsNull_ShouldThrowBadRequest() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                ERROR_MESSAGE_TYPE_JAVA_LANG_LONG_FOR_INPUT_STRING_NULL
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(ACCOMMODATION_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, NULL_ID))
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
