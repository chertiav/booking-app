package com.chertiavdev.bookingapp.repository.booking.spec;

import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecificationProvider implements SpecificationProvider<Booking> {
    private static final String USER_KEY = "user";
    private static final String ID_FIELD = "id";

    @Override
    public Specification<Booking> getSpecification(String[] params) {
        return (root, query, cb) ->
                root.get(USER_KEY).get(ID_FIELD).in(Arrays.stream(params).toArray());
    }

    @Override
    public String getKey() {
        return USER_KEY;
    }
}
