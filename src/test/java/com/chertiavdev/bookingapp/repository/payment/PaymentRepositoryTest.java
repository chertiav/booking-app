package com.chertiavdev.bookingapp.repository.payment;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENT_SESSION_INVALID_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.executeSqlScripts;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.chertiavdev.bookingapp.config.TestConfig;
import com.chertiavdev.bookingapp.data.builders.PaymentTestDataBuilder;
import com.chertiavdev.bookingapp.model.Payment;
import com.chertiavdev.bookingapp.model.User;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

@DisplayName("Payment Repository Integration Test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class PaymentRepositoryTest {
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
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentTestDataBuilder paymentTestDataBuilder;

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) {
        setupDatabase(dataSource);
    }

    @AfterAll
    static void tearDown(@Autowired DataSource dataSource) {
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
    @DisplayName("Find all user's payments successfully when valid data is provided should return"
            + "Page of Payments")
    void findAllByUserId_ValidData_ShouldReturnPageOfPayments() {
        //Given
        User user = paymentTestDataBuilder.getPendingBooking().getUser();
        Page<Payment> expected = paymentTestDataBuilder.buildAllPaymentsUserJhonToPage();

        //When
        Page<Payment> actual = paymentRepository.findAllByUserId(
                user.getId(),
                paymentTestDataBuilder.getPageable()
        );

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected.getNumberOfElements(),
                actual.getNumberOfElements(),
                TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalPages(),
                actual.getTotalPages(),
                TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalElements(),
                actual.getTotalElements(),
                PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(expected.getContent(), actual.getContent(),
                CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
    }

    @Test
    @DisplayName("Find all user's payments when invalid data is provided should return"
            + "Page of Empty")
    void findAllByUserId_InValidData_ShouldReturnPageOfEmpty() {
        //Given
        Page<Payment> expected = paymentTestDataBuilder.buildEmptyPaymentsPage();

        //When
        Page<Payment> actual = paymentRepository.findAllByUserId(
                INVALID_TEST_ID,
                paymentTestDataBuilder.getPageable()
        );

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected.getNumberOfElements(),
                actual.getNumberOfElements(),
                TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalPages(),
                actual.getTotalPages(),
                TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(
                expected.getTotalElements(),
                actual.getTotalElements(),
                PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
        assertEquals(expected.getContent(), actual.getContent(),
                CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE);
    }

    @Test
    @DisplayName("Find user's payment successfully when valid SessionId is provided should return"
            + "an Optional of Payment")
    void findBySessionId_ValidSessionId_ShouldReturnOptionalOfPayment() {
        //Given
        Payment expected = paymentTestDataBuilder.getPendingPaymentPendingBooking();

        //When
        Optional<Payment> actual = paymentRepository.findBySessionId(expected.getSessionId());

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_BE_PRESENT);
        assertEquals(expected, actual.get(), ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Find user's payment when invalid SessionId is provided should return"
            + "an Optional of Empty")
    void findBySessionId_InValidSessionId_ShouldReturnOptionalOfEmpty() {
        //When
        Optional<Payment> actual = paymentRepository.findBySessionId(PAYMENT_SESSION_INVALID_ID);

        //Then
        assertFalse(actual.isPresent(), ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }

    @Test
    @DisplayName("Find user's payment successfully by status when valid data is provided should"
            + " return an Optional of Payment")
    void findAllByStatus() {
        //Given
        List<Payment> expected = paymentTestDataBuilder.buildListAllPaymentsByPendingStatus();

        //When
        List<Payment> actual = paymentRepository.findAllByStatus(Payment.Status.PENDING);

        //Then
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Find user's payment by status when null is provided should"
            + " return an List of Empty")
    void findAllByStatus_Null_ShouldReturnListOfEmpty() {
        //Given
        List<Payment> expected = List.of();

        //When
        List<Payment> actual = paymentRepository.findAllByStatus(null);

        //Then
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Find user's payment successfully when valid data is provided should return"
            + " an Optional of Payment")
    void findByBookingId_ValidBookingId_ShouldReturnOptionalOfPayment() {
        //Given
        Payment expected = paymentTestDataBuilder.getPendingPaymentPendingBooking();

        //When
        Optional<Payment> actual = paymentRepository.findByBookingId(expected.getBooking().getId());

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_BE_PRESENT);
        assertEquals(expected, actual.get(), ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Find user's payment successfully when invalid data is provided should return"
            + " an Optional of empty")
    void findByBookingId_InValidBookingId_ShouldReturnOptionalOfEmpty() {
        //When
        Optional<Payment> actual = paymentRepository.findByBookingId(INVALID_TEST_ID);

        //Then
        assertFalse(actual.isPresent(), ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }

    @Test
    void findPendingPaymentsCount() {
        //Given
        User user = paymentTestDataBuilder.getPendingBooking().getUser();
        Long expected = 1L;

        //When
        Long actual = paymentRepository.findPendingPaymentsCount(user.getId());

        //Then
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }
}
