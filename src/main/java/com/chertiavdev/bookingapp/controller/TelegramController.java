package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.operations.ApiOperationDetails;
import com.chertiavdev.bookingapp.annotations.responses.groups.BaseAuthApiResponses;
import com.chertiavdev.bookingapp.dto.user.telegram.TelegramLinkDto;
import com.chertiavdev.bookingapp.dto.user.telegram.UserTelegramStatusDto;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.service.TelegramLinkService;
import com.chertiavdev.bookingapp.service.UserTelegramService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Telegram Link", description = "Generates a deep link for binding a Telegram account")
@RestController
@RequiredArgsConstructor
@RequestMapping("/telegram")
public class TelegramController {
    private final TelegramLinkService telegramLinkService;
    private final UserTelegramService userTelegramService;

    @ApiOperationDetails(
            summary = "Generate Telegram deep link",
            description = "Generates a deep link for the Telegram bot, allowing the user "
                    + "to bind their Telegram account to their application profile.",
            responseDescription = "Returns the generated deep link as a string."
    )
    @BaseAuthApiResponses
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/link")
    public TelegramLinkDto getLink(@AuthenticationPrincipal User user) {
        return telegramLinkService.createLink(user);
    }

    @ApiOperationDetails(
            summary = "Get notification status",
            description = "Checks whether notifications are enabled for the specified "
                    + "authenticated user",
            responseDescription = "Returns true if notifications are enabled, false otherwise"
    )
    @BaseAuthApiResponses
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/status")
    public UserTelegramStatusDto getNotificationStatus(@AuthenticationPrincipal User user) {
        return userTelegramService.getStatus(user.getId());
    }

    @ApiOperationDetails(
            summary = "Unlink Telegram account",
            description = "Unlinks the Telegram account associated with the authenticated "
                    + "user's account",
            responseDescription = "No content is returned upon successful unlinking"
    )
    @BaseAuthApiResponses
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @DeleteMapping("/link")
    public void unlink(@AuthenticationPrincipal User user) {
        userTelegramService.unlinkByUserId(user.getId());
    }
}
