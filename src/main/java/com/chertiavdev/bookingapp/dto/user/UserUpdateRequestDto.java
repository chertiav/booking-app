package com.chertiavdev.bookingapp.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "DTO for updating user's data")
@Data
public class UserUpdateRequestDto {
    @NotBlank(message = "First name is required")
    @Schema(
            description = "The user's first name for updating",
            example = "John",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String firstName;

    @Schema(
            description = "The user's last name for updating",
            example = "Snow",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Last name is required")
    private String lastName;
}
