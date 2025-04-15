package com.chertiavdev.bookingapp.repository.booking.spec;

import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StatusSpecificationProvider implements SpecificationProvider<Booking> {
    private static final String STATUS_KEY = "status";

    @Override
    public Specification<Booking> getSpecification(String[] params) {
        return (root, query, cb) ->
                root.get(STATUS_KEY).in(Arrays.stream(params).toArray());
    }

    @Override
    public String getKey() {
        return STATUS_KEY;
    }
}
