package com.chertiavdev.bookingapp.data.builders;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.NUMBER_OF_MINUTES;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TEST_TOKEN_CURRENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.TEST_TOKEN_EXPIRED;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.calculateExpirationInstant;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestTelegramLink;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestTelegramLinkDto;

import com.chertiavdev.bookingapp.dto.user.telegram.TelegramLinkDto;
import com.chertiavdev.bookingapp.model.TelegramLink;
import com.chertiavdev.bookingapp.model.User;
import lombok.Getter;

@Getter
public class UserTelegramLinkTestDataBuilder {
    private final User userJohn;
    private final User userSansa;
    private final TelegramLink telegramLinkJohn;
    private final TelegramLink expiredTelegramLinkJohn;
    private final TelegramLink telegramLinkJohnToModel;
    private final TelegramLinkDto telegramLinkDtoJohn;

    public UserTelegramLinkTestDataBuilder(UserTestDataBuilder userTestDataBuilder) {
        this.userJohn = userTestDataBuilder.getUserJohn();
        this.userSansa = userTestDataBuilder.getUserSansa();
        this.telegramLinkJohn = createTelegramLinkJohn();
        this.expiredTelegramLinkJohn = createExpiredTegramLinkJohn();
        this.telegramLinkJohnToModel = createTelegramLinkJohnToModel();
        this.telegramLinkDtoJohn = createTegramLinkDtoJohn();
    }

    private TelegramLink createTelegramLinkJohn() {
        return createTestTelegramLink(
                SAMPLE_TEST_ID_1,
                userJohn,
                TEST_TOKEN_CURRENT,
                calculateExpirationInstant(NUMBER_OF_MINUTES, true),
                false
        );
    }

    private TelegramLink createExpiredTegramLinkJohn() {
        return createTestTelegramLink(
                SAMPLE_TEST_ID_2,
                userSansa,
                TEST_TOKEN_EXPIRED,
                calculateExpirationInstant(NUMBER_OF_MINUTES, false),
                false
        );
    }

    private TelegramLink createTelegramLinkJohnToModel() {
        return createTestTelegramLink(
                null,
                userJohn,
                TEST_TOKEN_CURRENT,
                calculateExpirationInstant(NUMBER_OF_MINUTES, true),
                false
        );
    }

    private TelegramLinkDto createTegramLinkDtoJohn() {
        return createTestTelegramLinkDto(telegramLinkJohn);
    }
}
