package com.chertiavdev.bookingapp.dto.amenity.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO for creating an new amenities category")
public class CreateAmenityCategoryRequestDto {
    @NotBlank(message = "Category is mandatory")
    @Schema(description = "Name of the category", example = "New category")
    private String name;
}
