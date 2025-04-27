package com.chertiavdev.bookingapp.telegram;

import java.util.Arrays;
import lombok.Getter;

public enum BotCommands {
    START("/start", "Start the bot and begin interaction."),
    UNSUBSCRIBE("/unsubscribe", "Unsubscribe from notifications."),
    HELP("/help", "Get instructions on how to use this bot.");

    @Getter
    private final String command;
    @Getter
    private final String description;

    BotCommands(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public static BotCommands fromCommand(String input) {
        return Arrays.stream(values())
                .filter(cmd -> cmd.getCommand().equalsIgnoreCase(input))
                .findFirst()
                .orElse(HELP);
    }
}
