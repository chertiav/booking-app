package com.chertiavdev.bookingapp.utils.helpers;

import static com.chertiavdev.bookingapp.model.Accommodation.Type.HOUSE;

import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAddressRequestDto;
import com.chertiavdev.bookingapp.dto.amenity.AmenityDto;
import com.chertiavdev.bookingapp.dto.amenity.CreateAmenityRequestDto;
import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import com.chertiavdev.bookingapp.model.Accommodation;
import com.chertiavdev.bookingapp.model.Address;
import com.chertiavdev.bookingapp.model.Amenity;
import com.chertiavdev.bookingapp.model.AmenityCategory;
import com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ServiceTestUtils {

    private ServiceTestUtils() {
    }

    //=======================Accommodation===========================================
    public static CreateAccommodationRequestDto createSampleAccommodationRequest() {
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto();
        requestDto.setType(HOUSE);
        requestDto.setLocation(createSampleAddressRequest());
        requestDto.setSize(ServiceTestConstants.ACCOMMODATION_SIZE);
        requestDto.setAmenities(ServiceTestConstants.ACCOMMODATION_DEFAULT_AMENITIES);
        requestDto.setDailyRate(ServiceTestConstants.ACCOMMODATION_DAILY_RATE);
        requestDto.setAvailability(ServiceTestConstants.ACCOMMODATION_AVAILABILITY);

        return requestDto;
    }

    public static Accommodation accommodationFromRequestDto(
            CreateAccommodationRequestDto requestDto
    ) {
        Accommodation accommodation = new Accommodation();
        accommodation.setType(requestDto.getType());
        accommodation.setLocation(addressFromRequestDto(requestDto.getLocation()));
        accommodation.setSize(requestDto.getSize());
        accommodation.setAmenities(getAmenitiesById(requestDto.getAmenities()));
        accommodation.setDailyRate(requestDto.getDailyRate());
        accommodation.setAvailability(requestDto.getAvailability());

        return accommodation;
    }

    public static AccommodationDto mapAccommodationToDto(Accommodation accommodation) {
        AccommodationDto accommodationDto = new AccommodationDto();
        accommodationDto.setId(accommodation.getId());
        accommodationDto.setType(accommodation.getType());
        accommodationDto.setLocation(mapAddressToString(accommodation.getLocation()));
        accommodationDto.setSize(accommodation.getSize());
        accommodationDto.setAmenitiesIds(getSetOfAmenitiesId(accommodation.getAmenities()));
        accommodationDto.setDailyRate(accommodation.getDailyRate());
        accommodationDto.setAvailability(accommodation.getAvailability());

        return accommodationDto;
    }

    public static String generateAccommodationExistsMessage(
            CreateAccommodationRequestDto requestDto
    ) {
        return String.format("Accommodation with the same city: %s, street: %s, house number: %s, "
                        + "apartment number: %s, type: %s, and size: %s already exists.",
                requestDto.getLocation().getCity(),
                requestDto.getLocation().getStreet(),
                requestDto.getLocation().getHouseNumber(),
                requestDto.getLocation().getApartmentNumber(),
                requestDto.getType(),
                requestDto.getSize()
        );
    }

    private static CreateAddressRequestDto createSampleAddressRequest() {
        CreateAddressRequestDto requestDto = new CreateAddressRequestDto();
        requestDto.setStreet(ServiceTestConstants.ADDRESS_STREET_KHRESHCHATYK);
        requestDto.setCity(ServiceTestConstants.ADDRESS_CITY_KYIV);
        requestDto.setHouseNumber(ServiceTestConstants.ADDRESS_HOUSE_NUMBER_15B);
        requestDto.setApartmentNumber(ServiceTestConstants.ADDRESS_APARTMENT_NUMBER_25);

        return requestDto;
    }

    private static Address addressFromRequestDto(CreateAddressRequestDto requestDto) {
        Address address = new Address();
        address.setStreet(requestDto.getStreet());
        address.setCity(requestDto.getCity());
        address.setHouseNumber(requestDto.getHouseNumber());
        address.setApartmentNumber(requestDto.getApartmentNumber());

        return address;
    }

    private static Set<Amenity> getAmenitiesById(List<Long> amenitiesIds) {
        return loadAllAmenity().stream()
                .filter(amenity -> amenitiesIds.contains(amenity.getId()))
                .collect(Collectors.toSet());
    }

    private static String mapAddressToString(Address address) {
        return String.format("%s %s, %s, %s",
                address.getStreet(),
                address.getHouseNumber(),
                address.getApartmentNumber(),
                address.getCity());
    }

    private static Set<Long> getSetOfAmenitiesId(Set<Amenity> amenities) {
        return amenities.stream()
                .map(Amenity::getId)
                .collect(Collectors.toSet());
    }

    //=======================Amenities===========================================
    public static Set<Amenity> loadAllAmenity() {
        return Set.of(
                createAmenity(1L, "Free Wi-Fi", 1L),
                createAmenity(2L, "Air Conditioning/Heating", 1L),
                createAmenity(3L, "Television", 2L)
        );
    }

    public static Amenity createAmenity(Long id, String name, Long categoryId) {
        Amenity amenity = new Amenity();
        amenity.setId(id);
        amenity.setName(name);
        amenity.setCategory(new AmenityCategory(categoryId));
        amenity.setDeleted(false);
        return amenity;
    }

    public static CreateAmenityRequestDto createSampleAmenityRequest() {
        CreateAmenityRequestDto requestDto = new CreateAmenityRequestDto();
        requestDto.setName(ServiceTestConstants.CATEGORY_NAME);
        requestDto.setCategoryId(ServiceTestConstants.SAMPLE_TEST_ID_1);
        return requestDto;
    }

    public static Amenity amenityFromRequestDto(
            CreateAmenityRequestDto requestDto
    ) {
        Amenity amenity = new Amenity();
        amenity.setName(requestDto.getName());
        amenity.setCategory(new AmenityCategory(requestDto.getCategoryId()));
        amenity.setDeleted(false);

        return amenity;
    }

    public static AmenityDto mapAmenityToDto(Amenity amenity) {
        AmenityDto amenityDto = new AmenityDto();
        amenityDto.setId(amenity.getId());
        amenityDto.setName(amenity.getName());
        amenityDto.setCategoryId(amenity.getCategory().getId());

        return amenityDto;
    }

    //=======================AmenityCategories===========================================
    public static CreateAmenityCategoryRequestDto createSampleAmenityCategoryRequest() {
        CreateAmenityCategoryRequestDto requestDto = new CreateAmenityCategoryRequestDto();
        requestDto.setName(ServiceTestConstants.AMENITY_CATEGORY_NAME);
        return requestDto;
    }

    public static AmenityCategory amenityCategoryFromRequestDto(
            CreateAmenityCategoryRequestDto requestDto
    ) {
        AmenityCategory amenityCategory = new AmenityCategory();
        amenityCategory.setName(requestDto.getName());
        amenityCategory.setDeleted(false);

        return amenityCategory;
    }

    public static AmenityCategoryDto mapAmenityCategorToDto(AmenityCategory amenityCategory) {
        AmenityCategoryDto amenityCategoryDto = new AmenityCategoryDto();
        amenityCategoryDto.setId(amenityCategory.getId());
        amenityCategoryDto.setName(amenityCategory.getName());

        return amenityCategoryDto;
    }

    //========================methods for all services======================================
    public static <T> Page<T> createPage(List<T> listOfObjects, Pageable pageable) {
        return new PageImpl<>(listOfObjects, pageable, listOfObjects.size());
    }
}
