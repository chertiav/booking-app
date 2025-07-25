package com.chertiavdev.bookingapp.data.builders;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_CATEGORIES_DEFAULT_NAMES;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_CATEGORY_COMFORT_CONVENIENCE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_CATEGORY_NEW_AMENITIES;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.AMENITY_CATEGORY_UPDATED_NAME;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_11;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestAmenityCategory;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestAmenityCategoryDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestAmenityCategoryRequest;

import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import com.chertiavdev.bookingapp.model.AmenityCategory;
import java.util.List;
import java.util.stream.IntStream;
import lombok.Getter;

@Getter
public class AmenityCategoryTestDataBuilder {
    private static final String NAME_BAD_REQUEST = "";

    private final AmenityCategory amenityCategoryNewAmenities;
    private final AmenityCategory amenityCategoryComfortAndConvenience;
    private final AmenityCategory updatedAmenityCategoryNewAmenities;

    private final CreateAmenityCategoryRequestDto amenityCategoryNewAmenitiesRequestDto;
    private final CreateAmenityCategoryRequestDto amenityCategoryComfortAndConvenienceRequestDto;
    private final CreateAmenityCategoryRequestDto updatedAmenityCategoryNewAmenitiesRequestDto;

    private final AmenityCategory amenityCategoryBasicToModel;
    private final AmenityCategory amenityCategoryComfortToModel;

    private final AmenityCategoryDto amenityCategoryNewAmenitiesDto;
    private final AmenityCategoryDto amenityCategoryComfortAndConvenienceDto;
    private final AmenityCategoryDto updatedAmenityCategoryNewAmenitiesDto;

    public AmenityCategoryTestDataBuilder() {
        this.amenityCategoryNewAmenities = createAmenityCategoryNewAmenities();
        this.amenityCategoryComfortAndConvenience = createAmenityCategoryComfortAndConvenience();
        this.updatedAmenityCategoryNewAmenities = createUpdatedAmenityCategoryNewAmenities();

        this.amenityCategoryNewAmenitiesRequestDto =
                createAmenityCategoryNewAmenitiesRequestDto();
        this.amenityCategoryComfortAndConvenienceRequestDto =
                createAmenityCategoryComfortAndConvenienceRequestDto();
        this.updatedAmenityCategoryNewAmenitiesRequestDto =
                createUpdatedAmenityCategoryNewAmenitiesRequestDto();

        this.amenityCategoryBasicToModel = createAmenityCategoryNewAmenitiesToModel();
        this.amenityCategoryComfortToModel = createAmenityCategoryComfortAndConvenienceToModel();

        this.amenityCategoryNewAmenitiesDto = createAmenityCategoryNewAmenitiesDto();
        this.amenityCategoryComfortAndConvenienceDto =
                createAmenityCategoryComfortAndConvenienceDto();
        this.updatedAmenityCategoryNewAmenitiesDto =
                createUpdatedAmenityCategoryNewAmenitiesDto();
    }

    public List<AmenityCategory> buildAllAmenityCategoryList() {
        return List.of(amenityCategoryNewAmenities, amenityCategoryComfortAndConvenience);
    }

    public List<AmenityCategoryDto> buildAllAmenityCategoryDtosList() {
        return List.of(amenityCategoryNewAmenitiesDto, amenityCategoryComfortAndConvenienceDto);
    }

    public CreateAmenityCategoryRequestDto createAmenityCategoryNewAmenitiesBatRequestDto() {
        return createTestAmenityCategoryRequest(NAME_BAD_REQUEST);
    }

    public List<AmenityCategoryDto> buildAmenityCategoryDtosAllList() {
        return IntStream.range(0, AMENITY_CATEGORIES_DEFAULT_NAMES.size())
                .mapToObj(index -> createTestAmenityCategoryDto(
                        (long) index + 1L,
                        AMENITY_CATEGORIES_DEFAULT_NAMES.get(index)))
                .toList();
    }

    private AmenityCategory createAmenityCategoryNewAmenities() {
        return createTestAmenityCategory(SAMPLE_TEST_ID_11, AMENITY_CATEGORY_NEW_AMENITIES);
    }

    private AmenityCategory createAmenityCategoryComfortAndConvenience() {
        return createTestAmenityCategory(SAMPLE_TEST_ID_2, AMENITY_CATEGORY_COMFORT_CONVENIENCE);
    }

    private AmenityCategory createUpdatedAmenityCategoryNewAmenities() {
        return createTestAmenityCategory(SAMPLE_TEST_ID_11, AMENITY_CATEGORY_UPDATED_NAME);
    }

    private AmenityCategory createAmenityCategoryNewAmenitiesToModel() {
        return createTestAmenityCategory(null, AMENITY_CATEGORY_NEW_AMENITIES);
    }

    private AmenityCategory createAmenityCategoryComfortAndConvenienceToModel() {
        return createTestAmenityCategory(null, AMENITY_CATEGORY_COMFORT_CONVENIENCE);
    }

    private CreateAmenityCategoryRequestDto createAmenityCategoryNewAmenitiesRequestDto() {
        return createTestAmenityCategoryRequest(AMENITY_CATEGORY_NEW_AMENITIES);
    }

    private CreateAmenityCategoryRequestDto createUpdatedAmenityCategoryNewAmenitiesRequestDto() {
        return createTestAmenityCategoryRequest(AMENITY_CATEGORY_UPDATED_NAME);
    }

    private CreateAmenityCategoryRequestDto createAmenityCategoryComfortAndConvenienceRequestDto() {
        return createTestAmenityCategoryRequest(AMENITY_CATEGORY_COMFORT_CONVENIENCE);
    }

    private AmenityCategoryDto createAmenityCategoryNewAmenitiesDto() {
        return createTestAmenityCategoryDto(SAMPLE_TEST_ID_11, AMENITY_CATEGORY_NEW_AMENITIES);
    }

    private AmenityCategoryDto createAmenityCategoryComfortAndConvenienceDto() {
        return createTestAmenityCategoryDto(SAMPLE_TEST_ID_2, AMENITY_CATEGORY_COMFORT_CONVENIENCE);
    }

    private AmenityCategoryDto createUpdatedAmenityCategoryNewAmenitiesDto() {
        return createTestAmenityCategoryDto(SAMPLE_TEST_ID_11, AMENITY_CATEGORY_UPDATED_NAME);
    }
}
