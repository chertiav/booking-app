package com.chertiavdev.bookingapp.controller;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ADMIN_EMAIL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CURRENT_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_BOOKING_ID_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_CAN_T_RETRIEVE_PAYMENTS_FOR_USER;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_RENEWAL_PAYMENT_STATUS_ERROR;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ERROR_MESSAGE_USER_ID_INVALID_FORMAT_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.FIELD_BOOKING_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.NULL_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENTS_CANCEL_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENTS_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENTS_RENEW_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENTS_SUCCESS_ENDPOINT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENT_ID_PARAMETER;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENT_RENEWAL_INVALID_USER_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENT_SESSION_INVALID_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENT_SESSION_NOT_FOUND_BY_ID_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENT_SESSION_NOT_FOUND_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENT_SESSION_PENDING_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SESSION_ID_PARAMETER;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TIMESTAMP_FIELD;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_EMAIL_JOHN;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_EMAIL_SANSA;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_ID_PARAMETER;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.buildSingleRequestParams;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorDetailMap;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.createErrorResponse;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.mapMvcResultToObjectDto;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.parseErrorResponseFromMvcResult;
import static com.chertiavdev.bookingapp.utils.helpers.ControllersTestUtils.parseObjectDtoPageResponse;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.executeSqlScripts;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chertiavdev.bookingapp.config.StripeTestConfig;
import com.chertiavdev.bookingapp.config.TestConfig;
import com.chertiavdev.bookingapp.data.builders.PaymentTestDataBuilder;
import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.dto.page.PageResponse;
import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.chertiavdev.bookingapp.dto.payment.PaymentDto;
import com.chertiavdev.bookingapp.model.Payment;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("Payment Controller Integration Test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestConfig.class, StripeTestConfig.class})
class PaymentControllerTest {
    protected static MockMvc mockMvc;
    private static final String[] SETUP_SCRIPTS = {
            "database/accommodation/address/add-address-into-address-table.sql",
            "database/accommodation/add-accommodations-into-accommodations-table.sql",
            "database/user/add-users-to-users-table.sql",
            "database/user/role/add-role-for-into-users_roles_table.sql",
            "database/booking/add-bookings-into-bookings-table.sql",
            "database/payment/add-payments-into-payments-table.sql",
    };
    private static final String[] CLEANUP_SCRIPTS = {
            "database/payment/remove-all-from-payments-table.sql",
            "database/booking/remove-all-bookings-from-bookings-table.sql",
            "database/accommodation/remove-all-accommodations-from-accommodation-table.sql",
            "database/accommodation/address/remove-all-address-from-address-table.sql",
            "database/user/role/remove-role-where-user_id-more-than-one-from-users_roles_table.sql",
            "database/user/remove-users-where-id-more-than-one-from-users-table.sql"
    };
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PaymentTestDataBuilder paymentTestDataBuilder;

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

    @DisplayName("Getting all user's payments successfully by userId parameter when valid usersId "
            + "is provided should return PageResponse of Payments")
    @WithUserDetails(value = USER_EMAIL_JOHN)
    @Test
    void getPayments_ValidUserId_ShouldReturnPageResponseOfPayments() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                USER_ID_PARAMETER,
                String.valueOf(SAMPLE_TEST_ID_2));
        PageResponse<PaymentDto> expected = paymentTestDataBuilder
                .buildAllPaymentDtosUserJhonToPageResponse();

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<PaymentDto> actual = parseObjectDtoPageResponse(
                result,
                objectMapper,
                PaymentDto.class
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

    @DisplayName("Getting all users payments by userId parameter successfully "
            + "when an admin making a request should return PageResponse of Payments")
    @WithUserDetails(value = ADMIN_EMAIL)
    @Test
    void getPayments_ValidUserIdAndAdminRequest_ShouldReturnPageResponseOfPayments(
    ) throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                USER_ID_PARAMETER,
                String.valueOf(SAMPLE_TEST_ID_2));
        PageResponse<PaymentDto> expected = paymentTestDataBuilder
                .buildAllPaymentDtosUserJhonToPageResponse();

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<PaymentDto> actual = parseObjectDtoPageResponse(
                result,
                objectMapper,
                PaymentDto.class
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

    @DisplayName("Getting all users payments successfully when an admin making a request "
            + "and userID is not provided should return PageResponse of Payments")
    @WithUserDetails(value = ADMIN_EMAIL)
    @Test
    void getPayments_ValidDataAndAdminRequest_ShouldReturnPageResponseOfPayments(
    ) throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(USER_ID_PARAMETER, "");
        PageResponse<PaymentDto> expected = paymentTestDataBuilder
                .buildAllPaymentDtosForAdminToPageResponse();

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PageResponse<PaymentDto> actual = parseObjectDtoPageResponse(
                result,
                objectMapper,
                PaymentDto.class
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

    @DisplayName("Getting all user payments by userId parameter when userIds are not compared "
            + "should throw a Forbidden")
    @WithUserDetails(value = USER_EMAIL_SANSA)
    @Test
    void getPayments_UsersIdAreNotCompared_ShouldThrowForbidden() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                USER_ID_PARAMETER,
                String.valueOf(SAMPLE_TEST_ID_2));
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_CAN_T_RETRIEVE_PAYMENTS_FOR_USER + SAMPLE_TEST_ID_2
        );

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT)
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

    @DisplayName("Getting all user payments by userId parameter when a user is unauthorized "
            + "should throw a Unauthorized")
    @Test
    void getPayments_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                USER_ID_PARAMETER,
                String.valueOf(SAMPLE_TEST_ID_2));
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT)
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

    @DisplayName("Getting all user's payments by userId parameter when invalid userId "
            + "is provided should throw a BadRequest")
    @WithUserDetails(value = USER_EMAIL_JOHN)
    @Test
    void getPayments_InValidUserId_ShouldThrowBadRequest() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(USER_ID_PARAMETER, NULL_ID);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                String.format(
                        ERROR_MESSAGE_USER_ID_INVALID_FORMAT_MESSAGE,
                        USER_ID_PARAMETER,
                        NULL_ID)
        );

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT)
                        .params(params)
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

    @Sql(
            scripts = {
                    "classpath:database/payment/remove-all-from-payments-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    "classpath:database/payment/remove-all-from-payments-table.sql",
                    "classpath:database/payment/add-payments-into-payments-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Creating a payment when a valid data is provided should return PaymentDto")
    @Test
    void createPayment_ValidData_ShouldReturnPaymentDto() throws Exception {
        //Given
        CreatePaymentRequestDto requestDto = paymentTestDataBuilder
                .getPaymentRequestPendingBookingDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        PaymentDto expected = paymentTestDataBuilder.getPendingPaymentPendingBookingDto();

        //When
        MvcResult result = mockMvc
                .perform(post(PAYMENTS_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        PaymentDto actual = mapMvcResultToObjectDto(result, objectMapper, PaymentDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Creating a payment when an invalid data is provided should throw a BadRequest")
    @Test
    void createPayment_InValidData_ShouldThrowBadRequest() throws Exception {
        //Given
        CreatePaymentRequestDto requestDto = paymentTestDataBuilder
                .createPaymentBadRequestPendingBookingDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        Map<String, String> errorDetailDto = createErrorDetailMap(
                FIELD_BOOKING_ID,
                ERROR_MESSAGE_BOOKING_ID_NULL
        );
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of(errorDetailDto)
        );

        //When
        MvcResult result = mockMvc
                .perform(post(PAYMENTS_ENDPOINT)
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

    @WithUserDetails(value = ADMIN_EMAIL)
    @DisplayName("Creating a payment when an invalid user`s role is provided "
            + "should throw a Forbidden")
    @Test
    void createPayment_InValidData_ShouldThrowForbidden() throws Exception {
        //Given
        CreatePaymentRequestDto requestDto = paymentTestDataBuilder
                .getPaymentRequestPendingBookingDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(PAYMENTS_ENDPOINT)
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

    @DisplayName("Creating a payment when a user is unauthorized should throw a Unauthorized")
    @Test
    void createPayment_InValidData_ShouldThrowUnauthorized() throws Exception {
        //Given
        CreatePaymentRequestDto requestDto = paymentTestDataBuilder
                .getPaymentRequestPendingBookingDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(PAYMENTS_ENDPOINT)
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
                    "classpath:database/payment/remove-all-from-payments-table.sql",
                    "classpath:database/payment/add-payments-into-payments-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Handling a successes payment when a valid data is provided "
            + "should return PaymentDto")
    @Test
    void success_ValidData_ShouldReturnPaymentDto() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                SESSION_ID_PARAMETER,
                PAYMENT_SESSION_PENDING_ID);
        PaymentDto expected = paymentTestDataBuilder.getPaidPaymentPendingBookingDto();

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT + PAYMENTS_SUCCESS_ENDPOINT)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PaymentDto actual = mapMvcResultToObjectDto(result, objectMapper, PaymentDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Handling a payment when a invalid sessionId is provided "
            + "should throw NotFound")
    @Test
    void success_InValidData_ShouldThrowNotFound() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                SESSION_ID_PARAMETER,
                PAYMENT_SESSION_INVALID_ID);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                PAYMENT_SESSION_NOT_FOUND_MESSAGE + PAYMENT_SESSION_INVALID_ID
        );

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT + PAYMENTS_SUCCESS_ENDPOINT)
                        .params(params)
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

    @WithUserDetails(value = ADMIN_EMAIL)
    @DisplayName("Handling a payment when an invalid users role is provided "
            + "should throw Forbidden")
    @Test
    void success_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                SESSION_ID_PARAMETER,
                PAYMENT_SESSION_PENDING_ID);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT + PAYMENTS_SUCCESS_ENDPOINT)
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

    @DisplayName("Handling a payment when a user is unauthorized "
            + "should throw Unauthorized")
    @Test
    void success_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                SESSION_ID_PARAMETER,
                PAYMENT_SESSION_PENDING_ID);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT + PAYMENTS_SUCCESS_ENDPOINT)
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

    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Canceling a payment when a valid data is provided should return PaymentDto")
    @Test
    void cancel_ValidData_ShouldReturnPaymentDto() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                SESSION_ID_PARAMETER,
                PAYMENT_SESSION_PENDING_ID);
        PaymentDto expected = paymentTestDataBuilder.getPendingPaymentPendingBookingDto();

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT + PAYMENTS_CANCEL_ENDPOINT)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PaymentDto actual = mapMvcResultToObjectDto(result, objectMapper, PaymentDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Canceling a payment when a invalid sessionId is provided "
            + "should throw NotFound")
    @Test
    void cancel_InValidData_ShouldThrowNotFound() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                SESSION_ID_PARAMETER,
                PAYMENT_SESSION_INVALID_ID);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                PAYMENT_SESSION_NOT_FOUND_MESSAGE + PAYMENT_SESSION_INVALID_ID
        );

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT + PAYMENTS_CANCEL_ENDPOINT)
                        .params(params)
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

    @WithUserDetails(value = ADMIN_EMAIL)
    @DisplayName("Canceling a payment when an invalid users role is provided "
            + "should throw Forbidden")
    @Test
    void cancel_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                SESSION_ID_PARAMETER,
                PAYMENT_SESSION_PENDING_ID);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT + PAYMENTS_CANCEL_ENDPOINT)
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

    @DisplayName("Canceling a payment when a user is unauthorized "
            + "should throw Unauthorized")
    @Test
    void cancel_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                SESSION_ID_PARAMETER,
                PAYMENT_SESSION_PENDING_ID);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(get(PAYMENTS_ENDPOINT + PAYMENTS_CANCEL_ENDPOINT)
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

    @Sql(
            scripts = {
                    "classpath:database/payment/remove-all-from-payments-table.sql",
                    "classpath:database/payment/add-payments-into-payments-table.sql",
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = USER_EMAIL_SANSA)
    @DisplayName("Renewing a payment when a valid sessionId is provided "
            + "should return PaymentDto")
    @Test
    void renew_ValidSessionId_ShouldReturnPaymentDto() throws Exception {
        //Given
        Payment payment = paymentTestDataBuilder.getExpiredPaymentPendingBooking();
        MultiValueMap<String, String> params = buildSingleRequestParams(
                PAYMENT_ID_PARAMETER,
                payment.getId().toString());
        PaymentDto expected = paymentTestDataBuilder.getPaymentRenewSessionDto();

        //When
        MvcResult result = mockMvc
                .perform(post(PAYMENTS_ENDPOINT + PAYMENTS_RENEW_ENDPOINT)
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PaymentDto actual = mapMvcResultToObjectDto(result, objectMapper, PaymentDto.class);

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @WithUserDetails(value = USER_EMAIL_SANSA)
    @DisplayName("Renewing a payment when a invalid sessionId is provided "
            + "should throw NotFound")
    @Test
    void renew_InValidSessionId_ShouldThrowNotFound() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                PAYMENT_ID_PARAMETER,
                String.valueOf(INVALID_TEST_ID));
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.NOT_FOUND,
                PAYMENT_SESSION_NOT_FOUND_BY_ID_MESSAGE + INVALID_TEST_ID
        );

        //When
        MvcResult result = mockMvc
                .perform(post(PAYMENTS_ENDPOINT + PAYMENTS_RENEW_ENDPOINT)
                        .params(params)
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

    @WithUserDetails(value = USER_EMAIL_SANSA)
    @DisplayName("Renewing a payment when a bad data is provided "
            + "should throw BadRequest")
    @Test
    void renew_InValidData_ShouldThrowBadRequest() throws Exception {
        //Given
        MultiValueMap<String, String> params = buildSingleRequestParams(
                PAYMENT_ID_PARAMETER,
                NULL_ID);
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                String.format(
                        ERROR_MESSAGE_USER_ID_INVALID_FORMAT_MESSAGE,
                        PAYMENT_ID_PARAMETER,
                        NULL_ID)
        );

        //When
        MvcResult result = mockMvc
                .perform(post(PAYMENTS_ENDPOINT + PAYMENTS_RENEW_ENDPOINT)
                        .params(params)
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

    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Renewing a payment by userId parameter when userIds are not compared "
            + "should throw Forbidden")
    @Test
    void renew_UsersIdAreNotCompared_ShouldThrowForbidden() throws Exception {
        //Given
        Payment payment = paymentTestDataBuilder.getExpiredPaymentPendingBooking();
        MultiValueMap<String, String> params = buildSingleRequestParams(
                PAYMENT_ID_PARAMETER,
                payment.getId().toString());
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                PAYMENT_RENEWAL_INVALID_USER_MESSAGE + payment.getId().toString()
        );

        //When
        MvcResult result = mockMvc
                .perform(post(PAYMENTS_ENDPOINT + PAYMENTS_RENEW_ENDPOINT)
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

    @WithUserDetails(value = ADMIN_EMAIL)
    @DisplayName("Renewing a payment by userId parameter when an invalid users role is provided "
            + "should throw Forbidden")
    @Test
    void renew_InValidUsersRole_ShouldThrowForbidden() throws Exception {
        //Given
        Payment payment = paymentTestDataBuilder.getExpiredPaymentPendingBooking();
        MultiValueMap<String, String> params = buildSingleRequestParams(
                PAYMENT_ID_PARAMETER,
                payment.getId().toString());
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.FORBIDDEN,
                ERROR_MESSAGE_ACCESS_DENIED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(PAYMENTS_ENDPOINT + PAYMENTS_RENEW_ENDPOINT)
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

    @DisplayName("Renewing a payment by userId parameter when a user is unauthorized "
            + "should throw Unauthorized")
    @Test
    void renew_Unauthorized_ShouldThrowUnauthorized() throws Exception {
        //Given
        Payment payment = paymentTestDataBuilder.getExpiredPaymentPendingBooking();
        MultiValueMap<String, String> params = buildSingleRequestParams(
                PAYMENT_ID_PARAMETER,
                payment.getId().toString());
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED
        );

        //When
        MvcResult result = mockMvc
                .perform(post(PAYMENTS_ENDPOINT + PAYMENTS_RENEW_ENDPOINT)
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

    @WithUserDetails(value = USER_EMAIL_JOHN)
    @DisplayName("Renewing a payment by userId parameter when a user is unauthorized "
            + "should throw Conflict")
    @Test
    void renew_nauthorized_ShouldThrowConflict() throws Exception {
        //Given
        Payment payment = paymentTestDataBuilder.getPendingPaymentPendingBooking();
        MultiValueMap<String, String> params = buildSingleRequestParams(
                PAYMENT_ID_PARAMETER,
                payment.getId().toString());
        CommonApiErrorResponseDto expected = createErrorResponse(
                HttpStatus.CONFLICT,
                String.format(
                        ERROR_MESSAGE_RENEWAL_PAYMENT_STATUS_ERROR,
                        payment.getId()
                )
        );

        //When
        MvcResult result = mockMvc
                .perform(post(PAYMENTS_ENDPOINT + PAYMENTS_RENEW_ENDPOINT)
                        .params(params)
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
}
