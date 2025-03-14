package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    AccommodationDto save(CreateAccommodationRequestDto requestDto);

    Page<AccommodationDto> findAllAvailable(Pageable pageable);

    AccommodationDto findAvailableById(Long id);

    AccommodationDto updateById(Long id, CreateAccommodationRequestDto requestDto);

    void deleteById(Long id);
}
