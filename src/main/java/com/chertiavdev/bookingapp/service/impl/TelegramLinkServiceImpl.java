package com.chertiavdev.bookingapp.service.impl;

import com.chertiavdev.bookingapp.dto.user.telegram.TelegramLinkRequestDto;
import com.chertiavdev.bookingapp.mapper.TelegramLinkMapper;
import com.chertiavdev.bookingapp.model.TelegramLink;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.telegram.link.TelegramLinkRepository;
import com.chertiavdev.bookingapp.service.TelegramLinkService;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TelegramLinkServiceImpl implements TelegramLinkService {
    private static final String TELEGRAM_LINK_TEMPLATE = "https://t.me/%s?start=%s";
    private static final int NUMBER_OF_MINUTES = 10;
    private final TelegramLinkRepository telegramLinkRepository;
    private final TelegramLinkMapper telegramLinkMapper;
    @Value("${telegram.bot.username}")
    private String telegramBotUsername;

    @Transactional
    @Override
    public TelegramLinkRequestDto createLink(User user) {
        telegramLinkRepository.findByUserId(user.getId())
                .ifPresent(telegramLinkRepository::delete);

        String token = UUID.randomUUID().toString().replace("-", "");
        TelegramLink telegramLink = telegramLinkMapper.toModel(user, token, NUMBER_OF_MINUTES);
        telegramLinkRepository.save(telegramLink);

        return telegramLinkMapper.toDto(
                telegramLinkRepository.save(telegramLink),
                TELEGRAM_LINK_TEMPLATE,
                telegramBotUsername);
    }

    @Transactional
    @Override
    public Optional<Long> getUserIdByToken(String token) {
        return telegramLinkRepository.findByToken(token)
                .filter(link -> link.getExpiresAt().isAfter(Instant.now()))
                .map(link -> {
                    telegramLinkRepository.delete(link);
                    return link.getUser().getId();
                });
    }

    @Transactional
    @Override
    public void deleteByExpiresAtBefore(Instant now) {
        telegramLinkRepository.deleteByExpiresAtBefore(now);
    }
}
