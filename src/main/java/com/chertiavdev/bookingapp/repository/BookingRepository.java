package com.chertiavdev.bookingapp.repository;

import com.chertiavdev.bookingapp.model.Booking;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b "
            + "WHERE b.accommodation.id = :accommodationId "
            + "AND b.status != :statusCancelled "
            + "AND b.isDeleted = false "
            + "AND (b.checkIn < :checkOut AND b.checkOut > :checkIn)")
    List<Booking> findOverlappingBookings(@Param("accommodationId") Long accommodationId,
                                          @Param("checkIn") LocalDate checkIn,
                                          @Param("checkOut") LocalDate checkOut,
                                          @Param("statusCancelled") Booking.Status statusCancelled);
}
