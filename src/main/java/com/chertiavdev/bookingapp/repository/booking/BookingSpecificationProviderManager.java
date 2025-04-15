package com.chertiavdev.bookingapp.repository.booking;

import com.chertiavdev.bookingapp.exception.SpecificationProviderNotFoundException;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.repository.SpecificationProvider;
import com.chertiavdev.bookingapp.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookingSpecificationProviderManager implements SpecificationProviderManager<Booking> {
    private final List<SpecificationProvider<Booking>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Booking> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(parameter -> parameter.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new SpecificationProviderNotFoundException(
                        "Can't find correct specification provider or key " + key));
    }
}
