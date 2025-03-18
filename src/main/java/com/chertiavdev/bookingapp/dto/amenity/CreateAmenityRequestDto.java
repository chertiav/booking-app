package com.chertiavdev.bookingapp.dto.amenity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "DTO for creating an new amenity")
@Data
public class CreateAmenityRequestDto {
    @Schema(description = "Name of the amenity", example = "Wi-Fi")
    private String name;
    @Schema(description = "Unique identifier of the amenity category", example = "1")
    private Long categoryId;
}
