package com.chertiavdev.bookingapp.dto.booking;

import com.chertiavdev.bookingapp.model.Booking.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

@Schema(description = "DTO representing a booking")
@Data
public class BookingDto {
    @Schema(description = "Unique identifier of the accommodation", example = "1")
    private Long id;
    @Schema(description = "Date of check-in, yyyy-MM-dd", example = "2025-03-01")
    private LocalDate checkIn;
    @Schema(description = "Date of check-out, yyyy-MM-dd", example = "2025-03-10")
    private LocalDate checkOut;
    @Schema(description = "Unique identifier of the accommodation", example = "1")
    private Long accommodationId;
    @Schema(description = "Unique identifier of the user", example = "2")
    private Long userId;
    @Schema(description = "Status of the booking", example = "PENDING")
    private Status status;
}
