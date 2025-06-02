package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.model.Booking.Status.CONFIRMED;
import static com.chertiavdev.bookingapp.model.Booking.Status.PENDING;
import static com.chertiavdev.bookingapp.model.Payment.Status.EXPIRED;
import static com.chertiavdev.bookingapp.model.Payment.Status.PAID;
import static com.chertiavdev.bookingapp.model.Role.RoleName.ADMIN;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.PAYMENT_CANCELLED_NOTIFICATION;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.PAYMENT_NOTIFICATION;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.PAYMENT_NOT_COMPLETED_NOTIFICATION;

import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.chertiavdev.bookingapp.dto.payment.PaymentDto;
import com.chertiavdev.bookingapp.exception.AccessDeniedException;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.exception.PaymentRenewException;
import com.chertiavdev.bookingapp.mapper.PaymentMapper;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.Payment;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.booking.BookingRepository;
import com.chertiavdev.bookingapp.repository.payment.PaymentRepository;
import com.chertiavdev.bookingapp.service.NotificationService;
import com.chertiavdev.bookingapp.service.PaymentService;
import com.chertiavdev.bookingapp.service.StripeService;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;
    private final StripeService stripeService;
    private final PaymentMapper paymentMapper;

    @Override
    public Page<PaymentDto> getPayments(Long userId, Pageable pageable) {
        if (userId != null) {
            return paymentRepository.findAllByUserId(userId, pageable).map(paymentMapper::toDto);
        }
        return paymentRepository.findAll(pageable).map(paymentMapper::toDto);
    }

    @Transactional
    @Override
    public PaymentDto initiatePayment(CreatePaymentRequestDto requestDto, User user) {
        BigDecimal amountToPay = bookingRepository
                .calculateTotalPriceByBookingIdAndUserId(requestDto.getBookingId(), user.getId());
        Session session = stripeService.createSession(requestDto.getBookingId(), amountToPay);
        return paymentMapper.toDto(paymentRepository.save(
                paymentMapper.toModel(requestDto, session, amountToPay)
        ));
    }

    @Transactional
    @Override
    public PaymentDto handleSuccess(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);
        Long userId = payment.getBooking().getUser().getId();
        if (stripeService.isSessionPaid(sessionId)) {
            processPaymentAndBookingUpdate(payment);
            notifySuccess(payment, userId);
        } else {
            notificationService.sendNotificationByUserId(
                            String.format(PAYMENT_NOT_COMPLETED_NOTIFICATION, payment.getId()),
                            userId);
        }
        return paymentMapper.toDto(payment);
    }

    @Override
    public PaymentDto handleCancel(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);

        notificationService.sendNotificationByUserId(
                PAYMENT_CANCELLED_NOTIFICATION,
                payment.getBooking().getUser().getId());

        return paymentMapper.toDto(payment);
    }

    @Transactional
    @Override
    public void expirePendingPayments() {
        List<Payment> payments = paymentRepository.findAllByStatus(Payment.Status.PENDING);
        if (!payments.isEmpty()) {
            payments.forEach(payment -> {
                if (stripeService.isSessionExpired(payment.getSessionId())) {
                    payment.setStatus(EXPIRED);
                    paymentRepository.save(payment);
                }
            });
        }
    }

    @Transactional
    @Override
    public PaymentDto renewPayment(Long paymentId, Long userId) {
        Payment payment = getPaymentById(paymentId);
        Long paymentUserId = payment.getBooking().getUser().getId();
        Booking booking = payment.getBooking();

        checkSessionOwnership(paymentId, userId, paymentUserId);
        verifyRenewalEligibility(payment, booking);

        updatePayment(booking.getId(), payment);

        return paymentMapper.toDto(payment);
    }

    @Override
    public Long getPendingPaymentsCountByUserId(Long userId) {
        return paymentRepository.findPendingPaymentsCount(userId);
    }

    @Transactional
    @Override
    public void updateStatusByBookingId(Long bookingId, Payment.Status status) {
        paymentRepository.findByBookingId(bookingId)
                .ifPresent(payment -> {
                    payment.setStatus(status);
                    paymentRepository.save(payment);
                });
    }

    private void processPaymentAndBookingUpdate(Payment payment) {
        payment.setStatus(PAID);
        paymentRepository.save(payment);

        Booking booking = payment.getBooking();
        booking.setStatus(CONFIRMED);
        bookingRepository.save(booking);
    }

    private Payment getPaymentBySessionId(String sessionId) {
        return paymentRepository.findAllBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find payment by session id: "
                        + sessionId));
    }

    private void notifySuccess(Payment payment, Long userId) {
        String message = String.format(PAYMENT_NOTIFICATION,
                payment.getId(), payment.getAmountToPay());
        notificationService.sendNotification(message, ADMIN);
        notificationService.sendNotificationByUserId(message, userId);
    }

    private static void checkSessionOwnership(Long paymentId, Long userId, Long paymentUserId) {
        if (!paymentUserId.equals(userId)) {
            throw new AccessDeniedException("Can't renew session by payment id" + paymentId);
        }
    }

    private Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find payment by id: "
                        + paymentId));
    }

    private static void verifyRenewalEligibility(Payment payment, Booking booking) {
        if (!payment.getStatus().equals(EXPIRED) || !booking.getStatus().equals(PENDING)) {
            throw new PaymentRenewException("Can't renew payment by id: " + payment.getId()
                    + " payment status must be: " + EXPIRED + " and"
                    + " booking status must be: " + PENDING);
        }
    }

    private void updatePayment(Long bookingId, Payment payment) {
        Session session = stripeService.createSession(bookingId, payment.getAmountToPay());
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setStatus(Payment.Status.PENDING);
        paymentRepository.save(payment);
    }
}
