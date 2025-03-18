package com.chertiavdev.bookingapp.dto.amenity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "DTO representing details of an amenity.")
@Data
public class AmenityDto {
    @Schema(description = "Unique identifier of the amenity", example = "1")
    private Long id;
    @Schema(description = "Name of the amenity", example = "Free Wi-Fi")
    private String name;
    @Schema(description = "Unique identifier of the amenity category", example = "1")
    private Long categoryId;
}
