package com.chertiavdev.bookingapp.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "DTO for login requests containing user credentials")
public class UserLoginRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(
            description = "The email address of the user",
            example = "example@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Length(min = 8, max = 35,
            message = "Password must be between 8 and 35 characters")
    @Schema(
            description = "The password of the user. Must be between 8 and 35 characters long.",
            example = "strongPassword123*",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @ToString.Exclude
    private String password;
}
