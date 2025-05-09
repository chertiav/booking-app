package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.chertiavdev.bookingapp.dto.payment.PaymentDto;
import com.chertiavdev.bookingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    Page<PaymentDto> getPayments(Long userId, Pageable pageable);

    PaymentDto initiatePayment(CreatePaymentRequestDto requestDto, User user);
}
