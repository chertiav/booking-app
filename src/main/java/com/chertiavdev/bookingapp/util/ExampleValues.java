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
}
