package com.chertiavdev.bookingapp.util.constants;

public class TelegramNotificationConstants {
    public static final String TEST_TELEGRAM_TOKEN = "test-token";
    public static final String SUCCESSFUL_TELEGRAM_LINK_MESSAGE =
            "✅ Telegram is linked to your account.";
    public static final String EXPIRED_TOKEN_NOTIFICATION = "❌ Invalid or expired token.";
    public static final String HELP_MENU = "Available commands:\n%s\n";
    public static final String UNKNOWN_COMMAND_USE_HELP_TO_SEE_THE_AVAILABLE_COMMANDS =
            "Unknown command. 🤔 Use /help to see the available commands.";
    public static final String SUCCESSFULLY_UNSUBSCRIBED_FROM_NOTIFICATIONS =
            "You have successfully unsubscribed from notifications. ❌";
    public static final String START_COMMAND_MESSAGE = """
            ❗️ To enable Telegram notifications:
            
            1. Open the application and log in to your account.
            2. Go to the notification settings.
            3. Click [Connect Telegram].
            
            Once you receive a command in the format `/start <token>`, come back here and send it.
            """;
    public static final String HELP_MENU_ENTRY_FORMAT = "%s - %s";
    public static final String PAYMENT_NOT_COMPLETED_NOTIFICATION = "The payment with id: %s has "
            + "not been completed. Please try again within 24 hours using the provided link";
    public static final String PAYMENT_CANCELLED_NOTIFICATION = "Payment cancelled. You can "
            + "complete this payment within 24 hours using the provided link";
    public static final String PAYMENT_NOTIFICATION =
            "Payment by id: %d for the amount: %.2f was completed successfully";
}
