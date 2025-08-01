package com.chertiavdev.bookingapp.scheduler;

import com.chertiavdev.bookingapp.service.BookingService;
import com.chertiavdev.bookingapp.service.PaymentService;
import com.chertiavdev.bookingapp.service.TelegramLinkService;
import java.time.Instant;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MyCronTasks {
    private static final int DAYS_UNTIL_EXPIRY = 1;
    private final TelegramLinkService telegramLinkService;
    private final BookingService bookingService;
    private final PaymentService paymentService;

    @Scheduled(cron = "${scheduler.cron.hourly}", zone = "${scheduler.cron.time.zone}")
    void deleteExpiredLinks() {
        telegramLinkService.deleteByExpiresAtBefore(Instant.now());
    }

    @Scheduled(cron = "${scheduler.cron.daily-nine}", zone = "${scheduler.cron.time.zone}")
    void checkExpiredBookings() {
        bookingService.checkAndNotifyExpiredBookings(LocalDate.now().plusDays(DAYS_UNTIL_EXPIRY));
    }

    @Scheduled(cron = "${scheduler.cron.minute}", zone = "${scheduler.cron.time.zone}")
    void checkExpiredPayments() {
        paymentService.expirePendingPayments();
    }
}
