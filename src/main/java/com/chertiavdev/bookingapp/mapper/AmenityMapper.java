package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.dto.amenity.AmenityDto;
import com.chertiavdev.bookingapp.dto.amenity.CreateAmenityRequestDto;
import com.chertiavdev.bookingapp.model.Amenity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = {AmenityCategoryMapper.class})
public interface AmenityMapper {
    @Mapping(target = "category", source = "requestDto.categoryId",
            qualifiedByName = "amenityCategoryById")
    Amenity toModel(CreateAmenityRequestDto requestDto);

    @Mapping(target = "categoryId", source = "category.id")
    AmenityDto toDto(Amenity amenity);

    @Mapping(target = "category", source = "requestDto.categoryId",
            qualifiedByName = "amenityCategoryById")
    void update(
            CreateAmenityRequestDto requestDto,
            @MappingTarget Amenity amenity
    );
}
