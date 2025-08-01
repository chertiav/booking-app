package com.chertiavdev.bookingapp.config;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENT_SESSION_PENDING_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.PAYMENT_SESSION_PENDING_URL;

import com.chertiavdev.bookingapp.service.StripeService;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class StripeTestConfig {
    @Bean
    public StripeService stripeService() {
        return new StripeService() {
            @Override
            public Session createSession(Long bookingId, BigDecimal amount) {
                Session sess = new Session();
                sess.setId(PAYMENT_SESSION_PENDING_ID);
                sess.setUrl(PAYMENT_SESSION_PENDING_URL);
                return sess;
            }

            @Override
            public boolean isSessionPaid(String sessionId) {
                return true;
            }

            @Override
            public boolean isSessionExpired(String sessionId) {
                return false;
            }
        };
    }
}
