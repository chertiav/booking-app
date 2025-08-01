package com.chertiavdev.bookingapp.data.builders;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_TELEGRAM_CHAT_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_TELEGRAM_NEW_CHAT_ID;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestUserTelegram;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestUserTelegramStatusDto;

import com.chertiavdev.bookingapp.dto.user.telegram.UserTelegramStatusDto;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.model.UserTelegram;
import lombok.Getter;

@Getter
public class UserTelegramTestDataBuilder {
    private final User userJohn;
    private final User userSansa;
    private final UserTelegram userTelegramJohn;
    private final UserTelegram userTelegramJohnToModel;
    private final UserTelegram updatedUserTelegramJohn;
    private final UserTelegram deletedUserTelegramSansa;

    public UserTelegramTestDataBuilder(UserTestDataBuilder userTestDataBuilder) {
        userJohn = userTestDataBuilder.getUserJohn();
        userSansa = userTestDataBuilder.getUserSansa();
        userTelegramJohn = createUserTelegramJohn();
        userTelegramJohnToModel = createUserTelegramJohnToModel();
        updatedUserTelegramJohn = createUpdatedUserTelegramJohn();
        deletedUserTelegramSansa = createDeletedUserTelegramSansa();
    }

    public UserTelegramStatusDto getUserTelegramStatusDtoJohn() {
        return createTestUserTelegramStatusDto(userTelegramJohn);
    }

    public UserTelegramStatusDto getUserTelegramStatusDtoSansa() {
        return createTestUserTelegramStatusDto(deletedUserTelegramSansa);
    }

    private UserTelegram createUserTelegramJohn() {
        return createTestUserTelegram(
                SAMPLE_TEST_ID_1,
                userJohn,
                USER_TELEGRAM_CHAT_ID,
                false
        );
    }

    private UserTelegram createUserTelegramJohnToModel() {
        return createTestUserTelegram(
                null,
                userJohn,
                USER_TELEGRAM_CHAT_ID,
                false
        );
    }

    private UserTelegram createUpdatedUserTelegramJohn() {
        return createTestUserTelegram(
                SAMPLE_TEST_ID_1,
                userJohn,
                USER_TELEGRAM_NEW_CHAT_ID,
                false
        );
    }

    private UserTelegram createDeletedUserTelegramSansa() {
        return createTestUserTelegram(
                SAMPLE_TEST_ID_2,
                userSansa,
                USER_TELEGRAM_CHAT_ID,
                true
        );
    }
}
