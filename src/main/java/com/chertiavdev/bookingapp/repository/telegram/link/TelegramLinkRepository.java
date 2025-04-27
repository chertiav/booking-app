package com.chertiavdev.bookingapp.repository.telegram.link;

import com.chertiavdev.bookingapp.model.TelegramLink;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramLinkRepository extends JpaRepository<TelegramLink, Long> {
    Optional<TelegramLink> findByUserId(Long userId);

    Optional<TelegramLink> findByToken(String token);
}
