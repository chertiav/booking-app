package com.chertiavdev.bookingapp.data.builders;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_DAILY_RATE_7550;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_PENDING_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_PENDING_URL;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_RENEW_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_RENEW_URL;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestSession;

import com.stripe.model.checkout.Session;
import lombok.Getter;

@Getter
public class StripleTestDataBuilder {
    private final Session sessionPendingBooking;
    private final Session renewSession;

    public StripleTestDataBuilder() {
        this.sessionPendingBooking = createSessionPendingBooking();
        this.renewSession = createRenewSession();
    }

    private Session createSessionPendingBooking() {
        return createTestSession(
                PAYMENT_SESSION_PENDING_ID,
                PAYMENT_SESSION_PENDING_URL,
                ACCOMMODATION_DAILY_RATE_7550
        );
    }

    private Session createRenewSession() {
        return createTestSession(
                PAYMENT_SESSION_RENEW_ID,
                PAYMENT_SESSION_RENEW_URL,
                ACCOMMODATION_DAILY_RATE_7550
        );
    }
}
