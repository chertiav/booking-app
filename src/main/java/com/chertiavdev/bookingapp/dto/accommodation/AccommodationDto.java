package com.chertiavdev.bookingapp.dto.accommodation;

import static com.chertiavdev.bookingapp.model.Accommodation.Type;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

@Schema(description = "DTO representing details of an accommodation.")
@Data
public class AccommodationDto {
    @Schema(description = "Unique identifier of the accommodation", example = "1")
    private Long id;
    @Schema(description = "Type of the accommodation", example = "HOUSE")
    private Type type;
    @Schema(description = "Address of the accommodation", example = "Khreshchatyk 15B, 25, Kyiv")
    private String location;
    @Schema(description = "Size of the accommodation", example = "Studio")
    private String size;
    @Schema(description = "List of IDs representing the amenities associated with the "
            + "accommodation", example = "[1, 2]")
    private Set<Long> amenitiesIds;
    @Schema(description = "Price per day in $USD", example = "75.50")
    private BigDecimal dailyRate;
    @Schema(description = "Available accommodation units for booking", example = "1")
    private Integer availability;
}
