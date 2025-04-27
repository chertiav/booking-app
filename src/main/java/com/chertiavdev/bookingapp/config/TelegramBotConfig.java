package com.chertiavdev.bookingapp.config;

import com.chertiavdev.bookingapp.telegram.TelegramNotificationBot;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@ConditionalOnProperty(name = "telegram.enabled", havingValue = "true", matchIfMissing = true)
@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramNotificationBot telegramNotificationBot) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramNotificationBot);
            return botsApi;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register Telegram bot", e);
        }
    }
}
