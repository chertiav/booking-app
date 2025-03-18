package com.chertiavdev.bookingapp.service.impl;

import com.chertiavdev.bookingapp.dto.amenity.AmenityDto;
import com.chertiavdev.bookingapp.dto.amenity.CreateAmenityRequestDto;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.mapper.AmenityMapper;
import com.chertiavdev.bookingapp.model.Amenity;
import com.chertiavdev.bookingapp.repository.AmenityRepository;
import com.chertiavdev.bookingapp.service.AmenityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {
    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    @Transactional
    @Override
    public AmenityDto save(CreateAmenityRequestDto requestDto) {
        Amenity amenity = amenityMapper.toModel(requestDto);
        return amenityMapper.toDto(amenityRepository.save(amenity));
    }

    @Override
    public List<AmenityDto> findAll() {
        return amenityRepository.findAll().stream()
                .map(amenityMapper::toDto)
                .toList();
    }

    @Override
    public AmenityDto findById(Long id) {
        return amenityRepository.findById(id)
                .map(amenityMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Can't find amenity by id: " + id));
    }

    @Transactional
    @Override
    public AmenityDto updateById(Long id, CreateAmenityRequestDto requestDto) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't update amenity by id: "
                        + id));
        amenityMapper.update(requestDto, amenity);
        return amenityMapper.toDto(amenityRepository.save(amenity));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        amenityRepository.deleteById(id);
    }
}
