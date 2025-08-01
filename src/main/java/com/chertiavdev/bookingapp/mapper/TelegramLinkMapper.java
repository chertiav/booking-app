package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.dto.user.telegram.TelegramLinkDto;
import com.chertiavdev.bookingapp.model.TelegramLink;
import com.chertiavdev.bookingapp.model.User;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface TelegramLinkMapper {
    @Mapping(target = "user", source = "user")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    TelegramLink toModel(User user, String token, int numberOfMinutes);

    @AfterMapping
    default void setExpiresAt(@MappingTarget TelegramLink telegramLink, int numberOfMinutes) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        telegramLink.setExpiresAt(now.plusMinutes(numberOfMinutes).toInstant());
    }

    @Mapping(target = "link",
            expression = "java(String.format(telegramLinkTemplate, "
                    + "telegramBotUsername, telegramLink.getToken()))")
    TelegramLinkDto toDto(
            TelegramLink telegramLink,
            String telegramLinkTemplate,
            String telegramBotUsername
    );
}
