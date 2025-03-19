package com.chertiavdev.bookingapp.dto.accommodation;

import static com.chertiavdev.bookingapp.model.Accommodation.Type;

import com.chertiavdev.bookingapp.validation.enumvalidator.EnumNamePattern;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "DTO for creating a new accommodation.")
public class CreateAccommodationRequestDto {
    @EnumNamePattern(regexp = "HOUSE|APARTMENT|CONDO|VACATION_HOME")
    @Schema(description = "Type status",
            example = "HOUSE",
            allowableValues = {"HOUSE", "APARTMENT", "CONDO", "VACATION_HOME"})
    private Type type;

    @NotNull(message = "Address location is mandatory")
    @Valid
    @Schema(description = "Address of the accommodation")
    private CreateAddressRequestDto location;

    @NotBlank(message = "Size of the accommodation is mandatory")
    @Schema(description = "Size of the accommodation (e.g., Studio, 1 Bedroom, 2 Bedroom, etc.)",
            example = "Studio")
    private String size;

    @NotEmpty(message = "Amenities list must not be null")
    @Schema(description = "A list of IDs representing the amenities associated with the "
            + "accommodation.",
            example = "[1, 2]")
    private List<Long> amenities;

    @NotNull(message = "Daily rate is mandatory")
    @Positive(message = "Daily rate must be greater than zero")
    @DecimalMax(value = "10000.0", message = "Daily rate must not exceed $10,000")
    @Schema(description = "Price per day in $USD",
            example = "75.50")
    private BigDecimal dailyRate;

    @NotNull(message = "Availability is mandatory")
    @PositiveOrZero(message = "Availability must not be negative")
    @Schema(description = "Available accommodation units for booking",
            example = "1")
    private Integer availability;
}
