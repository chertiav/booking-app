package com.chertiavdev.bookingapp.dto.amenity.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "DTO representing details of an amenities category.")
@Data
public class AmenityCategoryDto {
    @Schema(description = "Unique identifier of the accommodation", example = "1")
    private Long id;
    @Schema(description = "Type of the category", example = "Basic Amenities")
    private String name;
}
