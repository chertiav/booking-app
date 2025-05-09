package com.chertiavdev.bookingapp.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Schema(description = "DTO for creating a new payment")
@Data
public class CreatePaymentRequestDto {
    @NotNull(message = "Booking ID must not be null")
    @Positive(message = "Booking ID must be a positive number")
    @Schema(description = "Unique identifier of the booking", example = "1")
    private Long bookingId;
}
