package com.chertiavdev.bookingapp.service.impl;

import com.chertiavdev.bookingapp.service.NotificationService;
import com.chertiavdev.bookingapp.telegram.TelegramNotificationBot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final TelegramNotificationBot telegramNotificationBot;

    @Value("${telegram.chat.id}")
    private long chatId;

    @Async
    @Override
    public void sendNotification(String message) {
        telegramNotificationBot.sendNotification(chatId, message);
    }
}
