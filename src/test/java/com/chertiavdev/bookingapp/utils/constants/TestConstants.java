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
    public static final String RECORD_SHOULD_BE_DELETED = "Record should be deleted";
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
    public static final String ERROR_MESSAGE_USER_NOT_FOUND_EMAIL = "User not found with email:";
    public static final String ERROR_MESSAGE_USER_NOT_FOUND_ID = "User not found with id:";
    public static final String ERROR_FIRST_NAME_REQUIRED = "First name is required";
    public static final String ERROR_ROLE_MUST_NOT_BE_NULL = "Role must not be null";
    public static final String ERROR_MESSAGE_USER_ALREADY_EXISTS =
            "User with email: %s already exists";
    public static final String ERROR_PASSWORD_DO_NOT_MATCH = "Passwords do not match";
    public static final String ERROR_BAD_CREDENTIALS = "Bad credentials";
    public static final String ERROR_INVALID_EMAIL_FORMAT = "Invalid email format";

    // ======================== API Endpoints ========================
    public static final String USERS_ME_ENDPOINT = "/users/me";
    public static final String USERS_UPDATE_ROLE_ENDPOINT = "/users/%s/role";
    public static final String AUTH_REGISTER_ENDPOINT = "/register";
    public static final String AUTH_LOGIN_ENDPOINT = "/login";

    // ======================== Error Field Keys ========================
    public static final String TIMESTAMP_FIELD = "timestamp";
    public static final String ERROR_FIELD_TITLE = "field";
    public static final String ERROR_MESSAGE_TITLE = "message";
    public static final String FIELD_FIRST_NAME = "firstName";
    public static final String FIELD_ROLE_NAME = "roleName";
    public static final String FIELD_REGISTER_DTO = "userRegisterRequestDto";
    public static final String FIELD_EMAIL = "email";

    private TestConstants() {
    }
}
