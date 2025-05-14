package com.chertiavdev.bookingapp.repository.payment;

import com.chertiavdev.bookingapp.model.Payment;
import com.chertiavdev.bookingapp.model.Payment.Status;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p "
            + "JOIN FETCH p.booking b "
            + "WHERE b.user.id = :userId "
            + "AND p.status != 'CANCELED' "
            + "AND b.isDeleted = false")
    Page<Payment> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Payment p "
                  + "JOIN FETCH p.booking b "
                  + "JOIN FETCH b.user u "
                  + "WHERE p.sessionId = :sessionId "
                  + "AND b.isDeleted = false")
    Optional<Payment> findAllBySessionId(@Param("sessionId") String sessionId);

    List<Payment> findAllByStatus(Status status);

    @Query("SELECT p FROM Payment p "
            + "JOIN FETCH p.booking b "
            + "JOIN FETCH b.user u "
            + "WHERE b.id = :bookingId "
            + "AND b.isDeleted = false")
    Optional<Payment> findByBookingId(@Param("bookingId") Long bookingId);
}
