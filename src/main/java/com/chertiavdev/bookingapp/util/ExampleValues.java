package com.chertiavdev.bookingapp.util;

public class ExampleValues {
    public static final String EXAMPLE_USER_LOGGING = """
            {
              "email": "example@example.com",
              "password": "strongPassword123*"
            }
            """;
    public static final String EXAMPLE_ADMIN_LOGGING = """
            {
              "email": "admin@example.com",
              "password": "12345678"
            }
            """;
    public static final String VALIDATION_ERROR_EXAMPLE = """
            {
               "status": "BAD_REQUEST",
               "timestamp": "26-02-2025 15:28:55",
               "errorMessage": [
                 {
                   "field": "email",
                   "message": "Invalid email format"
                 }
               ]
             }
            """;
    public static final String COMMON_ERROR_EXAMPLE = """
            {
               "status": "BAD_REQUEST",
               "timestamp": "01-01-2023 12:00:00",
               "errorMessage": "Invalid request parameters"
             }
            """;
    public static final String UNAUTHORIZED_ERROR_EXAMPLE = """
            {
               "status": "UNAUTHORIZED",
               "timestamp": "02-03-2025 19:10:07",
               "errorMessage": "Access Denied: Full authentication is required to access this
               resource"
             }
            """;
    public static final String NOT_FOUND_ERROR_EXAMPLE = """
            {
               "status": "NOT_FOUND",
               "timestamp": "02-03-2025 19:10:07",
               "errorMessage": "User not found with email: example@example.com"
             }
            """;
    public static final String INTERNAL_SERVER_ERROR_ERROR_EXAMPLE = """
            {
               "status": "INTERNAL_SERVER_ERROR",
               "timestamp": "02-03-2025 19:10:07",
               "errorMessage": "Internal server error"
             }
            """;
}
