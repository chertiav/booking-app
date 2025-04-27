package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.user.telegram.UserTelegramStatusDto;
import com.chertiavdev.bookingapp.model.Role.RoleName;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.model.UserTelegram;
import java.util.List;
import java.util.Optional;

public interface UserTelegramService {
    void update(UserTelegram userTelegram, Long chatId);

    void create(User user, Long chatId);

    void link(Long userId, Long chatId);

    UserTelegramStatusDto getStatus(Long id);

    void unlinkByChatId(Long chatId);

    void unlinkByUserId(Long userId);

    List<UserTelegram> getAllUserByRole(RoleName roleName);

    Optional<UserTelegram> getByUserId(Long userId);
}
