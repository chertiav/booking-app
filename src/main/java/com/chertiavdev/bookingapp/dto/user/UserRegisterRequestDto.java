package com.chertiavdev.bookingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserRegisterRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

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
    private String password;

    private String repeatPassword;
}
