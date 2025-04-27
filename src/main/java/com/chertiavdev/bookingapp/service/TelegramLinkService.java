package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.user.telegram.TelegramLinkRequestDto;
import com.chertiavdev.bookingapp.model.User;
import java.util.Optional;

public interface TelegramLinkService {
    TelegramLinkRequestDto createLink(User user);

    Optional<Long> getUserIdByToken(String token);
}
