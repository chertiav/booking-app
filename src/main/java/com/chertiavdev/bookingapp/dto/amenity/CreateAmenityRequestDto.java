package com.chertiavdev.bookingapp.dto.amenity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Schema(description = "DTO for creating an new amenity")
@Data
public class CreateAmenityRequestDto {
    @NotBlank(message = "Name of the amenity is mandatory")
    @Schema(description = "Name of the amenity", example = "Wi-Fi")
    private String name;
    @Positive(message = "categoryId must be a positive number")
    @Schema(description = "Unique identifier of the amenity category", example = "1")
    private Long categoryId;
}
