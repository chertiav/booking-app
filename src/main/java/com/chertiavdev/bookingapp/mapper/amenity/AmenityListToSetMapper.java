package com.chertiavdev.bookingapp.mapper.amenity;

import com.chertiavdev.bookingapp.model.Amenity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AmenityListToSetMapper {

    @AmenityMapping
    public Set<Amenity> map(List<Long> amenities) {
        return amenities.stream()
                .map(Amenity::new)
                .collect(Collectors.toSet());
    }
}
