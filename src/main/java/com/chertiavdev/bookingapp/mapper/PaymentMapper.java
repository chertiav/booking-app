package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.chertiavdev.bookingapp.dto.payment.PaymentDto;
import com.chertiavdev.bookingapp.model.Payment;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {BookingMapper.class})
public interface PaymentMapper {
    @Mapping(target = "bookingId", source = "booking.id")
    PaymentDto toDto(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "status", expression = "java(Payment.Status.PENDING)")
    @Mapping(target = "booking", source = "requestDto.bookingId", qualifiedByName = "bookingById")
    @Mapping(target = "sessionUrl", source = "session.url")
    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "amountToPay", source = "amountToPay")
    Payment toModel(CreatePaymentRequestDto requestDto, Session session, BigDecimal amountToPay);
}
