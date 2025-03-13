package com.chertiavdev.bookingapp.repository;

import static com.chertiavdev.bookingapp.model.Accommodation.Type;

import com.chertiavdev.bookingapp.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    @Query("""
            SELECT COUNT(a) > 0 FROM Accommodation a
            JOIN a.location l
            WHERE l.city = :city
            AND l.street = :street
            AND l.houseNumber = :houseNumber
            AND (:apartmentNumber IS NULL OR l.apartmentNumber = :apartmentNumber)
            AND a.type = :type
            AND a.size = :size
            """
    )
    boolean existsByLocationAndTypeAndSize(
            @Param("city") String city,
            @Param("street") String street,
            @Param("houseNumber") String houseNumber,
            @Param("apartmentNumber") String apartmentNumber,
            @Param("type") Type type,
            @Param("size") String size
    );
}
