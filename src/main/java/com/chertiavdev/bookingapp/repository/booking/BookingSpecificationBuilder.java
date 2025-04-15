package com.chertiavdev.bookingapp.repository.booking;

import com.chertiavdev.bookingapp.dto.booking.BookingSearchParameters;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.repository.SpecificationBuilder;
import com.chertiavdev.bookingapp.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookingSpecificationBuilder implements SpecificationBuilder<Booking> {
    private static final String USER_ID_KEY = "user";
    private static final String STATUS_KEY = "status";
    private final SpecificationProviderManager<Booking> specificationProviderManager;

    @Override
    public Specification<Booking> build(BookingSearchParameters searchParameters) {
        Specification<Booking> bookSpecification = Specification.where(null);
        if (searchParameters.userId() != null && searchParameters.userId().length > 0) {
            bookSpecification = bookSpecification
                    .and(specificationProviderManager.getSpecificationProvider(USER_ID_KEY)
                            .getSpecification(searchParameters.userId()));
        }
        if (searchParameters.status() != null && searchParameters.status().length > 0) {
            bookSpecification = bookSpecification
                    .and(specificationProviderManager.getSpecificationProvider(STATUS_KEY)
                            .getSpecification(searchParameters.status()));
        }
        return bookSpecification;
    }
}
