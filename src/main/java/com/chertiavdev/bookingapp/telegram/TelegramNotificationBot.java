package com.chertiavdev.bookingapp.telegram;

import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.EXPIRED_TOKEN_NOTIFICATION;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.HELP_MENU;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.HELP_MENU_ENTRY_FORMAT;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.START_COMMAND_MESSAGE;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.SUCCESSFULLY_UNSUBSCRIBED_FROM_NOTIFICATIONS;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.SUCCESSFUL_TELEGRAM_LINK_MESSAGE;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.TEST_TELEGRAM_TOKEN;
import static com.chertiavdev.bookingapp.util.constants.TelegramNotificationConstants.UNKNOWN_COMMAND_USE_HELP_TO_SEE_THE_AVAILABLE_COMMANDS;

import com.chertiavdev.bookingapp.exception.NotificationException;
import com.chertiavdev.bookingapp.service.TelegramLinkService;
import com.chertiavdev.bookingapp.service.UserTelegramService;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramNotificationBot extends TelegramLongPollingBot {
    private static final int ARGUMENT_LIMIT = 2;
    private static final int TOKEN_PART_INDEX = 1;
    private static final int COMMAND_PART_INDEX = 0;
    private static final String DELIMITER = "\n";
    private static final String REGEX_WHITESPACE = "\\s+";
    private final String botUsername;
    private final String botToken;
    private final UserTelegramService userTelegramService;
    private final TelegramLinkService telegramLinkService;

    @Value("${telegram.enabled:true}")
    private boolean telegramEnabled;

    public TelegramNotificationBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            UserTelegramService userTelegramService,
            TelegramLinkService telegramLinkService
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.userTelegramService = userTelegramService;
        this.telegramLinkService = telegramLinkService;
    }

    @PostConstruct
    public void init() {
        if (!telegramEnabled || TEST_TELEGRAM_TOKEN.equals(botToken)) {
            log.info("Telegram disabled or mock token — skipping command setup.");
            return;
        }
        try {
            execute(SetMyCommandsCommandFactory.getDefaultCommands());
        } catch (TelegramApiException e) {
            log.error("Error while setting bot commands: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            log.warn("Update does not contain a valid text message.");
            return;
        }

        String text = update.getMessage().getText().trim();
        Long chatId = update.getMessage().getChatId();

        String[] parts = text.split(REGEX_WHITESPACE, ARGUMENT_LIMIT);
        BotCommands command = BotCommands.fromCommand(parts[COMMAND_PART_INDEX]);

        switch (command) {
            case START -> handleStartCommand(parts, chatId);
            case HELP -> sendNotification(chatId, HELP_MENU.formatted(createHelpMenu()));
            case UNSUBSCRIBE -> {
                userTelegramService.unlinkByChatId(chatId);
                sendNotification(chatId, SUCCESSFULLY_UNSUBSCRIBED_FROM_NOTIFICATIONS);
            }
            default -> sendNotification(chatId,
                    UNKNOWN_COMMAND_USE_HELP_TO_SEE_THE_AVAILABLE_COMMANDS);
        }
    }

    public void sendNotification(long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(message)
                .build();
        try {
            execute(sendMessage);
            log.info("Notification sent to chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Error while sending message to chatId: {}. Error: {}",
                    chatId, e.getMessage(), e);
            throw new NotificationException("Can't send message: " + message);
        }
    }

    private void handleStartCommand(String[] parts, Long chatId) {
        if (parts.length < ARGUMENT_LIMIT || parts[TOKEN_PART_INDEX].isBlank()) {
            sendNotification(chatId, START_COMMAND_MESSAGE);
            return;
        }

        String token = parts[TOKEN_PART_INDEX].trim();
        log.info("Received /start with token: {}", token);

        telegramLinkService.getUserIdByToken(token).ifPresentOrElse(
                userId -> linkTelegramAccount(chatId, userId),
                () -> sendNotification(chatId, EXPIRED_TOKEN_NOTIFICATION)
        );
    }

    private void linkTelegramAccount(Long chatId, Long userId) {
        log.info("Linking Telegram chatId={} to userId={}", chatId, userId);
        userTelegramService.link(userId, chatId);
        sendNotification(chatId, SUCCESSFUL_TELEGRAM_LINK_MESSAGE);
    }

    private static String createHelpMenu() {
        return Arrays.stream(BotCommands.values())
                .map(command -> HELP_MENU_ENTRY_FORMAT
                        .formatted(command.getCommand(), command.getDescription()))
                .collect(Collectors.joining(DELIMITER));
    }
}
