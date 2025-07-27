package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAddressRequestDto;
import com.chertiavdev.bookingapp.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface AddressMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Address toModel(CreateAddressRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateFromDto(
            CreateAddressRequestDto dto,
            @MappingTarget Address entity
    );

    @Named("mapAddressRequestDtoToModel")
    default Address mapToModel(CreateAddressRequestDto requestDto) {
        return toModel(requestDto);
    }

    @Named("mapAddressToString")
    default String mapAddressToString(Address address) {
        return String.format("%s %s, %s, %s",
                address.getStreet(),
                address.getHouseNumber(),
                address.getApartmentNumber(),
                address.getCity());
    }
}
