package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.model.Booking.Status.CONFIRMED;
import static com.chertiavdev.bookingapp.model.Payment.Status.EXPIRED;
import static com.chertiavdev.bookingapp.model.Payment.Status.PAID;
import static com.chertiavdev.bookingapp.model.Payment.Status.PENDING;
import static com.chertiavdev.bookingapp.model.Role.RoleName.ADMIN;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.PAYMENT_CANCELLED_NOTIFICATION;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.PAYMENT_NOTIFICATION;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.PAYMENT_NOT_COMPLETED_NOTIFICATION;

import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.chertiavdev.bookingapp.dto.payment.PaymentDto;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.mapper.PaymentMapper;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.Payment;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.booking.BookingRepository;
import com.chertiavdev.bookingapp.repository.payment.PaymentRepository;
import com.chertiavdev.bookingapp.service.BookingService;
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
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

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
        BigDecimal amountToPay = bookingService
                .calculateTotalPrice(requestDto.getBookingId(), user.getId());
        Session session = stripeService.createSession(requestDto, amountToPay);
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
            notificationService
                    .sendNotificationByUserId(PAYMENT_NOT_COMPLETED_NOTIFICATION, userId);
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
        List<Payment> payments = paymentRepository.findAllByStatus(PENDING);
        if (!payments.isEmpty()) {
            payments.forEach(payment -> {
                if (stripeService.isSessionExpired(payment.getSessionId())) {
                    payment.setStatus(EXPIRED);
                    paymentRepository.save(payment);
                }
            });
        }
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
}
