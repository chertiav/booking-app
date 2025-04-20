package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.BookingSearchParameters;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto save(CreateBookingRequestDto requestDto, User user);

    Page<BookingDto> search(BookingSearchParameters searchParameters, Pageable pageable);

    Page<BookingDto> findByUserId(Long id, Pageable pageable);

    BookingDto findByIdAndUserId(Long id, Long userId);

    BookingDto updatedByIdAndUserId(Long id, Long userId, CreateBookingRequestDto requestDto);

    void cancelById(Long id, User user);
}
