package com.chertiavdev.bookingapp.dto.user.telegram;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO representing the notification status for a Telegram account")
public class UserTelegramStatusDto {
    @Schema(
            description = "Indicates whether Telegram notifications are enabled for the user",
            example = "true"
    )
    private boolean enabled;
}
