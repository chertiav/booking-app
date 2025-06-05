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
    public static final String DEFAULT_NOTIFICATION_MESSAGE = "test message";
    public static final String NOTIFICATION_SEND_ERROR_PREFIX = "Can't send message: ";
    public static final String PAYMENT_SESSION_ID = "1234567890";
    public static final String PAYMENT_SESSION_URL = "session_id_1234567890";
    public static final String CAN_T_RETRIEVE_SESSION_BY_ID = "Can't retrieve session by id";
    public static final String SESSION_ID_RETRIEVAL_ERROR_TEMPLATE = "%s: %s. Reason: %s: %s";
    public static final String EXPECTED_PAYMENT_STATUS_PAID_MESSAGE = "Payment status should be %s";
    public static final String EXPECTED_BOOKING_STATUS_MESSAGE = "Booking status should be %s";
    public static final String PAYMENT_SESSION_NOT_FOUND_MESSAGE =
            "Can't find payment by session id: ";
    public static final String PAYMENT_SESSION_NOT_FOUND_BY_ID_MESSAGE =
            "Can't find payment by id: ";
    public static final String PAYMENT_RENEWAL_INVALID_USER_MESSAGE =
            "Can't renew session by payment id";
    public static final String PAYMENT_RENEWAL_INVALID_STATUS_MESSAGE = "Can't renew payment by "
            + "id: %s payment status must be: %s and booking status must be: %s";
    public static final String STRIPE_APP_BASE_URL_NAME = "appBaseUrl";
    public static final String STRIPE_APP_BASE_URL = "http://localhost:8080";
    public static final String STRIPE_CURRENCY_NAME = "currency";
    public static final String STRIPE_CURRENCY_USD = "usd";
    public static final String SESSION_ID_QUERY_STRING = "?session_id={CHECKOUT_SESSION_ID}";
    public static final long DEFAULT_SESSION_ITEM_QUANTITY = 1L;
    public static final String BOOKING_SESSION_PREFIX = "Booking #";
    public static final String SESSION_STATUS_OPEN = "open";
    public static final String SESSION_STATUS_EXPIRED = "expired";
    public static final String PAYMENT_STATUS_UNPAID = "unpaid";
    public static final String PAYMENT_STATUS_PAID = "paid";
    public static final String SESSION_PAYMENT_SUCCESS_URL = "/api/payments/success";
    public static final String SESSION_PAYMENT_CANCEL_URL = "/api/payments/cancel";
    public static final String STRIPE_API_ERROR_MESSAGE = "Error when working with Stripe API";
    public static final String DEFAULT_TEST_TOKEN = "test-token";
    public static final int NUMBER_OF_MINUTES = 10;
    public static final String TELEGRAM_LINK_TEMPLATE = "https://t.me/%s?start=%s";
    public static final String TEST_BOT_USERNAME = "TestBot";
    public static final String USER_EMAIL_EXAMPLE = "example@example.com";
    public static final String VALID_USER_PASSWORD = "strongPassword123*";
    public static final String USERNAME_FIRST = "John";
    public static final String USERNAME_LAST = "Doe";
    public static final String USER_ALREADY_EXISTS_MESSAGE = "User with email: %s already exists";
    public static final String ROLE_NOT_FOUND_ERROR_MESSAGE = "Role not found role: ";
    public static final String USER_NOT_FOUND_ERROR_MESSAGE = "User not found with %s%s";
    public static final String EMAIL_PREFIX = "email:";
    public static final String USER_ID_PREFIX = "id:";
    public static final String USERNAME_UPDATE_FIRST = "Sansa";
    public static final String USERNAME_UPDATE_LAST = "Stark";
}
