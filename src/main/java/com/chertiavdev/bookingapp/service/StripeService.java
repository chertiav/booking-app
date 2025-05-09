package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;

public interface StripeService {
    Session createSession(CreatePaymentRequestDto requestDto, BigDecimal amount);
}
