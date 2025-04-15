package com.chertiavdev.bookingapp.repository;

import com.chertiavdev.bookingapp.dto.booking.BookingSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookingSearchParameters searchParameters);
}
