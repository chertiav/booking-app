package com.chertiavdev.bookingapp.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO representing a user's data")
public class UserDto {
    @Schema(description = "Unique identifier of the user", example = "2")
    private Long id;
    @Schema(description = "Email address of the user", example = "example@example.com")
    private String email;
    @Schema(description = "First name of the user", example = "John")
    private String firstName;
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;
}
