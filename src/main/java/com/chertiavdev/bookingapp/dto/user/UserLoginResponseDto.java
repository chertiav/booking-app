package com.chertiavdev.bookingapp.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the authentication token for the user")
public record UserLoginResponseDto(
        @Schema(description = "JWT authentication token issued after successful login",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleGFtcGxlQGV4YW1wbGUuY29tIiwiaWF0Ijox"
                        + "NzQwNDkzOTU5LCJleHAiOjE3NDA0OTc1NTl9"
                        + ".drZ7iT198QnWjmxZRuT9ctXMRwYmVBZSjBlMQCwJ398")
        String token
) {
}
