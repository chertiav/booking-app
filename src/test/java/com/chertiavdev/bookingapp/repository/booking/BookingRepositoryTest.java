package com.chertiavdev.bookingapp.repository.booking;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE;
import static com.chertiavdev.bookingapp.utils.helpers.RepositoriesTestUtils.executeSqlScripts;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.calculateTotalPriceByBooking;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.chertiavdev.bookingapp.config.TestConfig;
import com.chertiavdev.bookingapp.data.builders.BookingTestDataBuilder;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.User;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Booking Repository Integration Test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class BookingRepositoryTest {
    private static final String[] SETUP_SCRIPTS = {
            "database/accommodation/address/add-address-into-address-table.sql",
            "database/accommodation/add-accommodations-into-accommodations-table.sql",
            "database/user/add-users-to-users-table.sql",
            "database/booking/add-bookings-into-bookings-table.sql"
    };
    private static final String[] CLEANUP_SCRIPTS = {
            "database/booking/remove-all-bookings-from-bookings-table.sql",
            "database/accommodation/remove-all-accommodations-from-accommodation-table.sql",
            "database/accommodation/address/remove-all-address-from-address-table.sql",
            "database/user/remove-users-where-id-more-than-one-from-users-table.sql"
    };
    @Autowired
    private BookingTestDataBuilder bookingsTestDataBuilder;
    @Autowired
    private BookingRepository bookingRepository;

    @BeforeAll
    void setUp(@Autowired DataSource dataSource) {
        setupDatabase(dataSource);
    }

    @AfterAll
    void tearDown(@Autowired DataSource dataSource) {
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

    @ParameterizedTest(name = "Case {index}: {0}")
    @MethodSource("overlappingBookingsProvider")
    @DisplayName("Find overlapping bookings with different date ranges")
    void findOverlappingBookings_ValidData_ShouldReturnListOfBookings(
            String caseName,
            Long accommodationId,
            LocalDate checkIn,
            LocalDate checkOut,
            List<Booking> expected
    ) {
        List<Booking> actual = bookingRepository.findOverlappingBookings(
                accommodationId,
                checkIn,
                checkOut
        );

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Find bookings by user id successfully when valid data is provided should return "
            + "page of bookings")
    void findBookingsByUserId_ValidUserId_ShouldReturnPageOfBookings() {
        //Given
        Page<Booking> expected = bookingsTestDataBuilder.buildExpectedAllBookingsPage();

        //When
        Page<Booking> actual = bookingRepository.findBookingsByUserId(
                bookingsTestDataBuilder.getUserJohn().getId(),
                bookingsTestDataBuilder.getPageable()
        );

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(
                expected.getNumberOfElements(),
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
    @DisplayName("Find bookings by user id successfully when valid data is provided should return "
            + "page of bookings")
    void findBookingsByUserId_InValidUserId_ShouldReturnEmptyPageOfBookings() {
        //Given
        Page<Booking> expected = bookingsTestDataBuilder.buildExpectedEmptyBookingsPage();

        //When
        Page<Booking> actual = bookingRepository.findBookingsByUserId(
                INVALID_TEST_ID,
                bookingsTestDataBuilder.getPageable()
        );

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(
                expected.getNumberOfElements(),
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
    @DisplayName("Find booking successfully when valid data is provided should return Booking")
    void findByIdAndUserId_ValidData_ShouldReturnBooking() {
        //Given
        User user = bookingsTestDataBuilder.getUserJohn();
        Booking expected = bookingsTestDataBuilder.getPendingBooking();

        //When
        Optional<Booking> actual = bookingRepository
                .findByIdAndUserId(expected.getId(), user.getId());

        //Then
        assertTrue(actual.isPresent(), ACTUAL_RESULT_SHOULD_BE_PRESENT);
        assertEquals(expected, actual.get(), ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Find booking when invalid user ID is provided should return optional empty")
    void findByIdAndUserId_InvalidIdOrUserId_ShouldReturnOptionalEmpty() {
        //Given
        Long bookingId = bookingsTestDataBuilder.getPendingBooking().getId();
        Long userId = bookingsTestDataBuilder.getUserJohn().getId();

        //When
        Optional<Booking> actualInvalidUserId = bookingRepository
                .findByIdAndUserId(bookingId, INVALID_TEST_ID);
        Optional<Booking> actualInvalidBookingId = bookingRepository
                .findByIdAndUserId(INVALID_TEST_ID, userId);

        //Then
        assertTrue(actualInvalidUserId.isEmpty(), ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
        assertTrue(actualInvalidBookingId.isEmpty(), ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }

    @Test
    @DisplayName("Find upcoming Bookings successfully when valid data is provided should return "
            + "list of Booking")
    void findUpcomingBookings_ValidData_ShouldReturnListOfBookings() {
        //Given
        List<Booking> expected = bookingsTestDataBuilder.buildUpcomingBookingsList();

        //When
        List<Booking> actual = bookingRepository.findUpcomingBookings(
                bookingsTestDataBuilder.getExpiredDate());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    @Test
    @DisplayName("Find upcoming bookings with non-expired date returns empty list")
    void findUpcomingBookings_NonExpiredDate_ShouldReturnEmptyList() {
        //When
        List<Booking> actual = bookingRepository.findUpcomingBookings(LocalDate.now());

        //Then
        assertTrue(actual.isEmpty(), ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT);
    }

    @Test
    @DisplayName("Calculate total price by booking id and user id successfully when valid data "
            + "is provided")
    void calculateTotalPriceByBookingIdAndUserId_ValidData_ShouldReturnBigDecimal() {
        //Given
        User user = bookingsTestDataBuilder.getUserJohn();
        Booking booking = bookingsTestDataBuilder.getPendingBooking();
        BigDecimal expected = calculateTotalPriceByBooking(booking);

        //When
        BigDecimal actual = bookingRepository.calculateTotalPriceByBookingIdAndUserId(
                booking.getId(),
                user.getId()
        );

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
    }

    private @NotNull Stream<Arguments> overlappingBookingsProvider() {
        Booking pendingBooking = bookingsTestDataBuilder.getPendingBooking();
        Booking confirmedBooking = bookingsTestDataBuilder.getConfirmedBooking();

        return Stream.of(
                arguments("Exact date match for booking PENDING",
                        SAMPLE_TEST_ID_1,
                        pendingBooking.getCheckIn(),
                        pendingBooking.getCheckOut(),
                        List.of(pendingBooking)),
                arguments("Exact date match for booking CONFIRMED",
                        SAMPLE_TEST_ID_2,
                        confirmedBooking.getCheckIn(),
                        confirmedBooking.getCheckOut(),
                        List.of(confirmedBooking)),
                arguments("Exact date match for booking CANCELED",
                        SAMPLE_TEST_ID_2,
                        pendingBooking.getCheckIn(),
                        pendingBooking.getCheckOut(),
                        List.of()),
                arguments("Exact date match for booking EXPIRED",
                        SAMPLE_TEST_ID_2,
                        pendingBooking.getCheckIn(),
                        pendingBooking.getCheckOut(),
                        List.of()),
                arguments("Non-overlapping dates after",
                        SAMPLE_TEST_ID_1,
                        pendingBooking.getCheckOut().plusDays(10),
                        pendingBooking.getCheckOut().plusDays(20),
                        List.of()),
                arguments("Partial overlap at start",
                        SAMPLE_TEST_ID_1,
                        pendingBooking.getCheckIn().minusDays(2),
                        pendingBooking.getCheckIn().plusDays(2),
                        List.of(pendingBooking)),
                arguments("Partial overlap at end",
                        SAMPLE_TEST_ID_1,
                        pendingBooking.getCheckOut().minusDays(2),
                        pendingBooking.getCheckOut().plusDays(2),
                        List.of(pendingBooking))
        );
    }
}
