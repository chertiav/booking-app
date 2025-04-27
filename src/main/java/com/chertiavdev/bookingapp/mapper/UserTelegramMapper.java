package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.dto.user.telegram.UserTelegramStatusDto;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.model.UserTelegram;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserTelegramMapper {
    @Mapping(target = "user", source = "user")
    @Mapping(target = "chatId", source = "chatId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    UserTelegram toModel(User user, Long chatId);

    UserTelegramStatusDto toUserTelegramStatusDto(Boolean enabled);
}
