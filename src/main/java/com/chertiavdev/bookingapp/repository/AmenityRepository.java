package com.chertiavdev.bookingapp.repository;

import com.chertiavdev.bookingapp.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}
