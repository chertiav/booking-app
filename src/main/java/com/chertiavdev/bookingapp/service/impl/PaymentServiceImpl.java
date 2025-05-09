package com.chertiavdev.bookingapp.service.impl;

import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.chertiavdev.bookingapp.dto.payment.PaymentDto;
import com.chertiavdev.bookingapp.mapper.PaymentMapper;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.payment.PaymentRepository;
import com.chertiavdev.bookingapp.service.BookingService;
import com.chertiavdev.bookingapp.service.PaymentService;
import com.chertiavdev.bookingapp.service.StripeService;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
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
}
