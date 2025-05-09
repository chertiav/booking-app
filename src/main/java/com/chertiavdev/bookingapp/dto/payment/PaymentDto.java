package com.chertiavdev.bookingapp.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

@Schema(description = "DTO representing payment details")
@Data
public class PaymentDto {
    @Schema(description = "Unique identifier of the payment", example = "1")
    private Long id;
    @Schema(description = "Unique identifier of the booking", example = "1")
    private Long bookingId;
    @Schema(description = "URL to redirect the user to for payment", example = "https://example.com/redirect")
    private String sessionUrl;
    @Schema(description = "Unique identifier of the session", example = "1")
    private String sessionId;
    @Schema(description = "Payment amount", example = "100.00")
    private BigDecimal amountToPay;
    @Schema(description = "Payment status", example = "PAID")
    private String status;
}
