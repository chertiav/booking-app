package com.chertiavdev.bookingapp.util;

public class ApiResponseConstants {
    // HTTP code
    public static final String RESPONSE_CODE_OK = "200";
    public static final String RESPONSE_CODE_CREATED = "201";
    public static final String RESPONSE_CODE_BAD_REQUEST = "400";
    public static final String RESPONSE_CODE_UNAUTHORIZED = "401";
    public static final String RESPONSE_CODE_FORBIDDEN = "403";
    public static final String RESPONSE_CODE_NOT_FOUND = "404";
    public static final String RESPONSE_CODE_CONFLICT = "409";
    public static final String RESPONSE_CODE_INTERNAL_SERVER_ERROR = "500";
    // HTTP code description
    public static final String USER_REGISTRATION_DESCRIPTION = "Registration a new user";
    public static final String USER_LOGIN_DESCRIPTION = "User login";
    public static final String INVALID_REQUEST_DESCRIPTION = "Invalid request";
    public static final String UNAUTHORIZED_DESCRIPTION = "Unauthorized";
    public static final String FORBIDDEN_DESCRIPTION = "Access denied";
    public static final String NOT_FOUND_DESCRIPTION = "Resource not found";
    public static final String CONFLICT_ERROR_DESCRIPTION = "Client error";
    public static final String INTERNAL_SERVER_ERROR_DESCRIPTION = "Internal Server Error";
    // Constants for Error Examples
    public static final String RESOURCE_NOT_FOUND_EXAMPLE_MESSAGE = "Not Found Error Example";
    public static final String NOT_FOUND_EXAMPLE_DESCRIPTION = "An example of an error where the "
            + "requested resource is not found";
    public static final String UNAUTHORIZED_ERROR_EXAMPLE_MESSAGE = "Unauthorized Error Example";
    public static final String USER_UNAUTHORIZED_ERROR_EXAMPLE_DESCRIPTION = "An example of an "
            + "error where the user is unauthorized";
    public static final String INTERNAL_SERVER_ERROR_EXAMPLE_MESSAGE = "Internal server error Error"
            + " Example";
    public static final String INTERNAL_SERVER_ERROR_EXAMPLE_DESCRIPTION = "An example of an error "
            + "where an internal server issue occurred";
    public static final String VALIDATION_ERROR_EXAMPLE_MESSAGE = "Validation Error Example";
    public static final String VALIDATION_ERROR_EXAMPLE_DESCRIPTION = "An example of a validation"
            + " error response with field-specific issues";
    public static final String GENERAL_ERROR_EXAMPLE_MESSAGE = "General Error Example";
    public static final String GENERAL_ERROR_EXAMPLE_DESCRIPTION = "An example of a general error "
            + "response for invalid request parameters";
    public static final String FORBIDDEN_ERROR_EXAMPLE_MESSAGE = "Forbidden Error Example";
    public static final String FORBIDDEN_ERROR_EXAMPLE_DESCRIPTION = "An example response for a"
            + " forbidden access attempt when the user does not have the required permissions.";
    public static final String CONFLICT_ERROR_EXAMPLE_MESSAGE = "Conflict Error Example";
    public static final String CONFLICT_ERROR_EXAMPLE_DESCRIPTION = "A conflict occurred while "
            + "processing the request, such as duplicate data or violation of constraints.";
}
