package com.chertiavdev.bookingapp.data.builders;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.AMENITY_AIR_CONDITIONING_HEATING;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.AMENITY_FREE_WIFI;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.AMENITY_TELEVISION;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.AMENITY_UPDATED_NAME;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_3;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestAmenity;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestAmenityRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.mapAmenityToDto;

import com.chertiavdev.bookingapp.dto.amenity.AmenityDto;
import com.chertiavdev.bookingapp.dto.amenity.CreateAmenityRequestDto;
import com.chertiavdev.bookingapp.model.Amenity;
import com.chertiavdev.bookingapp.model.AmenityCategory;
import java.util.List;
import java.util.Set;
import lombok.Getter;

@Getter
public class AmenityTestDataBuilder {
    private final AmenityCategory amenityCategoryBasicAmenities;
    private final AmenityCategory amenityCategoryComfortAndConvenience;
    
    private final Amenity amenityFreeWiFi;
    private final Amenity amenityAirConditioning;
    private final Amenity amenityTelevision;
    private final Amenity updatedAmenityFreeWiFi;

    private final CreateAmenityRequestDto amenityFreeWiFiRequestDto;
    private final CreateAmenityRequestDto updatedAmenityFreeWiFiRequestDto;

    private final Amenity amenityFreeWiFiToModel;

    private final AmenityDto amenityFreeWiFiDto;
    private final AmenityDto amenityAirConditioningDto;
    private final AmenityDto amenityTelevisionDto;
    private final AmenityDto updatedAmenityFreeWiFiDto;

    public AmenityTestDataBuilder(AmenityCategoryTestDataBuilder amenityCategoryTestDataBuilder) {
        this.amenityCategoryBasicAmenities = amenityCategoryTestDataBuilder
                .getAmenityCategoryBasicAmenities();
        this.amenityCategoryComfortAndConvenience = amenityCategoryTestDataBuilder
                .getAmenityCategoryComfortAndConvenience();

        this.amenityFreeWiFi = createAmenityFreeWiFi();
        this.amenityAirConditioning = createAmenityAirConditioning();
        this.amenityTelevision = createAmenityTelevision();
        this.updatedAmenityFreeWiFi = createUpdatedAmenityFreeWiFi();

        this.amenityFreeWiFiRequestDto = createAmenityFreeWiFiRequestDto();
        this.updatedAmenityFreeWiFiRequestDto = createUpdatedAmenityFreeWiFiRequestDto();

        this.amenityFreeWiFiToModel = createAmenityFreeWiFiToModel();

        this.amenityFreeWiFiDto = createAmenityFreeWiFiDto();
        this.amenityAirConditioningDto = createAmenityAirConditioningDto();
        this.amenityTelevisionDto = createAmenityTelevisionDto();
        this.updatedAmenityFreeWiFiDto = createUpdatedAmenityFreeWiFiDto();
    }

    public Set<Amenity> generateAmenityCollection() {
        return Set.of(amenityFreeWiFi, amenityAirConditioning, amenityTelevision);
    }

    public CreateAmenityRequestDto createAmenityFreeWiFiBadRequestDto() {
        return createTestAmenityRequestDto(
                "",
                amenityCategoryBasicAmenities.getId()
        );
    }

    public List<AmenityDto> buildAllAmenityDtosList() {
        return List.of(amenityFreeWiFiDto, amenityAirConditioningDto, amenityTelevisionDto);
    }

    private Amenity createAmenityFreeWiFi() {
        return createTestAmenity(
                SAMPLE_TEST_ID_1,
                AMENITY_FREE_WIFI,
                amenityCategoryBasicAmenities
        );
    }

    private Amenity createAmenityAirConditioning() {
        return createTestAmenity(
                SAMPLE_TEST_ID_2,
                AMENITY_AIR_CONDITIONING_HEATING,
                amenityCategoryBasicAmenities
        );
    }

    private Amenity createAmenityTelevision() {
        return createTestAmenity(
                SAMPLE_TEST_ID_3,
                AMENITY_TELEVISION,
                amenityCategoryComfortAndConvenience
        );
    }

    private Amenity createUpdatedAmenityFreeWiFi() {
        return createTestAmenity(
                SAMPLE_TEST_ID_1,
                AMENITY_UPDATED_NAME,
                amenityCategoryBasicAmenities
        );
    }

    private CreateAmenityRequestDto createAmenityFreeWiFiRequestDto() {
        return createTestAmenityRequestDto(
                AMENITY_FREE_WIFI,
                amenityCategoryBasicAmenities.getId()
        );
    }

    private CreateAmenityRequestDto createUpdatedAmenityFreeWiFiRequestDto() {
        return createTestAmenityRequestDto(
                AMENITY_UPDATED_NAME,
                amenityCategoryBasicAmenities.getId()
        );
    }

    private Amenity createAmenityFreeWiFiToModel() {
        return createTestAmenity(
                null,
                AMENITY_FREE_WIFI,
                amenityCategoryBasicAmenities
        );
    }

    private AmenityDto createAmenityFreeWiFiDto() {
        return mapAmenityToDto(amenityFreeWiFi);
    }

    private AmenityDto createAmenityAirConditioningDto() {
        return mapAmenityToDto(amenityAirConditioning);
    }

    private AmenityDto createAmenityTelevisionDto() {
        return mapAmenityToDto(amenityTelevision);
    }

    private AmenityDto createUpdatedAmenityFreeWiFiDto() {
        return mapAmenityToDto(updatedAmenityFreeWiFi);
    }
}
