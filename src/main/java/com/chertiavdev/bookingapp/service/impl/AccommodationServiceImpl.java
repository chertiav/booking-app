package com.chertiavdev.bookingapp.service.impl;

import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.chertiavdev.bookingapp.exception.AccommodationAlreadyExistsException;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.mapper.AccommodationMapper;
import com.chertiavdev.bookingapp.model.Accommodation;
import com.chertiavdev.bookingapp.repository.AccommodationRepository;
import com.chertiavdev.bookingapp.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private static final int AVAILABILITY_THRESHOLD = 0;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;

    @Transactional
    @Override
    public AccommodationDto save(CreateAccommodationRequestDto requestDto) {
        validateAccommodationUniqueness(requestDto);
        Accommodation accommodation = accommodationMapper.toModel(requestDto);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public Page<AccommodationDto> findAllAvailable(Pageable pageable) {
        return accommodationRepository.findAllByAvailabilityGreaterThan(
                        AVAILABILITY_THRESHOLD, pageable)
                .map(accommodationMapper::toDto);
    }

    @Override
    public AccommodationDto findAvailableById(Long id) {
        return accommodationRepository.findByIdAndAvailabilityGreaterThan(
                        id, AVAILABILITY_THRESHOLD)
                .map(accommodationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Can't find accommodation by id: "
                        + id));
    }

    @Transactional
    @Override
    public AccommodationDto updateById(Long id, CreateAccommodationRequestDto requestDto) {
        validateAccommodationUniqueness(requestDto);
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't update accommodation by id: "
                        + id));
        accommodationMapper.updateAccommodationFromDto(requestDto, accommodation);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        accommodationRepository.deleteById(id);
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
