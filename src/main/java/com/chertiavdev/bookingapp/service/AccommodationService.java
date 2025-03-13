package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;

public interface AccommodationService {
    AccommodationDto save(CreateAccommodationRequestDto requestDto);
}
