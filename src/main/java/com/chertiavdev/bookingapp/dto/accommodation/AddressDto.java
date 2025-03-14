package com.chertiavdev.bookingapp.dto.accommodation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "DTO representing address details of an accommodation.")
@Data
public class AddressDto {
    @Schema(description = "Unique identifier of the address", example = "1")
    private Long id;
    @Schema(description = "City where the accommodation is located", example = "Kyiv")
    private String city;
    @Schema(description = "Street name of the accommodation", example = "Khreshchatyk")
    private String street;
    @Schema(description = "House number of the accommodation", example = "15B")
    private String houseNumber;
    @Schema(description = "Apartment number in the building", example = "25")
    private String apartmentNumber;
}
