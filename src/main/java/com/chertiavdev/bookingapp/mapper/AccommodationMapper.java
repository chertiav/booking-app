package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.chertiavdev.bookingapp.mapper.amenity.AmenityListToSetMapper;
import com.chertiavdev.bookingapp.mapper.amenity.AmenityMapping;
import com.chertiavdev.bookingapp.model.Accommodation;
import com.chertiavdev.bookingapp.model.Amenity;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = {AmenityListToSetMapper.class, AddressMapper.class})
public interface AccommodationMapper {
    @Mapping(target = "amenitiesIds", ignore = true)
    @Mapping(target = "location", source = "location",
            qualifiedByName = "mapAddressToString")
    AccommodationDto toDto(Accommodation accommodation);

    @AfterMapping
    default void setAmenitiesIds(
            @MappingTarget AccommodationDto accommodationDto,
            Accommodation accommodation) {
        Set<Long> amenitiesIds = accommodation.getAmenities().stream()
                .map(Amenity::getId)
                .collect(Collectors.toSet());
        accommodationDto.setAmenitiesIds(amenitiesIds);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "amenities", source = "amenities", qualifiedBy = AmenityMapping.class)
    @Mapping(target = "location", source = "location",
            qualifiedByName = "mapAddressRequestDtoToModel")
    Accommodation toModel(CreateAccommodationRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "location", source = "location")
    @Mapping(target = "amenities", source = "amenities", qualifiedBy = AmenityMapping.class)
    void updateAccommodationFromDto(
            CreateAccommodationRequestDto requestDto,
            @MappingTarget Accommodation accommodation);

    @Named("accommodationById")
    default Accommodation accommodationById(Long id) {
        return Optional.ofNullable(id)
                .map(Accommodation::new)
                .orElse(new Accommodation());
    }
}
