package com.chertiavdev.bookingapp.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents details of a specific error.")
public class ErrorDetailDto {
    @Schema(
            description = "The name of the field where the error occurred.",
            example = "email"
    )
    private String field;
    @Schema(
            description = "A descriptive error message for the field.",
            example = "Invalid email format."
    )
    private String message;
}
