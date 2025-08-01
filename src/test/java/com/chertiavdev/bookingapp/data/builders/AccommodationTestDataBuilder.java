package com.chertiavdev.bookingapp.data.builders;

import static com.chertiavdev.bookingapp.model.Accommodation.Type.APARTMENT;
import static com.chertiavdev.bookingapp.model.Accommodation.Type.HOUSE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACCOMMODATION_AVAILABILITY;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACCOMMODATION_DAILY_RATE_10050;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACCOMMODATION_DAILY_RATE_7550;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACCOMMODATION_DEFAULT_AMENITIES;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACCOMMODATION_SIZE_STUDIO;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ADDRESS_APARTMENT_NUMBER_25;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ADDRESS_APARTMENT_NUMBER_26;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ADDRESS_CITY_KYIV;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ADDRESS_HOUSE_NUMBER_15B;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ADDRESS_STREET_KHRESHCHATYK;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.AVAILABILITY_THRESHOLD;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createPage;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createPageResponse;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestAccommodation;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestAccommodationRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestAddress;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestAddressRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.generateAccommodationExistsMessage;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.mapAccommodationToDto;

import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAddressRequestDto;
import com.chertiavdev.bookingapp.dto.page.PageResponse;
import com.chertiavdev.bookingapp.model.Accommodation;
import com.chertiavdev.bookingapp.model.Accommodation.Type;
import com.chertiavdev.bookingapp.model.Address;
import com.chertiavdev.bookingapp.model.Amenity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
public class AccommodationTestDataBuilder {
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final Set<Amenity> amenitySet;

    private final Accommodation pendingAccommodation;
    private final Accommodation confirmedAccommodation;
    private final Accommodation updatedPendingAccommodation;

    private final CreateAddressRequestDto addressPendingAccommodationRequestDto;
    private final CreateAddressRequestDto addressConfirmedAccommodationRequestDto;

    private final CreateAccommodationRequestDto pendingAccommodationRequestDto;
    private final CreateAccommodationRequestDto confirmedAccommodationRequestDto;
    private final CreateAccommodationRequestDto updatedPendingAccommodationRequestDto;

    private final Accommodation pendingAccommodationToModel;
    private final Accommodation confirmedAccommodationToModel;

    private final AccommodationDto pendingAccommodationDto;
    private final AccommodationDto confirmedAccommodationDto;
    private final AccommodationDto updatedPendingAccommodationDto;

    private final Pageable pageable;

