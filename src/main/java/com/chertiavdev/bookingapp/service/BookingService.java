package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto save(CreateBookingRequestDto requestDto, User user);

    Page<BookingDto> findAllByUserEmail(String email, Pageable pageable);
}
