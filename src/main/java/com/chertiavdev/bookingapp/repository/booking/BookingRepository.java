package com.chertiavdev.bookingapp.repository.booking;

import com.chertiavdev.bookingapp.model.Booking;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long>,
        JpaSpecificationExecutor<Booking> {
    @Query("SELECT b FROM Booking b "
            + "WHERE b.accommodation.id = :accommodationId "
            + "AND b.status != :statusCancelled "
            + "AND b.isDeleted = false "
            + "AND (b.checkIn < :checkOut AND b.checkOut > :checkIn)")
    List<Booking> findOverlappingBookings(@Param("accommodationId") Long accommodationId,
                                          @Param("checkIn") LocalDate checkIn,
                                          @Param("checkOut") LocalDate checkOut,
                                          @Param("statusCancelled") Booking.Status statusCancelled);

    Page<Booking> findBookingsByUserId(Long userId, Pageable pageable);

    Optional<Booking> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT b FROM Booking b "
            + "JOIN FETCH b.accommodation a "
            + "JOIN FETCH a.location l "
            + "JOIN FETCH b.user u "
            + "WHERE b.status NOT IN ('CANCELLED', 'EXPIRED')"
            + "AND b.isDeleted = false "
            + "AND b.checkOut <= :expiredToDate "
            + "ORDER BY b.id, b.checkOut")
    List<Booking> findUpcomingBookings(@Param("expiredToDate") LocalDate expiredToDate);

    @Query(value = "SELECT "
            + "COALESCE(SUM(a.daily_rate * (b.check_out - b.check_in)), 0) AS total_price "
            + "FROM bookings b "
            + "JOIN accommodations a ON a.id = b.accommodation_id "
            + "WHERE b.id = :bookingId AND b.user_id = :userId",
            nativeQuery = true)
    Optional<BigDecimal> calculateTotalPriceByBookingIdAndUserId(
            Long bookingId,
            Long userId
    );
}