    public AccommodationTestDataBuilder(AmenityTestDataBuilder amenityTestDataBuilder) {
        this.amenitySet = amenityTestDataBuilder.generateAmenityCollection();

        this.pendingAccommodation = createPendingAccommodation();
        this.confirmedAccommodation = createConfirmedAccommodation();
        this.updatedPendingAccommodation = createUpdatedPendingAccommodation();

        this.addressPendingAccommodationRequestDto = createAddressPendingAccommodationRequestDto();
        this.addressConfirmedAccommodationRequestDto =
                createAddressConfirmedAccommodationRequestDto();

        this.pendingAccommodationRequestDto = createPendingAccommodationRequestDto();
        this.confirmedAccommodationRequestDto = createConfirmedAccommodationRequestDto();
        this.updatedPendingAccommodationRequestDto = createUpdatedPendingAccommodationRequestDto();

        this.pendingAccommodationToModel = createPendingAccommodationToModel();
        this.confirmedAccommodationToModel = createConfirmedAccommodationToModel();

        this.pendingAccommodationDto = createPendingAccommodationDto();
        this.confirmedAccommodationDto = createConfirmedAccommodationDto();
        this.updatedPendingAccommodationDto = createUpdatedPendingAccommodationDto();

        this.pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE);
    }

    public Page<Accommodation> buildPendingAccommodationPage() {
        return createPage(List.of(pendingAccommodation), pageable);
    }

    public Page<AccommodationDto> buildPendingAccommodationDtoPage() {
        return createPage(List.of(pendingAccommodationDto), pageable);
    }

    public PageResponse<AccommodationDto> buildAvailableAccommodationDtoPageResponse() {
        return createPageResponse(List.of(pendingAccommodationDto), pageable);
    }

    public Page<Accommodation> buildEmptyAccommodationPage() {
        return createPage(List.of(), pageable);
    }

    public Page<AccommodationDto> buildEmptyAccommodationDtoPage() {
        return createPage(List.of(), pageable);
    }

    public String buildEExistsMessage(CreateAccommodationRequestDto requestDto) {
        return generateAccommodationExistsMessage(requestDto);
    }

    public CreateAccommodationRequestDto createPendingAccommodationBadRequestDto() {
        return createTestAccommodationRequestDto(
                null,
                addressPendingAccommodationRequestDto,
                ACCOMMODATION_SIZE_STUDIO,
                ACCOMMODATION_DEFAULT_AMENITIES,
                ACCOMMODATION_DAILY_RATE_7550,
                ACCOMMODATION_AVAILABILITY
        );
    }

    private Accommodation createPendingAccommodation() {
        return createAccommodation(
                SAMPLE_TEST_ID_1,
                Type.HOUSE,
                ACCOMMODATION_SIZE_STUDIO,
                ACCOMMODATION_DAILY_RATE_7550,
                amenitySet,
                ACCOMMODATION_AVAILABILITY,
                SAMPLE_TEST_ID_1,
                ADDRESS_APARTMENT_NUMBER_25
        );
    }

    private Accommodation createConfirmedAccommodation() {
        return createAccommodation(
                SAMPLE_TEST_ID_2,
                Type.APARTMENT,
                ACCOMMODATION_SIZE_STUDIO,
                ACCOMMODATION_DAILY_RATE_10050,
                amenitySet,
                AVAILABILITY_THRESHOLD,
                SAMPLE_TEST_ID_2,
                ADDRESS_APARTMENT_NUMBER_26
        );
    }

    private Accommodation createUpdatedPendingAccommodation() {
        return createAccommodation(
                SAMPLE_TEST_ID_1,
                Type.APARTMENT,
                ACCOMMODATION_SIZE_STUDIO,
                ACCOMMODATION_DAILY_RATE_7550,
                amenitySet,
                ACCOMMODATION_AVAILABILITY,
                SAMPLE_TEST_ID_1,
                ADDRESS_APARTMENT_NUMBER_25
        );
    }

    private CreateAddressRequestDto createAddressPendingAccommodationRequestDto() {
        return createTestAddressRequestDto(
                ADDRESS_CITY_KYIV,
                ADDRESS_STREET_KHRESHCHATYK,
                ADDRESS_HOUSE_NUMBER_15B,
                ADDRESS_APARTMENT_NUMBER_25
        );
    }

    private CreateAddressRequestDto createAddressConfirmedAccommodationRequestDto() {
        return createTestAddressRequestDto(
                ADDRESS_STREET_KHRESHCHATYK,
                ADDRESS_CITY_KYIV,
                ADDRESS_HOUSE_NUMBER_15B,
                ADDRESS_APARTMENT_NUMBER_26
        );
    }

    private CreateAccommodationRequestDto createPendingAccommodationRequestDto() {
        return createTestAccommodationRequestDto(
                HOUSE,
                addressPendingAccommodationRequestDto,
                ACCOMMODATION_SIZE_STUDIO,
                ACCOMMODATION_DEFAULT_AMENITIES,
                ACCOMMODATION_DAILY_RATE_7550,
                ACCOMMODATION_AVAILABILITY
        );
    }

    private CreateAccommodationRequestDto createConfirmedAccommodationRequestDto() {
        return createTestAccommodationRequestDto(
                APARTMENT,
                addressConfirmedAccommodationRequestDto,
                ACCOMMODATION_SIZE_STUDIO,
                ACCOMMODATION_DEFAULT_AMENITIES,
                ACCOMMODATION_DAILY_RATE_10050,
                AVAILABILITY_THRESHOLD
        );
    }

    private CreateAccommodationRequestDto createUpdatedPendingAccommodationRequestDto() {
        return createTestAccommodationRequestDto(
                APARTMENT,
                addressPendingAccommodationRequestDto,
                ACCOMMODATION_SIZE_STUDIO,
                ACCOMMODATION_DEFAULT_AMENITIES,
                ACCOMMODATION_DAILY_RATE_7550,
                ACCOMMODATION_AVAILABILITY
        );
    }

    private AccommodationDto createPendingAccommodationDto() {
        return mapAccommodationToDto(pendingAccommodation);
    }

    private AccommodationDto createConfirmedAccommodationDto() {
        return mapAccommodationToDto(confirmedAccommodation);
    }

    private AccommodationDto createUpdatedPendingAccommodationDto() {
        return mapAccommodationToDto(updatedPendingAccommodation);
    }

    private Accommodation createPendingAccommodationToModel() {
        return createAccommodation(
                null,
                Type.HOUSE,
                ACCOMMODATION_SIZE_STUDIO,
                ACCOMMODATION_DAILY_RATE_7550,
                amenitySet,
                ACCOMMODATION_AVAILABILITY,
                SAMPLE_TEST_ID_1,
                ADDRESS_APARTMENT_NUMBER_25
        );
    }

    private Accommodation createConfirmedAccommodationToModel() {
        return createAccommodation(
                null,
                APARTMENT,
                ACCOMMODATION_SIZE_STUDIO,
                ACCOMMODATION_DAILY_RATE_10050,
                amenitySet,
                AVAILABILITY_THRESHOLD,
                SAMPLE_TEST_ID_2,
                ADDRESS_APARTMENT_NUMBER_26
        );
    }

    private Accommodation createAccommodation(
            Long accommodationId, Type accommodationType, String size,
            BigDecimal dailyRate, Set<Amenity> amenities, Integer accommodationAvailability,
            Long addressId, String addressApartmentNumber
    ) {
        return createTestAccommodation(
                accommodationId,
                accommodationType,
                size,
                createAddress(addressId, addressApartmentNumber),
                amenities,
                dailyRate,
                accommodationAvailability
        );
    }

    private Address createAddress(Long id, String apartmentNumber) {
        return createTestAddress(
                id,
                ADDRESS_CITY_KYIV,
                ADDRESS_STREET_KHRESHCHATYK,
                ADDRESS_HOUSE_NUMBER_15B,
                apartmentNumber
        );
    }
}
