package com.chertiavdev.bookingapp.service.impl;

import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.mapper.AmenityCategoryMapper;
import com.chertiavdev.bookingapp.model.AmenityCategory;
import com.chertiavdev.bookingapp.repository.AmenityCategoryRepository;
import com.chertiavdev.bookingapp.service.AmenityCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AmenityCategoryServiceImpl implements AmenityCategoryService {
    private final AmenityCategoryRepository amenityCategoryRepository;
    private final AmenityCategoryMapper amenityCategoryMapper;

    @Transactional
    @Override
    public AmenityCategoryDto save(CreateAmenityCategoryRequestDto requestDto) {
        AmenityCategory amenityCategory = amenityCategoryMapper.toModel(requestDto);
        return amenityCategoryMapper.toDto(amenityCategoryRepository.save(amenityCategory));
    }

    @Override
    public List<AmenityCategoryDto> findAll() {
        return amenityCategoryRepository.findAll().stream()
                .map(amenityCategoryMapper::toDto)
                .toList();
    }

    @Override
    public AmenityCategoryDto findById(Long id) {
        return amenityCategoryRepository.findById(id)
                .map(amenityCategoryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Can't find amenity category by id: "
                        + id));
    }

    @Transactional
    @Override
    public AmenityCategoryDto updateById(Long id, CreateAmenityCategoryRequestDto requestDto) {
        AmenityCategory amenityCategory = amenityCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't update category by id: "
                        + id));
        amenityCategoryMapper.updateFromDto(requestDto, amenityCategory);
        return amenityCategoryMapper.toDto(amenityCategoryRepository.save(amenityCategory));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        amenityCategoryRepository.deleteById(id);
    }
}
