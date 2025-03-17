package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import com.chertiavdev.bookingapp.model.AmenityCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface AmenityCategoryMapper {
    AmenityCategory toModel(CreateAmenityCategoryRequestDto requestDto);

    AmenityCategoryDto toDto(AmenityCategory amenityCategory);

    void updateFromDto(
            CreateAmenityCategoryRequestDto requestDto,
            @MappingTarget AmenityCategory amenityCategory
    );
}
