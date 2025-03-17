package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import java.util.List;

public interface AmenityCategoryService {
    AmenityCategoryDto save(CreateAmenityCategoryRequestDto requestDto);

    List<AmenityCategoryDto> findAll();

    AmenityCategoryDto findById(Long id);

    AmenityCategoryDto updateById(Long id, CreateAmenityCategoryRequestDto requestDto);

    void deleteById(Long id);
}
