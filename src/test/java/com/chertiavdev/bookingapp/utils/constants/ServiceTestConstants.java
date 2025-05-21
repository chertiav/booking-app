package com.chertiavdev.bookingapp.utils.constants;

import java.math.BigDecimal;
import java.util.List;

public final class ServiceTestConstants {
    public static final String ACCOMMODATION_SIZE = "Studio";
    public static final BigDecimal ACCOMMODATION_DAILY_RATE = BigDecimal.valueOf(75.50);
    public static final int ACCOMMODATION_AVAILABILITY = 1;
    public static final List<Long> ACCOMMODATION_DEFAULT_AMENITIES = List.of(1L, 2L, 3L);
    public static final String ADDRESS_STREET_KHRESHCHATYK = "Khreshchatyk";
    public static final String ADDRESS_CITY_KYIV = "Kyiv";
    public static final String ADDRESS_HOUSE_NUMBER_15B = "15B";
    public static final String ADDRESS_APARTMENT_NUMBER_25 = "25";
    public static final int TEST_AVAILABILITY_THRESHOLD = 0;
    public static final long SAMPLE_TEST_ID_1 = 1L;
    public static final long SAMPLE_TEST_ID_2 = 2L;
    public static final String ACCOMMODATION_NOT_FOUND_MESSAGE = "Can't find accommodation by id: ";
    public static final String ACCOMMODATION_UPDATE_ERROR_MESSAGE =
            "Can't update accommodation by id: ";
    public static final String AMENITY_CATEGORY_NAME = "Test amenity category";
    public static final String AMENITY_CATEGORY_NOT_FOUND_MESSAGE =
            "Can't find amenity category by id: ";
    public static final String AMENITY_CATEGORY_NAME_UPDATE = "Updated category name";
    public static final String AMENITY_CATEGORY_UPDATE_ERROR_MESSAGE =
            "Can't update category by id: ";
    public static final String CATEGORY_NAME = "Test category";
    public static final String AMENITY_NOT_FOUND_MESSAGE = "Can't find amenity by id: ";
    public static final String AMENITY_NAME_UPDATE = "Updated amenity name";
    public static final String AMENITY_UPDATE_ERROR_MESSAGE = "Can't update amenity by id: ";
    public static final int BOOKING_DAYS_UNTIL_CHECKOUT = 5;
    public static final String BOOKING_ACTION_CREATED = "created";
    public static final String BOOKING_ACTION_CANCELED = "canceled";
    public static final String BOOKING_PENDING_PAYMENT_MESSAGE =
            "Your booking request has been submitted and is awaiting payment";
    public static final long BOOKING_PENDING_PAYMENTS_COUNT = 0L;
    public static final long BOOKING_HAS_PENDING_PAYMENTS_COUNT = 1L;
    public static final String BOOKING_USER_HAS_PAYMENT_MESSAGE =
            "User can't create new booking because has pending payments";
    public static final String BOOKING_UNAVAILABLE_ACCOMMODATION_MESSAGE =
            "Accommodation is not available for the requested dates: ";
    public static final String BOOKING_NOT_FOUND_MESSAGE = "Can't find booking by id: ";
    public static final String BOOKING_UPDATE_ERROR_MESSAGE = "Can't update booking by id: ";
    public static final String BOOKING_ALREADY_CANCELLED_MESSAGE =
            "Booking with ID %s has already been cancelled.";
    public static final String BOOKING_UPDATE_STATUS_MESSAGE =
            "The booking status should be updated to %s.";
    public static final String BOOKING_NO_EXPIRED_BOOKINGS_TODAY = "No expired bookings today!";
    public static final String SEARCH_USER_ID_KEY = "user";
    public static final String SEARCH_STATUS_KEY = "status";
}
