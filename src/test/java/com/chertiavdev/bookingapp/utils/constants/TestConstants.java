package com.chertiavdev.bookingapp.utils.constants;

public final class TestConstants {

    // ======================== General Test Messages ========================
    public static final String ACTUAL_RESULT_SHOULD_NOT_BE_NULL =
            "The actual result should not be null";
    public static final String ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE =
            "The actual result should be equal to the expected one";
    public static final String ACTUAL_RESULT_SHOULD_NOT_BE_EQUAL_TO_THE_EXPECTED_ONE =
            "The actual result should not be equal to the expected one";
    public static final String EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE =
            "The exception message should be equal to the expected one";
    public static final String TOTAL_ELEMENTS_IN_THE_PAGE_DO_NOT_MATCH_THE_EXPECTED_VALUE =
            "The total elements in the page do not match the expected value.";
    public static final String TOTAL_NUMBER_OF_PAGES_DOES_NOT_MATCH_THE_EXPECTED_VALUE =
            "The total number of pages does not match the expected value.";
    public static final String CURRENT_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE =
            "The current page does not match the expected value.";
    public static final String PAGE_SIZE_DOES_NOT_MATCH_THE_EXPECTED_VALUE =
            "The page size does not match the expected value.";
    public static final String CONTENT_OF_THE_PAGE_DOES_NOT_MATCH_THE_EXPECTED_VALUE =
            "The content of the page does not match the expected value.";
    public static final String ACTUAL_RESULT_SHOULD_BE_PRESENT =
            "The actual result should be present.";
    public static final String ACTUAL_RESULT_SHOULD_NOT_BE_PRESENT =
            "The actual result shouldn't be present.";
    public static final String EXPIRATION_TIMESTAMPS_ARE_DIFFERENT =
            "Expiration timestamps are different";
    public static final String RECORD_SHOULD_EXIST_BEFORE_DELETION =
            "Record should exist before deletion";
    public static final String BOOKING_SHOULD_NOT_BE_CANCELED =
            "Booking should not be canceled";
    public static final String RECORD_SHOULD_BE_DELETED = "Record should be deleted";
    public static final String BOOKING_SHOULD_BE_DELETED = "Booking should be canceled";
    public static final String DATE_PART_OF_THE_TIMESTAMP_DOES_NOT_MATCH =
            "The date part of the timestamp does not match between expected and actual objects";
    public static final String USERNAME_IN_THE_TOKEN_SHOULD_MATCH_THE_LOGIN_EMAIL =
            "Username in the token should match the login email";
    public static final String ISSUED_AT_SHOULD_NOT_BE_NULL = "IssuedAt should not be null";
    public static final String ISSUED_AT_SHOULD_BE_IN_THE_PAST = "IssuedAt should be in the past";
    public static final String EXPIRATION_SHOULD_NOT_BE_NULL = "Expiration should not be null";
    public static final String EXPIRATION_SHOULD_BE_IN_THE_FUTURE =
            "Expiration should be in the future";
    public static final String TOKEN_SHOULD_NOT_BE_NULL = "Token should not be null";

