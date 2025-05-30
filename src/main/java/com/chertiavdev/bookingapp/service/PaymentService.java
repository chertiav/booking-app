package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.chertiavdev.bookingapp.dto.payment.PaymentDto;
import com.chertiavdev.bookingapp.model.Payment.Status;
import com.chertiavdev.bookingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    Page<PaymentDto> getPayments(Long userId, Pageable pageable);

    PaymentDto initiatePayment(CreatePaymentRequestDto requestDto, User user);

    PaymentDto handleSuccess(String sessionId);

    PaymentDto handleCancel(String sessionId);

    void expirePendingPayments();

    PaymentDto renewPayment(Long paymentId, Long userId);

    Long getPendingPaymentsCountByUserId(Long userId);

    void updateStatusByBookingId(Long bookingId, Status status);
}
