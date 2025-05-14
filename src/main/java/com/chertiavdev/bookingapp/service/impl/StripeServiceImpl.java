package com.chertiavdev.bookingapp.service.impl;

import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.chertiavdev.bookingapp.exception.StripeServiceException;
import com.chertiavdev.bookingapp.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StripeServiceImpl implements StripeService {
    private static final String PAYMENT_SUCCESS_URL = "/api/payments/success";
    private static final String PAYMENT_CANCEL_URL = "/api/payments/cancel";
    private static final long DEFAULT_QUANTITY = 1L;
    private static final String BOOKING_PREFIX = "Booking #";
    private static final String PAYMENT_STATUS_PAID = "paid";
    private static final String PAYMENT_STATUS_EXPIRED = "expired";
    private static final String SESSION_ID_QUERY_PARAM = "session_id";
    private static final String CHECKOUT_SESSION_ID_TOKEN = "{CHECKOUT_SESSION_ID}";
    @Value("${app.base.url}")
    private String appBaseUrl;
    @Value("${stripe.secret.currency}")
    private String currency;

    @Override
    public Session createSession(CreatePaymentRequestDto requestDto, BigDecimal amount) {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(buildPaymentUrl(PAYMENT_SUCCESS_URL))
                    .setCancelUrl(buildPaymentUrl(PAYMENT_CANCEL_URL))
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(DEFAULT_QUANTITY)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(currency)
                                                    .setUnitAmount(convertToCents(amount))
                                                    .setProductData(createProductData(requestDto))
                                                    .build())
                                    .build())
                    .build();
            return Session.create(params);
        } catch (StripeException ex) {
            throw new StripeServiceException("Error when working with Stripe API");
        }
    }

    @Override
    public boolean isSessionPaid(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            System.out.println(session.getExpiresAt());
            return PAYMENT_STATUS_PAID.equals(session.getPaymentStatus());
        } catch (StripeException ex) {
            throw new StripeServiceException("Can't retrieve session by id: " + sessionId
                    + ". Reason: " + ex.getMessage());
        }
    }

    @Override
    public boolean isSessionExpired(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return PAYMENT_STATUS_EXPIRED.equals(session.getStatus());
        } catch (StripeException ex) {
            throw new StripeServiceException("Can't retrieve session by id: " + sessionId
                    + ". Reason: " + ex.getMessage());
        }
    }

    private long convertToCents(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).longValue();
    }

    private ProductData createProductData(CreatePaymentRequestDto requestDto) {
        return ProductData.builder()
                .setName(BOOKING_PREFIX + requestDto.getBookingId())
                .build();
    }

    private String buildPaymentUrl(String url) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(appBaseUrl);
        uriBuilder.queryParam(SESSION_ID_QUERY_PARAM, CHECKOUT_SESSION_ID_TOKEN);
        return uriBuilder.path(url).build().toString();
    }
}
