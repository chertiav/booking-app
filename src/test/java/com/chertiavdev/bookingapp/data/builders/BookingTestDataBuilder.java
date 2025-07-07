package com.chertiavdev.bookingapp.data.builders;

import static com.chertiavdev.bookingapp.model.Booking.Status.CANCELED;
import static com.chertiavdev.bookingapp.model.Booking.Status.CONFIRMED;
import static com.chertiavdev.bookingapp.model.Booking.Status.PENDING;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_DAYS_UNTIL_CHECKOUT;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_4;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_5;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createPage;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestBooking;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestBookingRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.mapBookingToDto;

import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.model.Accommodation;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.User;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
public class BookingTestDataBuilder {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int DAYS_UNTIL_EXPIRY = 1;

    private final User userJohn;
    private final User userSansa;

    private final Accommodation pendingAccommodation;
    private final Accommodation confirmedAccommodation;
    private final Booking pendingBooking;
    private final Booking pendingBookingUserSansa;
    private final Booking confirmedBooking;
    private final Booking canceledBooking;
    private final Booking updatedPendingBooking;

    private final CreateBookingRequestDto pendingBookingRequestDto;
    private final CreateBookingRequestDto updatedPendingBookingRequestDto;
    private final CreateBookingRequestDto updatedCanceledBookingRequestDto;
    private final CreateBookingRequestDto overlappingBookingRequestDto;

    private final Booking pendingBookingToModel;

    private final BookingDto pendingBookingDto;
    private final BookingDto confirmedBookingDto;
    private final BookingDto updatedPendingBookingDto;

    private final Pageable pageable;

    public BookingTestDataBuilder(
            AccommodationTestDataBuilder accommodationTestDataBuilder,
            UserTestDataBuilder userTestDataBuilder
    ) {
        this.userJohn = userTestDataBuilder.getUserJohn();
        this.userSansa = userTestDataBuilder.getUserSansa();

        this.confirmedAccommodation = accommodationTestDataBuilder.getConfirmedAccommodation();
        this.pendingAccommodation = accommodationTestDataBuilder.getPendingAccommodation();

        this.pendingBooking = createPendingBooking();
        this.pendingBookingUserSansa = createPendingBookingUserSansa();
        this.confirmedBooking = createConfirmedBooking();
        this.canceledBooking = createCanceledBooking();
        this.updatedPendingBooking = createUpdatedPendingBooking();

        this.pendingBookingRequestDto = createPendingBookingRequestDto();
        this.updatedPendingBookingRequestDto = createUpdatedPendingBookingRequestDto();
        this.updatedCanceledBookingRequestDto = createUpdatedCanceledBookingRequestDto();
        this.overlappingBookingRequestDto = createOverlappingBookingRequestDto();

        this.pendingBookingToModel = createPendingBookingToModel();

        this.pendingBookingDto = createPendingBookingDto();
        this.confirmedBookingDto = createConfirmedBookingDto();
        this.updatedPendingBookingDto = createUpdatedPendingBookingDto();

        this.pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE);
    }

    public Page<Booking> buildExpectedAllBookingsPage() {
        return createPage(List.of(pendingBooking, confirmedBooking), pageable);
    }

    public Page<BookingDto> buildExpectedAllBookingDtosPage() {
        return createPage(List.of(pendingBookingDto, confirmedBookingDto), pageable);
    }

    public Page<Booking> buildExpectedPendingBookingsPage() {
        return createPage(List.of(pendingBooking), pageable);
    }

    public Page<BookingDto> buildExpectedPendingBookingDtosPage() {
        return createPage(List.of(pendingBookingDto), pageable);
    }

    public Page<Booking> buildExpectedEmptyBookingsPage() {
        return createPage(List.of(), pageable);
    }

    public Page<BookingDto> buildExpectedEmptyBookingDtosPage() {
        return createPage(List.of(), pageable);
    }

    public LocalDate getExpiredDate() {
        return LocalDate.now().plusDays(BOOKING_DAYS_UNTIL_CHECKOUT).plusDays(DAYS_UNTIL_EXPIRY);
    }

    public List<Booking> buildUpcomingBookingsList() {
        return List.of(pendingBooking, pendingBookingUserSansa);
    }

    private Booking createPendingBooking() {
        return createTestBooking(
                SAMPLE_TEST_ID_1,
                LocalDate.now(),
                LocalDate.now().plusDays(BOOKING_DAYS_UNTIL_CHECKOUT),
                pendingAccommodation,
                userJohn,
                PENDING
        );
    }

    private Booking createPendingBookingUserSansa() {
        return createTestBooking(
                SAMPLE_TEST_ID_5,
                LocalDate.now(),
                LocalDate.now().plusDays(BOOKING_DAYS_UNTIL_CHECKOUT),
                confirmedAccommodation,
                userSansa,
                PENDING
        );
    }

    private Booking createUpdatedPendingBooking() {
        return createTestBooking(
                SAMPLE_TEST_ID_1,
                LocalDate.now(),
                LocalDate.now().plusDays(BOOKING_DAYS_UNTIL_CHECKOUT).minusDays(1),
                pendingAccommodation,
                userJohn,
                PENDING
        );
    }

    private Booking createConfirmedBooking() {
        return createTestBooking(
                SAMPLE_TEST_ID_4,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                confirmedAccommodation,
                userJohn,
                CONFIRMED
        );
    }

    private Booking createCanceledBooking() {
        return createTestBooking(
                SAMPLE_TEST_ID_2,
                LocalDate.now(),
                LocalDate.now().plusDays(BOOKING_DAYS_UNTIL_CHECKOUT),
                confirmedAccommodation,
                userSansa,
                CANCELED
        );
    }

    private CreateBookingRequestDto createPendingBookingRequestDto() {
        return createTestBookingRequestDto(
                LocalDate.now(),
                LocalDate.now().plusDays(BOOKING_DAYS_UNTIL_CHECKOUT),
                SAMPLE_TEST_ID_1
        );
    }

    private CreateBookingRequestDto createUpdatedPendingBookingRequestDto() {
        return createTestBookingRequestDto(
                LocalDate.now(),
                LocalDate.now().plusDays(BOOKING_DAYS_UNTIL_CHECKOUT).minusDays(1),
                SAMPLE_TEST_ID_1
        );
    }

    private CreateBookingRequestDto createUpdatedCanceledBookingRequestDto() {
        return createTestBookingRequestDto(
                LocalDate.now(),
                LocalDate.now().plusDays(BOOKING_DAYS_UNTIL_CHECKOUT).minusDays(1),
                SAMPLE_TEST_ID_2
        );
    }

    private CreateBookingRequestDto createOverlappingBookingRequestDto() {
        return createTestBookingRequestDto(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(25),
                SAMPLE_TEST_ID_2
        );
    }

    private Booking createPendingBookingToModel() {
        return createTestBooking(
                null,
                LocalDate.now(),
                LocalDate.now().plusDays(BOOKING_DAYS_UNTIL_CHECKOUT),
                pendingAccommodation,
                userJohn,
                PENDING
        );
    }

    private BookingDto createPendingBookingDto() {
        return mapBookingToDto(pendingBooking);
    }

    private BookingDto createUpdatedPendingBookingDto() {
        return mapBookingToDto(updatedPendingBooking);
    }

    private BookingDto createConfirmedBookingDto() {
        return mapBookingToDto(confirmedBooking);
    }
}
