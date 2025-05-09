package com.chertiavdev.bookingapp.repository.payment;

import com.chertiavdev.bookingapp.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p "
            + "JOIN FETCH p.booking b "
            + "WHERE b.user.id = :userId "
            + "AND b.isDeleted = false")
    Page<Payment> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}