    // ======================== Error Messages ========================
    public static final String ERROR_MESSAGE_ACCESS_DENIED = "Access Denied";
    public static final String ERROR_MESSAGE_ACCESS_DENIED_FULL_AUTHENTICATION_IS_REQUIRED =
            "Access Denied: Full authentication is required to access this resource";
    public static final String ERROR_MESSAGE_USER_NOT_FOUND_EMAIL = "User not found with email:";
    public static final String ERROR_MESSAGE_USER_NOT_FOUND_ID = "User not found with id:";
    public static final String ERROR_FIRST_NAME_REQUIRED = "First name is required";
    public static final String ERROR_ROLE_MUST_NOT_BE_NULL = "Role must not be null";
    public static final String ERROR_MESSAGE_USER_ALREADY_EXISTS =
            "User with email: %s already exists";
    public static final String ERROR_PASSWORD_DO_NOT_MATCH = "Passwords do not match";
    public static final String ERROR_BAD_CREDENTIALS = "Bad credentials";
    public static final String ERROR_INVALID_EMAIL_FORMAT = "Invalid email format";
    public static final String ERROR_CATEGORY_IS_MANDATORY = "Category is mandatory";
    public static final String ERROR_MESSAGE_AMENITY_NAME_MANDATORY =
            "Name of the amenity is mandatory";
    public static final String ERROR_MESSAGE_AMENITY_CATEGORY_NOT_FOUND_ID =
            "Can't find amenity category by id: ";
    public static final String ERROR_MESSAGE_AMENITY_NOT_FOUND_ID =
            "Can't find amenity by id: ";
    public static final String ERROR_MESSAGE_ACCOMMODATION_NOT_FOUND_ID =
            "Can't find accommodation by id: ";
    public static final String ERROR_MESSAGE_BOOKING_NOT_FOUND_ID =
            "Can't find booking by id: ";
    public static final String ERROR_MESSAGE_AMENITY_CATEGORY_CAN_NOT_UPDATE =
            "Can't update category by id: ";
    public static final String ERROR_MESSAGE_AMENITY_CAN_NOT_UPDATE =
            "Can't update amenity by id: ";
    public static final String ERROR_MESSAGE_ACCOMMODATION_CAN_NOT_UPDATE =
            "Can't update accommodation by id: ";
    public static final String ERROR_MESSAGE_BOOKING_CAN_NOT_UPDATE =
            "Can't update booking by id: ";
    public static final String ERROR_MESSAGE_TYPE_JAVA_LANG_LONG_FOR_INPUT_STRING_NULL = "Method "
            + "parameter 'id': Failed to convert value of type 'java.lang.String' "
            + "to required type 'java.lang.Long'; For input string: \"null\"";
    public static final String ERROR_MESSAGE_VALUE_MUST_BE_ANY_OF_HOUSE_APARTMENT_ETC =
            "Value must be any of HOUSE|APARTMENT|CONDO|VACATION_HOME";
    public static final String ERROR_MESSAGE_ACCOMMODATION_ALREADY_EXISTS =
            "Accommodation with the same city: Kyiv, street: Khreshchatyk, house number: 15B, "
                    + "apartment number: 25, type: HOUSE, and size: Studio already exists.";
    public static final String ERROR_MESSAGE_BOOKING_ALREADY_CANCELED =
            "Booking with ID %s has already been cancelled.";
    public static final String ERROR_MESSAGE_OUT_DATE_AND_MUST_BE_TODAY_OR_A_FUTURE_DATE =
            "The check-in date must precede the check-out date and must be today or a future date.";
    public static final String ERROR_MESSAGE_ACCOMMODATION_ISNOT_AVAILABLE =
            "Accommodation is not available for the requested dates: %s - %s";

    // ======================== API Endpoints ========================
    public static final String USERS_ME_ENDPOINT = "/users/me";
    public static final String USERS_UPDATE_ROLE_ENDPOINT = "/users/%s/role";
    public static final String AUTH_REGISTER_ENDPOINT = "/register";
    public static final String AUTH_LOGIN_ENDPOINT = "/login";
    public static final String AMENITY_CATEGORY_ENDPOINT = "/amenity-category";
    public static final String AMENITIES_ENDPOINT = "/amenities";
    public static final String ACCOMMODATION_ENDPOINT = "/accommodations";
    public static final String BOOKINGS_ENDPOINT = "/bookings";
    public static final String ALL_USERS_BOOKINGS_ENDPOINT = "/my";
    public static final String URL_PARAMETERIZED_TEMPLATE = "/%s";

    // ======================== Error Field Keys ========================
    public static final String TIMESTAMP_FIELD = "timestamp";
    public static final String DAILY_RATE_FIELD = "dailyRate";
    public static final String ERROR_FIELD_TITLE = "field";
    public static final String ERROR_MESSAGE_TITLE = "message";
    public static final String FIELD_FIRST_NAME = "firstName";
    public static final String FIELD_ROLE_NAME = "roleName";
    public static final String FIELD_REGISTER_DTO = "userRegisterRequestDto";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_NANE = "name";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_CREATE_BOOKING = "createBookingRequestDto";

    // ======================== Search Parameters ========================
    public static final String USER_ID_SEARCH_PARAMETER = "userId";
    public static final String STATUS_SEARCH_PARAMETER = "status";

    private TestConstants() {
    }
}
