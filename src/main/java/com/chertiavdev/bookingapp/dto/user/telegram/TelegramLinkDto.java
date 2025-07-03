package com.chertiavdev.bookingapp.dto.user.telegram;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO  used for connecting a user to the Telegram notification bot using "
        + "a deep link.")
public class TelegramLinkDto {
    @Schema(
            description = "Deep-link used to connect the user to the Telegram notification bot. "
                    + "The link should include the bot's username and a unique start token for "
                    + "authentication.",
            example = "https://t.me/notification_bot?start=91c7d0a6d3584f31885a313add040708"
    )
    private String link;
}
