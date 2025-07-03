package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.user.telegram.TelegramLinkDto;
import com.chertiavdev.bookingapp.model.User;
import java.time.Instant;
import java.util.Optional;

public interface TelegramLinkService {
    TelegramLinkDto createLink(User user);

    Optional<Long> getUserIdByToken(String token);

    void deleteByExpiresAtBefore(Instant now);
}
