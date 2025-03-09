package com.chertiavdev.bookingapp.dto.user;

import com.chertiavdev.bookingapp.validation.FieldMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@FieldMatch(
        firstField = "password",
        secondField = "repeatPassword",
        message = "Passwords do not match")
@Schema(description = "DTO for registering a new user")
@Data
public class UserRegisterRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "The user's email address",
            example = "example@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "First name is required")
    @Schema(
            description = "The user's first name",
            example = "John",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String firstName;

    @Schema(
            description = "The user's last name",
            example = "Doe",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Password is required")
    @Length(min = 8, max = 35,
            message = "Password must be between 8 and 35 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&*+=]).{8,35}$",
            message = "Password must be between 8 and 35 characters long and "
                    + "contain at least one digit, one lowercase letter, "
                    + "one uppercase letter, and one special character."
    )
    @Schema(
            description = """
                         The user's password. The password must be between 8 and 35 characters long
                         and must include at least one digit, one lowercase letter,
                         one uppercase letter, and one special character.
                         """,
            example = "strongPassword123*",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @ToString.Exclude
    private String password;

    @Schema(
            description = "Confirmation of the password to ensure they match",
            example = "strongPassword123*",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @ToString.Exclude
    private String repeatPassword;
}
