package com.chertiavdev.bookingapp.controller;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_TABLE_NAME;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.NULL_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_EMAIL_JOHN;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ALL_USERS_BOOKINGS_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.BOOKINGS_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.BOOKING_SHOULD_BE_DELETED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.BOOKING_SHOULD_NOT_BE_CANCELED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CURRENT_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCOMMODATION_ISNOT_AVAILABLE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_BOOKING_ALREADY_CANCELED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_BOOKING_CAN_NOT_UPDATE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_BOOKING_NOT_FOUND_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_OUT_DATE_AND_MUST_BE_TODAY_OR_A_FUTURE_DATE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_TYPE_JAVA_LANG_LONG_FOR_INPUT_STRING_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.FIELD_CREATE_BOOKING;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TIMESTAMP_FIELD;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.URL_PARAMETERIZED_TEMPLATE;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorDetailMap;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorResponse;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createSearchParams;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.mapMvcResultToObjectDto;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.parseErrorResponseFromMvcResult;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.parseObjectDtoPageResponse;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.executeSqlScripts;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.isBookingCanceledById;
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
import com.chertiavdev.bookingapp.data.builders.BookingTestDataBuilder;
import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.dto.page.PageResponse;
import com.chertiavdev.bookingapp.model.Booking;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("Booking Controller Integration Test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
class BookingControllerTest {
    protected static MockMvc mockMvc;
    private static final String[] SETUP_SCRIPTS = {
            "database/accommodation/address/add-address-into-address-table.sql",
            "database/accommodation/add-accommodations-into-accommodations-table.sql",
            "database/user/add-users-to-users-table.sql",
            "database/user/role/add-role-for-into-users_roles_table.sql",
            "database/booking/add-bookings-into-bookings-table.sql"
    };
    private static final String[] CLEANUP_SCRIPTS = {
            "database/booking/remove-all-bookings-from-bookings-table.sql",
            "database/accommodation/remove-all-accommodations-from-accommodation-table.sql",
            "database/accommodation/address/remove-all-address-from-address-table.sql",
            "database/user/role/remove-role-where-user_id-more-than-one-from-users_roles_table.sql",
            "database/user/remove-users-where-id-more-than-one-from-users-table.sql"
    };
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookingTestDataBuilder bookingsTestDataBuilder;
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
                    "classpath:database/booking/remove-all-bookings-from-bookings-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/booking/remove-all-bookings-from-bookings-table.sql",
                    "classpath:database/booking/add-bookings-into-bookings-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Creating a booking when a valid data is provided should return BookingDto")
    @Test
    void create_ValidData_ShouldReturnBookingDto() throws Exception {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .getPendingBookingRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        BookingDto expected = bookingsTestDataBuilder.getPendingBookingDto();

        //When
        MvcResult result = mockMvc
                .perform(post(BOOKINGS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        BookingDto actual = mapMvcResultToObjectDto(
                result, objectMapper, BookingDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Creating a booking when an invalid data is provided should throw a BadRequest")
    @Test
    void create_InValidData_ShouldThrowBadRequest() throws Exception {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .createPendingBookingBadRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_CREATE_BOOKING,
                ERROR_MESSAGE_OUT_DATE_AND_MUST_BE_TODAY_OR_A_FUTURE_DATE
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(post(BOOKINGS_ENDPOINT)
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
    @DisplayName("Creating a booking when an invalid users role is provided "
            + "should throw a Forbidden")
    @Test
    void create_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .getPendingBookingRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(BOOKINGS_ENDPOINT)
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

    @DisplayName("Creating a booking when an invalid users role is provided "
            + "should throw a Forbidden")
    @Test
    void create_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .getPendingBookingRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(BOOKINGS_ENDPOINT)
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

    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Creating a booking when an accommodation isn't available should return Conflict")
    @Test
    void create_IsNotAvailable_ShouldThrowConflict() throws Exception {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .getPendingBookingRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.CONFLICT,
                String.format(
                        ERROR_MESSAGE_ACCOMMODATION_ISNOT_AVAILABLE,
                        requestDto.getCheckIn(),
                        requestDto.getCheckOut()
                )
        );

        //When
        MvcResult result = mockMvc
                .perform(post(BOOKINGS_ENDPOINT)
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

    @DisplayName("Get all bookings by parameters when valid data is provided"
            + "should return PageResponse Of BookingDto")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void searchBookings_ValidData_ShouldReturnPageResponseOfBookingDto() throws Exception {
        //Given
        Booking booking = bookingsTestDataBuilder.getPendingBooking();
        MultiValueMap<String, String> params = createSearchParams(
                booking.getUser().getId().toString(),
                Booking.Status.PENDING.name()
        );
        PageResponse<BookingDto> expected = bookingsTestDataBuilder
                .buildExpectedPendingBookingDtosPageResponse();

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKINGS_ENDPOINT)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<BookingDto> actual = parseObjectDtoPageResponse(
                result,
                objectMapper,
                BookingDto.class
        );

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

    @DisplayName("Get all bookings by parameters when valid id and empty status is provided"
            + "should return PageResponse Of BookingDto")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void searchBookings_ValidIdAndEmptyStatus_ShouldReturnPageResponseOfBookingDto(
    ) throws Exception {
        //Given
        Booking booking = bookingsTestDataBuilder.getPendingBooking();
        MultiValueMap<String, String> params = createSearchParams(
                booking.getUser().getId().toString(),
                ""
        );
        PageResponse<BookingDto> expected = bookingsTestDataBuilder
                .buildExpectedAllBookingDtosPageResponse();

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKINGS_ENDPOINT)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<BookingDto> actual = parseObjectDtoPageResponse(
                result,
                objectMapper,
                BookingDto.class
        );

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

    @DisplayName("Get all bookings by parameters when an invalid users role is provided "
            + "should throw a Forbidden")
    @WithMockUser(username = "user")
    @Test
    void searchBookings_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        Booking booking = bookingsTestDataBuilder.getPendingBooking();
        MultiValueMap<String, String> params = createSearchParams(
                booking.getUser().getId().toString(),
                Booking.Status.PENDING.name()
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKINGS_ENDPOINT)
                        .params(params)
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

    @DisplayName("Get all bookings by parameters when a user is unauthorized "
            + "should throw a Unauthorized")
    @Test
    void searchBookings_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        Booking booking = bookingsTestDataBuilder.getPendingBooking();
        MultiValueMap<String, String> params = createSearchParams(
                booking.getUser().getId().toString(),
                Booking.Status.PENDING.name()
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKINGS_ENDPOINT)
                        .params(params)
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

    @DisplayName("Getting all users bookings by id when a valid id is provided "
            + "should return PageResponse of BookingDto")
    @WithUserDetails(value = USER_EMAIL_JOHN)
    @Test
    void getBookingsByUser_ValidId_ShouldReturnPageResponseOfBookingDto() throws Exception {
        //Given
        PageResponse<BookingDto> expected = bookingsTestDataBuilder
                .buildExpectedAllBookingDtosPageResponse();

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKINGS_ENDPOINT + ALL_USERS_BOOKINGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<BookingDto> actual = parseObjectDtoPageResponse(
                result, objectMapper, BookingDto.class);

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

    @DisplayName("Getting all users bookings by id when an invalid users role is provided "
            + "should throw a Forbidden")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void getBookingsByUser_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKINGS_ENDPOINT + ALL_USERS_BOOKINGS_ENDPOINT)
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

    @DisplayName("Getting all users bookings by id when a user is unauthorized "
            + "should throw a Unauthorized")
    @Test
    void getBookingsByUser_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKINGS_ENDPOINT + ALL_USERS_BOOKINGS_ENDPOINT)
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

    @DisplayName("Getting a booking by id when a valid id is provided "
            + "should return BookingDto")
    @WithUserDetails(value = USER_EMAIL_JOHN)
    @Test
    void getBookingById_ValidId_ShouldReturnBookingDto() throws Exception {
        //Given
        BookingDto expected = bookingsTestDataBuilder.getPendingBookingDto();

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKINGS_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        BookingDto actual = mapMvcResultToObjectDto(
                result, objectMapper, BookingDto.class
        );

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @DisplayName("Getting a booking by id when an invalid id is provided "
            + "should throw NotFound")
    @WithUserDetails(value = USER_EMAIL_JOHN)
    @Test
    void getBookingById_InValidId_ShouldThrowNotFound() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_BOOKING_NOT_FOUND_ID + INVALID_TEST_ID
        );

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKINGS_ENDPOINT + String
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

    @DisplayName("Getting a booking by id when a bad id is provided "
            + "should throw BadRequest")
    @WithUserDetails(value = USER_EMAIL_JOHN)
    @Test
    void getBookingById_IdIsNull_ShouldThrowBadRequest() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                ERROR_MESSAGE_TYPE_JAVA_LANG_LONG_FOR_INPUT_STRING_NULL
        );

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKINGS_ENDPOINT + String
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

    @DisplayName("Getting a booking by id when an invalid users role is provided "
            + "should throw Forbidden")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void getBookingById_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKINGS_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_1))
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

    @DisplayName("Getting a booking by id when a user is unauthorized "
            + "should throw Unauthorized")
    @Test
    void getBookingById_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(BOOKINGS_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_1))
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
                    "classpath:database/booking/remove-all-bookings-from-bookings-table.sql",
                    "classpath:database/booking/add-bookings-into-bookings-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Updating a booking by id when a valid data is provided "
            + "should return BookingDto")
    @Test
    @WithUserDetails(value = USER_EMAIL_JOHN)
    void update_ValidData_ShouldReturnAccommodationDto() throws Exception {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .getUpdatedPendingBookingRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        BookingDto expected = bookingsTestDataBuilder.getUpdatedPendingBookingDto();

        //When
        MvcResult result = mockMvc
                .perform(put(BOOKINGS_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_1))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        BookingDto actual = mapMvcResultToObjectDto(
                result, objectMapper, BookingDto.class
        );

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @DisplayName("Updating a booking by id when an invalid id is provided "
            + "should throw NotFound")
    @Test
    @WithUserDetails(value = USER_EMAIL_JOHN)
    void update_InValidId_ShouldThrowNotFound() throws Exception {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .getUpdatedPendingBookingRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ERROR_MESSAGE_BOOKING_CAN_NOT_UPDATE + INVALID_TEST_ID
        );

        //When
        MvcResult result = mockMvc
                .perform(put(BOOKINGS_ENDPOINT + String
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

    @DisplayName("Updating a booking by id when an invalid data is provided "
            + "should throw BadRequest")
    @Test
    @WithUserDetails(value = USER_EMAIL_JOHN)
    void update_InValidData_ShouldThrowBadRequest() throws Exception {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .createPendingBookingBadRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_CREATE_BOOKING,
                ERROR_MESSAGE_OUT_DATE_AND_MUST_BE_TODAY_OR_A_FUTURE_DATE
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(put(BOOKINGS_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, INVALID_TEST_ID))
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

    @DisplayName("Updating a booking by id when an invalid users role is provided "
            + "should throw Forbidden")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void update_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .getUpdatedPendingBookingRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(put(BOOKINGS_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, INVALID_TEST_ID))
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

    @DisplayName("Updating a booking by id when a user is unauthorized "
            + "should throw Unauthorized")
    @Test
    void update_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        CreateBookingRequestDto requestDto = bookingsTestDataBuilder
                .getUpdatedPendingBookingRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(put(BOOKINGS_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, INVALID_TEST_ID))
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
                    "classpath:database/booking/remove-all-bookings-from-bookings-table.sql",
                    "classpath:database/booking/add-bookings-into-bookings-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Canceling a booking when valid data is provided should return NoContent")
    @WithUserDetails(value = USER_EMAIL_JOHN)
    void cancelById_ValidData_ShouldReturnNoContent() throws Exception {
        //Given
        boolean bookingCanceledBefore = isBookingCanceledById(
                jdbcTemplate,
                BOOKING_TABLE_NAME,
                SAMPLE_TEST_ID_1);

        //When
        mockMvc.perform(delete(BOOKINGS_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        boolean bookingCanceledAfter = isBookingCanceledById(
                jdbcTemplate,
                BOOKING_TABLE_NAME,
                SAMPLE_TEST_ID_1);

        assertFalse(bookingCanceledBefore, BOOKING_SHOULD_NOT_BE_CANCELED);
        assertTrue(bookingCanceledAfter, BOOKING_SHOULD_BE_DELETED);
    }

    @Test
    @DisplayName("Canceling a booking when an invalid users role is provided "
            + "should throw Forbidden")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void cancelById_InValidRole_ShouldThrowForbidden() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(BOOKINGS_ENDPOINT + String
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
    @DisplayName("Canceling a booking when a user is unauthorized "
            + "should throw Unauthorized")
    void cancelById_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(BOOKINGS_ENDPOINT + String
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
    @DisplayName("Canceling a booking when an invalid id is provided should throw BadRequest")
    @WithUserDetails(value = USER_EMAIL_JOHN)
    void cancelById_IdIsNull_ShouldThrowBadRequest() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                ERROR_MESSAGE_TYPE_JAVA_LANG_LONG_FOR_INPUT_STRING_NULL
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(BOOKINGS_ENDPOINT + String
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

    @Sql(
            scripts = {
                    "classpath:database/booking/"
                            + "set-status_canceled-where-id_1-in-bookings-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/booking/remove-all-bookings-from-bookings-table.sql",
                    "classpath:database/booking/add-bookings-into-bookings-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Canceling a booking when an invalid id is provided should throw Conflict")
    @WithUserDetails(value = USER_EMAIL_JOHN)
    void cancelById_IdIsNull_ShouldThrowConflict() throws Exception {
        //Given
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.CONFLICT,
                String.format(ERROR_MESSAGE_BOOKING_ALREADY_CANCELED, SAMPLE_TEST_ID_1)
        );

        //When
        MvcResult result = mockMvc
                .perform(delete(BOOKINGS_ENDPOINT + String
                        .format(URL_PARAMETERIZED_TEMPLATE, SAMPLE_TEST_ID_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
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
