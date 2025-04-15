package com.chertiavdev.bookingapp.repository.accommodation;

import static com.chertiavdev.bookingapp.model.Accommodation.Type;

import com.chertiavdev.bookingapp.model.Accommodation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = {"location", "amenities"})
    Page<Accommodation> findAllByAvailabilityGreaterThan(Integer availability, Pageable pageable);

    @EntityGraph(attributePaths = {"location", "amenities"})
    Optional<Accommodation> findByIdAndAvailabilityGreaterThan(Long id, Integer availability);
}
