package com.chertiavdev.bookingapp.telegram;

import java.util.Arrays;
import java.util.List;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public class SetMyCommandsCommandFactory {
    public static SetMyCommands getDefaultCommands() {
        List<BotCommand> commands = Arrays.stream(BotCommands.values())
                .map(cmd -> new BotCommand(cmd.getCommand(), cmd.getDescription()))
                .toList();

        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(commands);

        return setMyCommands;
    }
}
