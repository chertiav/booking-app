package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.model.Booking.Status.CANCELLED;

import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.exception.AccommodationAvailabilityException;
import com.chertiavdev.bookingapp.mapper.BookingMapper;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.BookingRepository;
import com.chertiavdev.bookingapp.service.BookingService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingDto save(CreateBookingRequestDto requestDto, User user) {
        if (!isAccommodationAvailable(
                requestDto.getAccommodationId(),
                requestDto.getCheckIn(),
                requestDto.getCheckOut()
        )) {
            throw new AccommodationAvailabilityException(
                    "Accommodation is not available for the requested dates: "
                    + requestDto.getCheckIn() + " - " + requestDto.getCheckOut());
        }
        Booking booking = bookingMapper.toModel(requestDto, user);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public Page<BookingDto> findAllByUserEmail(String email, Pageable pageable) {
        return null;
    }

    private boolean isAccommodationAvailable(
            Long accommodationId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return bookingRepository
                .findOverlappingBookings(accommodationId, startDate, endDate, CANCELLED)
                .isEmpty();
    }
}
