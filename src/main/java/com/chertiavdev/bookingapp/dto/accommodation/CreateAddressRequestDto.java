package com.chertiavdev.bookingapp.dto.accommodation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "DTO for creating a new accommodations address.")
public class CreateAddressRequestDto {
    @NotBlank(message = "City is mandatory")
    @Size(max = 100, message = "City name must not exceed 100 characters")
    @Schema(description = "City where the accommodation is located", example = "Kyiv")
    private String city;

    @NotBlank(message = "Street is mandatory")
    @Size(max = 200, message = "Street name must not exceed 200 characters")
    @Schema(description = "Street where the accommodation is located", example = "Khreshchatyk")
    private String street;

    @NotBlank(message = "House number is mandatory")
    @Size(max = 10, message = "House number must not exceed 10 characters")
    @Schema(description = "Number of the house within the street", example = "15B")
    private String houseNumber;

    @Schema(description = "Apartment number within the building", example = "25")
    private String apartmentNumber;
}
