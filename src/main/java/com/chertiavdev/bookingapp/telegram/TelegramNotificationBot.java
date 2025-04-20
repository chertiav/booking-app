package com.chertiavdev.bookingapp.telegram;

import com.chertiavdev.bookingapp.exception.NotificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramNotificationBot extends TelegramLongPollingBot {
    private final String botUsername;

    public TelegramNotificationBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername
    ) {
        super(botToken);
        this.botUsername = botUsername;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
    }

    public void sendNotification(long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(message)
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new NotificationException("Can't send message: " + message);
        }
    }
}
