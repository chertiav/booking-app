package com.chertiavdev.bookingapp.dto.booking;

import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingExpiredNotificationDto {
    private Long bookingId;
    private LocalDate checkOut;
    private String location;
    private String customer;
    private String customerEmail;
    private String status;
}
