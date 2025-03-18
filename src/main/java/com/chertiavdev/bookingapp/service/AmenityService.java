package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.amenity.AmenityDto;
import com.chertiavdev.bookingapp.dto.amenity.CreateAmenityRequestDto;
import java.util.List;

public interface AmenityService {
    AmenityDto save(CreateAmenityRequestDto requestDto);

    List<AmenityDto> findAll();

    AmenityDto findById(Long id);

    AmenityDto updateById(Long id, CreateAmenityRequestDto requestDto);

    void deleteById(Long id);
}
