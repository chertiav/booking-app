package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import com.chertiavdev.bookingapp.model.AmenityCategory;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface AmenityCategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    AmenityCategory toModel(CreateAmenityCategoryRequestDto requestDto);

    AmenityCategoryDto toDto(AmenityCategory amenityCategory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateFromDto(
            CreateAmenityCategoryRequestDto requestDto,
            @MappingTarget AmenityCategory amenityCategory
    );

    @Named("amenityCategoryById")
    default AmenityCategory amenityCategoryDto(Long id) {
        return Optional.ofNullable(id)
                .map(AmenityCategory::new)
                .orElse(new AmenityCategory());
    }
}
