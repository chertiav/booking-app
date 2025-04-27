package com.chertiavdev.bookingapp.service.impl;

import com.chertiavdev.bookingapp.exception.NotificationException;
import com.chertiavdev.bookingapp.model.Role.RoleName;
import com.chertiavdev.bookingapp.model.UserTelegram;
import com.chertiavdev.bookingapp.service.NotificationService;
import com.chertiavdev.bookingapp.service.UserTelegramService;
import com.chertiavdev.bookingapp.telegram.TelegramNotificationBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Log4j2
@Service
public class NotificationServiceImpl implements NotificationService {
    private final UserTelegramService userTelegramService;
    private final TelegramNotificationBot telegramNotificationBot;

    @Async("notificationTaskExecutor")
    @Override
    public void sendNotification(String message, RoleName targetRole) {
        userTelegramService.getAllUserByRole(targetRole).parallelStream()
                .forEach(userTelegram ->
                        sendTelegramNotification(message, userTelegram));
    }

    @Async("notificationTaskExecutor")
    @Override
    public void sendNotificationByUserId(String message, Long userId) {
        userTelegramService.getByUserId(userId).ifPresent(
                userTelegram -> sendTelegramNotification(message, userTelegram));
    }

    private void sendTelegramNotification(String message, UserTelegram userTelegram) {
        try {
            telegramNotificationBot.sendNotification(userTelegram.getChatId(), message);
            log.info("Notification sent successfully to chatId: {}",
                    userTelegram.getChatId());
        } catch (NotificationException ex) {
            log.warn("Failed to send notification to chatId: {}. Reason: {}",
                    userTelegram.getChatId(), ex.getMessage());
        }
    }
}
