package com.chertiavdev.bookingapp.dto.booking;

import com.chertiavdev.bookingapp.validation.date.DateFieldMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Data;

@Data
@DateFieldMatch(startDate = "checkIn", endDate = "checkOut",
        message = "The check-in date must precede the check-out date and must be today or "
                + "a future date.")
public class CreateBookingRequestDto {
    @Schema(description = "Date of check-in, yyyy-MM-dd", example = "2025-03-01")
    @NotNull(message = "Check-in date must not be null")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date must not be null")
    @Schema(description = "Date of check-out, yyyy-MM-dd", example = "2025-03-10")
    private LocalDate checkOut;

    @NotNull(message = "User ID must not be null")
    @Positive(message = "Accommodation ID must be a positive number")
    @Schema(description = "Unique identifier of the accommodation", example = "1")
    private Long accommodationId;
}
