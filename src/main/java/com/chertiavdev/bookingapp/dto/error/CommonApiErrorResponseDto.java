package com.chertiavdev.bookingapp.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

@Schema(description = "Common format for API responses")
public record CommonApiErrorResponseDto(
        @Schema(
                description = "HTTP status of the response",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        HttpStatus status,
        @Schema(
                description = """
                        Timestamp when the response was generated,
                        formatted as 'dd-MM-yyyy HH:mm:ss'
                        """,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime timestamp,
        @Schema(
                description = """
                          Detailed error message in case of response failure.
                          Can contain a string or structured error details.
                          """
        )
        Object errorMessage
) {
}
