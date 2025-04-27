package com.chertiavdev.bookingapp.service.impl;

import com.chertiavdev.bookingapp.dto.user.telegram.UserTelegramStatusDto;
import com.chertiavdev.bookingapp.mapper.UserTelegramMapper;
import com.chertiavdev.bookingapp.model.Role.RoleName;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.model.UserTelegram;
import com.chertiavdev.bookingapp.repository.user.telegram.UserTelegramRepository;
import com.chertiavdev.bookingapp.service.UserService;
import com.chertiavdev.bookingapp.service.UserTelegramService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserTelegramServiceImpl implements UserTelegramService {
    private final UserTelegramRepository userTelegramRepository;
    private final UserTelegramMapper userTelegramMapper;
    private final UserService userService;

    @Transactional
    @Override
    public void create(User user, Long chatId) {
        userTelegramRepository.save(userTelegramMapper.toModel(user, chatId));
    }

    @Transactional
    @Override
    public void update(UserTelegram userTelegram, Long chatId) {
        if (userTelegram.isDeleted()) {
            userTelegramRepository.restoreUserTelegram(userTelegram.getId(), chatId);
        } else {
            userTelegram.setChatId(chatId);
            userTelegramRepository.save(userTelegram);
        }
    }

    @Transactional
    @Override
    public void link(Long userId, Long chatId) {
        User user = userService.findById(userId);
        userTelegramRepository.findByUserId(userId)
                .ifPresentOrElse(
                        userTelegram -> update(userTelegram, chatId),
                        () -> create(user, chatId)
                );
    }

    @Override
    public UserTelegramStatusDto getStatus(Long userId) {
        return userTelegramMapper
                .toUserTelegramStatusDto(userTelegramRepository.existsByUserId(userId));
    }

    @Transactional
    @Override
    public void unlinkByChatId(Long chatId) {
        userTelegramRepository.deleteByChatId(chatId);
    }

    @Transactional
    @Override
    public void unlinkByUserId(Long userId) {
        userTelegramRepository.deleteByUserId(userId);
    }

    @Override
    public List<UserTelegram> getAllUserByRole(RoleName roleName) {
        return userTelegramRepository.findAllByUserRoles(roleName);
    }

    @Override
    public Optional<UserTelegram> getByUserId(Long userId) {
        return userTelegramRepository.findByUserId(userId);
    }
}
