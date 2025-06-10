package com.chertiavdev.bookingapp.data.builders;

import static com.chertiavdev.bookingapp.model.Accommodation.Type.APARTMENT;
import static com.chertiavdev.bookingapp.model.Accommodation.Type.HOUSE;
import static com.chertiavdev.bookingapp.model.Booking.Status.CONFIRMED;
import static com.chertiavdev.bookingapp.model.Booking.Status.PENDING;
import static com.chertiavdev.bookingapp.model.Role.RoleName.USER;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_AVAILABILITY;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_DEFAULT_AMENITIES;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.BOOKING_DAYS_UNTIL_CHECKOUT;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USERNAME_FIRST;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USERNAME_LAST;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_EMAIL_EXAMPLE;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.accommodationFromRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.bookingFromRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createSampleAccommodationRequest;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createSampleBookingRequest;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.getAmenitiesById;

import com.chertiavdev.bookingapp.model.Accommodation;
import com.chertiavdev.bookingapp.model.Accommodation.Type;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
public class BookingTestDataBuilder {
    private static final String APARTMENT_NUMBER = "26";
    private static final BigDecimal DAILY_RATE = BigDecimal.valueOf(100.50);
    private static final long CONFIRMED_BOOKING_ID = 4L;
    private static final int MINIMUM_BOOKING_LIMIT = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int DAYS_UNTIL_EXPIRY = 1;

    private final User user;
    private final Accommodation pendingAccommodation;
    private final Accommodation confirmedAccommodation;
    private final Booking pendingBooking;
    private final Booking confirmedBooking;
    private final Pageable pageable;

    public BookingTestDataBuilder() {
        pendingAccommodation = createPendingAccommodation();
        confirmedAccommodation = createConfirmedAccommodation();
        user = createTestUser();
        pendingBooking = createPendingBooking();
        confirmedBooking = createConfirmedBooking();
        pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE);
    }

    public Page<Booking> buildExpectedBookingsPage() {
        List<Booking> bookings = List.of(pendingBooking, confirmedBooking);
        return new PageImpl<>(bookings, pageable, bookings.size());
    }

    public Page<Booking> buildExpectedEmptyBookingsPage() {
        return new PageImpl<>(List.of(), pageable, 0);
    }

    public LocalDate getExpiredDate() {
        return LocalDate.now().plusDays(BOOKING_DAYS_UNTIL_CHECKOUT).plusDays(DAYS_UNTIL_EXPIRY);
    }

    public List<Booking> buildUpcomingBookingsList() {
        return List.of(pendingBooking);
    }

    private Accommodation createPendingAccommodation() {
        return createTestAccommodation(
                SAMPLE_TEST_ID_1,
                HOUSE,
                ACCOMMODATION_AVAILABILITY
        );
    }

    private Accommodation createConfirmedAccommodation() {
        Accommodation accommodation = createTestAccommodation(
                SAMPLE_TEST_ID_2,
                APARTMENT,
                MINIMUM_BOOKING_LIMIT
        );
        accommodation.getLocation().setApartmentNumber(APARTMENT_NUMBER);
        accommodation.setDailyRate(DAILY_RATE.setScale(2, RoundingMode.HALF_UP));
        return accommodation;
    }

    private User createTestUser() {
        return ServiceTestUtils.createTestUser(
                SAMPLE_TEST_ID_2,
                USERNAME_FIRST,
                USERNAME_LAST,
                USER_EMAIL_EXAMPLE,
                USER
        );
    }

    private Booking createPendingBooking() {
        return createTestBooking(
                SAMPLE_TEST_ID_1,
                pendingAccommodation,
                user,
                PENDING
        );
    }

    private Booking createConfirmedBooking() {
        Booking booking = createTestBooking(
                CONFIRMED_BOOKING_ID,
                confirmedAccommodation,
                user,
                CONFIRMED
        );
        booking.setCheckIn(booking.getCheckIn().plusDays(10));
        booking.setCheckOut(booking.getCheckIn().plusDays(15));
        return booking;
    }

    private static Accommodation createTestAccommodation(Long id, Type type, Integer availability) {
        Accommodation accommodation = accommodationFromRequestDto(
                createSampleAccommodationRequest());
        accommodation.setId(id);
        accommodation.getLocation().setId(id);
        accommodation.setType(type);
        accommodation.setAvailability(availability);
        accommodation.setAmenities(getAmenitiesById(ACCOMMODATION_DEFAULT_AMENITIES));
        return accommodation;
    }

    private static Booking createTestBooking(
            Long id,
            Accommodation accommodation,
            User user,
            Booking.Status status
    ) {
        Booking booking = bookingFromRequestDto(createSampleBookingRequest());
        booking.setId(id);
        booking.setAccommodation(accommodation);
        booking.setUser(user);
        booking.setStatus(status);
        return booking;
    }
}
