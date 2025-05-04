package com.chertiavdev.bookingapp.util;

import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.booking.BookingExpiredNotificationDto;
import com.chertiavdev.bookingapp.model.Booking;
import com.chertiavdev.bookingapp.model.User;

public class NotificationUtils {

    private NotificationUtils() {
    }

    public static String accommodationCreatedNotification(AccommodationDto accommodationDto) {
        return String.format("""
                        New accommodation has been added:
                        - Type: %s
                        - Daily Rate: %.2f
                        - Location: %s
                        """,
                accommodationDto.getType(),
                accommodationDto.getDailyRate(),
                accommodationDto.getLocation()
        );
    }

    public static String bookingNotificationForAdmins(Booking booking, User user, String action) {
        return String.format("""
                        Booking has been %s.
                        - User: %s %s
                        - Booking ID: %s
                        - Accommodation ID: %s
                        - Check-in: %s
                        - Check-out: %s
                        """,
                action,
                user.getFirstName(),
                user.getLastName(),
                booking.getId(),
                booking.getAccommodation().getId(),
                booking.getCheckIn(),
                booking.getCheckOut()
        );
    }

    public static String bookingNotificationToUser(Booking booking, String action) {
        return String.format("""
                        Booking has been %s.
                        - Booking ID: %s
                        - Check-in: %s
                        - Check-out: %s
                        - If you have any questions, please contact support.
                        """,
                action,
                booking.getId(),
                booking.getCheckIn(),
                booking.getCheckOut()
        );
    }

    public static String buildBookingExpiredAlert(BookingExpiredNotificationDto notificationDto) {
        return String.format("""
                        ⚠ *Booking Expired!*
                        
                        📌 *Booking ID:* %d
                        👤 *Customer:* %s
                        📧 *Customer Email:* %s
                        🏨 *Location:* %s
                        📅 *Check-Out Date:* %s
                        📋 *Status:* %s
                        """,
                notificationDto.getBookingId(),
                notificationDto.getCustomer(),
                notificationDto.getCustomerEmail(),
                notificationDto.getLocation(),
                notificationDto.getCheckOut(),
                notificationDto.getStatus()
        );
    }
}
