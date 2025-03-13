package com.chertiavdev.bookingapp.service.impl;

import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.chertiavdev.bookingapp.exception.AccommodationAlreadyExistsException;
import com.chertiavdev.bookingapp.mapper.AccommodationMapper;
import com.chertiavdev.bookingapp.model.Accommodation;
import com.chertiavdev.bookingapp.repository.AccommodationRepository;
import com.chertiavdev.bookingapp.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;

    @Override
    public AccommodationDto save(CreateAccommodationRequestDto requestDto) {
        validateAccommodationUniqueness(requestDto);
        Accommodation accommodation = accommodationMapper.toModel(requestDto);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    private void validateAccommodationUniqueness(CreateAccommodationRequestDto requestDto) {
        if (accommodationRepository.existsByLocationAndTypeAndSize(
                requestDto.getLocation().getCity(),
                requestDto.getLocation().getStreet(),
                requestDto.getLocation().getHouseNumber(),
                requestDto.getLocation().getApartmentNumber(),
                requestDto.getType(),
                requestDto.getSize())
        ) {
            throw new AccommodationAlreadyExistsException(
                    generateAccommodationExistsMessage(requestDto)
            );
        }
    }

    private String generateAccommodationExistsMessage(CreateAccommodationRequestDto requestDto) {
        return String.format("Accommodation with the same city: %s, street: %s, house number: %s, "
                        + "apartment number: %s, type: %s, and size: %s already exists.",
                requestDto.getLocation().getCity(),
                requestDto.getLocation().getStreet(),
                requestDto.getLocation().getHouseNumber(),
                requestDto.getLocation().getApartmentNumber(),
                requestDto.getType(),
                requestDto.getSize()
        );
    }
}
