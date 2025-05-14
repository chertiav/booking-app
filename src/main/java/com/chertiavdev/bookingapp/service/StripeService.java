package com.chertiavdev.bookingapp.service;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;

public interface StripeService {
    Session createSession(Long bookingId, BigDecimal amount);

    boolean isSessionPaid(String sessionId);

    boolean isSessionExpired(String sessionId);
}
