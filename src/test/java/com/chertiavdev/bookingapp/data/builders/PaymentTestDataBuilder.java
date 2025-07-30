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
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final Booking pendingBooking;
    private final Booking pendingBookingUserSansa;
    private final Booking confirmedBooking;

    private final Session sessionPendingBooking;
    private final Session renewSession;

    private final Payment pendingPaymentPendingBooking;
    private final Payment paidPaymentPendingBooking;
    private final Payment expiredPaymentPendingBooking;
    private final Payment pendingPaymentPendingBookingToModel;
    private final Payment paidPaymentConfirmedBooking;
    private final Payment renewPaymentPendingBooking;

    private final PaymentDto pendingPaymentPendingBookingDto;
    private final PaymentDto paidPaymentConfirmedBookingDto;
    private final PaymentDto expiredPaymentPendingBookingDto;
    private final PaymentDto paidPaymentPendingBookingDto;
    private final PaymentDto paymentRenewSessionDto;

    private final CreatePaymentRequestDto paymentRequestPendingBookingDto;

    private final Pageable pageable;

    public PaymentTestDataBuilder(
            BookingTestDataBuilder bookingTestDataBuilder,
            StripleTestDataBuilder stripleTestDataBuilder
    ) {
        this.pendingBooking = bookingTestDataBuilder.getPendingBooking();
        this.pendingBookingUserSansa = bookingTestDataBuilder.getPendingBookingUserSansa();
        this.confirmedBooking = bookingTestDataBuilder.getConfirmedBooking();

        this.sessionPendingBooking = stripleTestDataBuilder.getSessionPendingBooking();
        this.renewSession = stripleTestDataBuilder.getRenewSession();

        this.pendingPaymentPendingBooking = createPendingPaymentPendingBooking();
        this.paidPaymentPendingBooking = createPaidPaymentPendingBooking();
        this.expiredPaymentPendingBooking = createExpiredPaymentPendingBooking();
        this.pendingPaymentPendingBookingToModel = createPendingPaymentPendingBookingToModel();
        this.paidPaymentConfirmedBooking = createPaidPaymentConfirmedBooking();
        this.renewPaymentPendingBooking = createRenewPaymentPendingBooking();

        this.pendingPaymentPendingBookingDto = createPendingPaymentPendingBookingDto();
        this.paidPaymentConfirmedBookingDto = createPaidPaymentConfirmedBookingDto();
        this.expiredPaymentPendingBookingDto = createExpiredPaymentPendingBookingDto();
        this.paidPaymentPendingBookingDto = createPaidPaymentPendingBookingDto();
        this.paymentRenewSessionDto = createPaymentRenewSessionDto();

        this.paymentRequestPendingBookingDto = createPaymentRequestPendingBookingDto();

        this.pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE);
    }

    public Page<Payment> buildAllPaymentsUserJhonToPage() {
        return createPage(
                List.of(pendingPaymentPendingBooking, paidPaymentConfirmedBooking),
                pageable
        );
    }

    public Page<PaymentDto> buildAllPaymentDtosUserJhonToPage() {
        return createPage(
                List.of(pendingPaymentPendingBookingDto, paidPaymentConfirmedBookingDto),
                pageable
        );
    }

    public Page<Payment> buildAllPaymentBookingsPage() {
        return createPage(
                List.of(
                        pendingPaymentPendingBooking,
                        paidPaymentConfirmedBooking,
                        expiredPaymentPendingBooking),
                pageable
        );
    }

    public Page<PaymentDto> buildAllPaymentDtosPage() {
        return createPage(
                List.of(
                        pendingPaymentPendingBookingDto,
                        paidPaymentConfirmedBookingDto,
                        expiredPaymentPendingBookingDto),
                pageable
        );
    }

    public Page<Payment> buildEmptyPaymentsPage() {
        return createPage(List.of(), pageable);
    }

    public List<Payment> buildListAllPaymentsByPendingStatus() {
        return List.of(pendingPaymentPendingBooking);
    }

    private Payment createPendingPaymentPendingBooking() {
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

    private Payment createPaidPaymentPendingBooking() {
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

    private Payment createExpiredPaymentPendingBooking() {
        return createTestPayment(
                SAMPLE_TEST_ID_3,
                Payment.Status.EXPIRED,
                pendingBookingUserSansa,
                PAYMENT_SESSION_EXPIRED_ID,
                PAYMENT_SESSION_EXPIRED_URL,
                ACCOMMODATION_DAILY_RATE_10050,
                false
        );
    }

    private Payment createPendingPaymentPendingBookingToModel() {
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

    private Payment createPaidPaymentConfirmedBooking() {
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
                SAMPLE_TEST_ID_3,
                Payment.Status.PENDING,
                pendingBookingUserSansa,
                PAYMENT_SESSION_RENEW_ID,
                PAYMENT_SESSION_RENEW_URL,
                ACCOMMODATION_DAILY_RATE_10050,
                false
        );
    }

    private PaymentDto createPendingPaymentPendingBookingDto() {
        return mapPaymentToDto(pendingPaymentPendingBooking);
    }

    private PaymentDto createPaidPaymentConfirmedBookingDto() {
        return mapPaymentToDto(paidPaymentConfirmedBooking);
    }

    private PaymentDto createExpiredPaymentPendingBookingDto() {
        return mapPaymentToDto(expiredPaymentPendingBooking);
    }

    private PaymentDto createPaidPaymentPendingBookingDto() {
        return mapPaymentToDto(paidPaymentPendingBooking);
    }

    private PaymentDto createPaymentRenewSessionDto() {
        return mapPaymentToDto(renewPaymentPendingBooking);
    }

    private CreatePaymentRequestDto createPaymentRequestPendingBookingDto() {
        return createTestPaymentRequestDto(SAMPLE_TEST_ID_1);
    }
}
