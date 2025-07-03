package com.chertiavdev.bookingapp.data.builders;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_DAILY_RATE_10050;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ACCOMMODATION_DAILY_RATE_7550;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_EXPIRED_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_EXPIRED_URL;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_PAID_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_PAID_URL;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_PENDING_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_PENDING_URL;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_RENEW_ID;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.PAYMENT_SESSION_RENEW_URL;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_3;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createPage;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestPayment;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestPaymentRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.mapPaymentToDto;
import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.chertiavdev.bookingapp.dto.payment.PaymentDto;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.Payment;
import com.stripe.model.checkout.Session;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
public class PaymentTestDataBuilder {
    private final Booking pendingBooking;
    private final Booking confirmedBooking;

    private final Session sessionPendingBooking;
    private final Session renewSession;

    private final Payment paymentPendingBooking;
    private final Payment paymentPaidBooking;
    private final Payment paymentExpiredBooking;
    private final Payment paymentPendingBookingToModel;
    private final Payment paymentConfirmedBooking;
    private final Payment renewPaymentPendingBooking;

    private final PaymentDto paymentPendingBookingDto;
    private final PaymentDto paymentConfirmedBookingDto;
    private final PaymentDto paymentPaidBookingDto;
    private final PaymentDto paymentRenewSessionDto;

    private final CreatePaymentRequestDto paymentRequestPendingBookingDto;

    private final Pageable pageable;

    public PaymentTestDataBuilder(
            BookingTestDataBuilder bookingTestDataBuilder,
            StripleTestDataBuilder stripleTestDataBuilder
    ) {
        this.pendingBooking = bookingTestDataBuilder.getPendingBooking();
        this.confirmedBooking = bookingTestDataBuilder.getConfirmedBooking();

        this.sessionPendingBooking = stripleTestDataBuilder.getSessionPendingBooking();
        this.renewSession = stripleTestDataBuilder.getRenewSession();

        this.paymentPendingBooking = createPaymentPendingBooking();
        this.paymentPaidBooking = createPaymentPaidBooking();
        this.paymentExpiredBooking = createPaymentExpiredBooking();
        this.paymentPendingBookingToModel = createPaymentPendingBookingToModel();
        this.paymentConfirmedBooking = createPaymentConfirmedBooking();
        this.renewPaymentPendingBooking = createRenewPaymentPendingBooking();

        this.paymentPendingBookingDto = createPaymentPendingBookingDto();
        this.paymentConfirmedBookingDto = createPaymentConfirmedBookingDto();
        this.paymentPaidBookingDto = createPaymentPaidBookingDto();
        this.paymentRenewSessionDto = createPaymentRenewSessionDto();

        this.paymentRequestPendingBookingDto = createPaymentRequestPendingBookingDto();

        this.pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE);
    }

    public Page<Payment> buildPaymentPendingBookingsPage() {
        return createPage(List.of(paymentPendingBooking), pageable);
    }

    public Page<PaymentDto> buildPaymentPendingBookingDtosPage() {
        return createPage(List.of(paymentPendingBookingDto), pageable);
    }

    public Page<Payment> buildAllPaymentBookingsPage() {
        return createPage(List.of(paymentPendingBooking, paymentConfirmedBooking), pageable);
    }

    public Page<PaymentDto> buildAllPaymentDtosPage() {
        return createPage(List.of(paymentPendingBookingDto, paymentConfirmedBookingDto), pageable);
    }

    private Payment createPaymentPendingBooking() {
        return createTestPayment(
                SAMPLE_TEST_ID_1,
                Payment.Status.PENDING,
                pendingBooking,
                PAYMENT_SESSION_PENDING_ID,
                PAYMENT_SESSION_PENDING_URL,
                ACCOMMODATION_DAILY_RATE_7550,
                false
        );
    }

    private Payment createPaymentPaidBooking() {
        return createTestPayment(
                SAMPLE_TEST_ID_1,
                Payment.Status.PAID,
                pendingBooking,
                PAYMENT_SESSION_PENDING_ID,
                PAYMENT_SESSION_PENDING_URL,
                ACCOMMODATION_DAILY_RATE_7550,
                false
        );
    }

    private Payment createPaymentExpiredBooking() {
        return createTestPayment(
                SAMPLE_TEST_ID_3,
                Payment.Status.EXPIRED,
                pendingBooking,
                PAYMENT_SESSION_EXPIRED_ID,
                PAYMENT_SESSION_EXPIRED_URL,
                ACCOMMODATION_DAILY_RATE_7550,
                false
        );
    }

    private Payment createPaymentPendingBookingToModel() {
        return createTestPayment(
                null,
                Payment.Status.PENDING,
                pendingBooking,
                PAYMENT_SESSION_PENDING_ID,
                PAYMENT_SESSION_PENDING_URL,
                ACCOMMODATION_DAILY_RATE_7550,
                false
        );
    }

    private Payment createPaymentConfirmedBooking() {
        return createTestPayment(
                SAMPLE_TEST_ID_2,
                Payment.Status.PAID,
                confirmedBooking,
                PAYMENT_SESSION_PAID_ID,
                PAYMENT_SESSION_PAID_URL,
                ACCOMMODATION_DAILY_RATE_10050,
                false
        );
    }

    private Payment createRenewPaymentPendingBooking() {
        return createTestPayment(
                SAMPLE_TEST_ID_1,
                Payment.Status.PENDING,
                pendingBooking,
                PAYMENT_SESSION_RENEW_ID,
                PAYMENT_SESSION_RENEW_URL,
                ACCOMMODATION_DAILY_RATE_7550,
                false
        );
    }

    private PaymentDto createPaymentPendingBookingDto() {
        return mapPaymentToDto(paymentPendingBooking);
    }

    private PaymentDto createPaymentConfirmedBookingDto() {
        return mapPaymentToDto(paymentConfirmedBooking);
    }

    private PaymentDto createPaymentPaidBookingDto() {
        return mapPaymentToDto(paymentPaidBooking);
    }

    private PaymentDto createPaymentRenewSessionDto() {
        return mapPaymentToDto(renewPaymentPendingBooking);
    }

    private CreatePaymentRequestDto createPaymentRequestPendingBookingDto() {
        return createTestPaymentRequestDto(SAMPLE_TEST_ID_1);
    }
}
