package com.chertiavdev.bookingapp.data.builders;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_CATEGORY_BASIC_AMENITIES;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_CATEGORY_COMFORT_CONVENIENCE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_CATEGORY_UPDATED_NAME;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestAmenityCategory;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestAmenityCategoryDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestAmenityCategoryRequest;

import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import com.chertiavdev.bookingapp.model.AmenityCategory;
import java.util.List;
import lombok.Getter;

@Getter
public class AmenityCategoryTestDataBuilder {
    private final AmenityCategory amenityCategoryBasicAmenities;
    private final AmenityCategory amenityCategoryComfortAndConvenience;
    private final AmenityCategory updatedAmenityCategoryBasicAmenities;

    private final CreateAmenityCategoryRequestDto amenityCategoryBasicAmenitiesRequestDto;
    private final CreateAmenityCategoryRequestDto amenityCategoryComfortAndConvenienceRequestDto;
    private final CreateAmenityCategoryRequestDto updatedAmenityCategoryBasicAmenitiesRequestDto;

    private final AmenityCategory amenityCategoryBasicToModel;
    private final AmenityCategory amenityCategoryComfortToModel;

    private final AmenityCategoryDto amenityCategoryBasicAmenitiesDto;
    private final AmenityCategoryDto amenityCategoryComfortAndConvenienceDto;
    private final AmenityCategoryDto updatedAmenityCategoryBasicAmenitiesDto;

    public AmenityCategoryTestDataBuilder() {
        this.amenityCategoryBasicAmenities = createAmenityCategoryBasicAmenities();
        this.amenityCategoryComfortAndConvenience = createAmenityCategoryComfortAndConvenience();
        this.updatedAmenityCategoryBasicAmenities = createUpdatedAmenityCategoryBasicAmenities();

        this.amenityCategoryBasicAmenitiesRequestDto =
                createAmenityCategoryBasicAmenitiesRequestDto();
        this.amenityCategoryComfortAndConvenienceRequestDto =
                createAmenityCategoryComfortAndConvenienceRequestDto();
        this.updatedAmenityCategoryBasicAmenitiesRequestDto =
                createUpdatedAmenityCategoryBasicAmenitiesRequestDto();

        this.amenityCategoryBasicToModel = createAmenityCategoryBasicAmenitiesToModel();
        this.amenityCategoryComfortToModel = createAmenityCategoryComfortAndConvenienceToModel();

        this.amenityCategoryBasicAmenitiesDto = createAmenityCategoryBasicAmenitiesDto();
        this.amenityCategoryComfortAndConvenienceDto =
                createAmenityCategoryComfortAndConvenienceDto();
        this.updatedAmenityCategoryBasicAmenitiesDto =
                createUpdatedAmenityCategoryBasicAmenitiesDto();
    }

    public List<AmenityCategory> buildAllAmenityCategoryList() {
        return List.of(amenityCategoryBasicAmenities, amenityCategoryComfortAndConvenience);
    }

    public List<AmenityCategoryDto> buildAllAmenityCategoryDtosList() {
        return List.of(amenityCategoryBasicAmenitiesDto, amenityCategoryComfortAndConvenienceDto);
    }

    private AmenityCategory createAmenityCategoryBasicAmenities() {
        return createTestAmenityCategory(SAMPLE_TEST_ID_1, AMENITY_CATEGORY_BASIC_AMENITIES);
    }

    private AmenityCategory createAmenityCategoryComfortAndConvenience() {
        return createTestAmenityCategory(SAMPLE_TEST_ID_2, AMENITY_CATEGORY_COMFORT_CONVENIENCE);
    }

    private AmenityCategory createUpdatedAmenityCategoryBasicAmenities() {
        return createTestAmenityCategory(SAMPLE_TEST_ID_1, AMENITY_CATEGORY_UPDATED_NAME);
    }

    private AmenityCategory createAmenityCategoryBasicAmenitiesToModel() {
        return createTestAmenityCategory(null, AMENITY_CATEGORY_BASIC_AMENITIES);
    }

    private AmenityCategory createAmenityCategoryComfortAndConvenienceToModel() {
        return createTestAmenityCategory(null, AMENITY_CATEGORY_COMFORT_CONVENIENCE);
    }

    private CreateAmenityCategoryRequestDto createAmenityCategoryBasicAmenitiesRequestDto() {
        return createTestAmenityCategoryRequest(AMENITY_CATEGORY_BASIC_AMENITIES);
    }

    private CreateAmenityCategoryRequestDto createUpdatedAmenityCategoryBasicAmenitiesRequestDto() {
        return createTestAmenityCategoryRequest(AMENITY_CATEGORY_UPDATED_NAME);
    }

    private CreateAmenityCategoryRequestDto createAmenityCategoryComfortAndConvenienceRequestDto() {
        return createTestAmenityCategoryRequest(AMENITY_CATEGORY_COMFORT_CONVENIENCE);
    }

    private AmenityCategoryDto createAmenityCategoryBasicAmenitiesDto() {
        return createTestAmenityCategoryDto(SAMPLE_TEST_ID_1, AMENITY_CATEGORY_BASIC_AMENITIES);
    }

    private AmenityCategoryDto createAmenityCategoryComfortAndConvenienceDto() {
        return createTestAmenityCategoryDto(SAMPLE_TEST_ID_2, AMENITY_CATEGORY_COMFORT_CONVENIENCE);
    }

    private AmenityCategoryDto createUpdatedAmenityCategoryBasicAmenitiesDto() {
        return createTestAmenityCategoryDto(SAMPLE_TEST_ID_1, AMENITY_CATEGORY_UPDATED_NAME);
    }
}
