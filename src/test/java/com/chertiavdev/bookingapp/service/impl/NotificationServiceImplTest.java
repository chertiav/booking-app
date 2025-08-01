package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.model.Role.RoleName.USER;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.DEFAULT_NOTIFICATION_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.INVALID_TEST_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.NOTIFICATION_SEND_ERROR_PREFIX;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.chertiavdev.bookingapp.data.builders.UserTelegramTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.UserTestDataBuilder;
import com.chertiavdev.bookingapp.exception.NotificationException;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.model.UserTelegram;
import com.chertiavdev.bookingapp.service.UserTelegramService;
import com.chertiavdev.bookingapp.telegram.TelegramNotificationBot;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

@ExtendWith(MockitoExtension.class)
@DisplayName("Notification Service Implementation Test")
class NotificationServiceImplTest {
    private UserTelegramTestDataBuilder userTelegramTestDataBuilder;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    @Mock
    private UserTelegramService userTelegramService;
    @Mock
    private TelegramNotificationBot telegramNotificationBot;

    @BeforeEach
    void setUp() {
        AsyncTaskExecutor executor = new ConcurrentTaskExecutor(new SyncTaskExecutor());
        userTelegramTestDataBuilder = new UserTelegramTestDataBuilder(
                new UserTestDataBuilder()
        );
    }

    @Test
    @DisplayName("Send notification to all users with target role")
    void sendNotification_Valid_ShouldSendNotificationToAllUsersWithTargetRole() {
        //Given
        UserTelegram userTelegram = userTelegramTestDataBuilder.getUserTelegramJohn();

        when(userTelegramService.getAllUserByRole(USER)).thenReturn(List.of(userTelegram));

        //When
        notificationService.sendNotification(DEFAULT_NOTIFICATION_MESSAGE, USER);

        //Then
        verify(userTelegramService).getAllUserByRole(USER);
        verify(telegramNotificationBot)
                .sendNotification(userTelegram.getChatId(), DEFAULT_NOTIFICATION_MESSAGE);
        assertDoesNotThrow(() ->
                notificationService.sendNotification(DEFAULT_NOTIFICATION_MESSAGE, USER));
    }

    @Test
    @DisplayName("Send notification to user with target role by valid user id")
    void sendNotificationByUserId_ValidId_ShouldSendNotification() {
        //Given
        User user = userTelegramTestDataBuilder.getUserJohn();
        UserTelegram userTelegram = userTelegramTestDataBuilder.getUserTelegramJohn();

        when(userTelegramService.getByUserId(user.getId())).thenReturn(Optional.of(userTelegram));

        //When
        notificationService.sendNotificationByUserId(DEFAULT_NOTIFICATION_MESSAGE, user.getId());

        //Then
        assertDoesNotThrow(() ->
                notificationService.sendNotification(DEFAULT_NOTIFICATION_MESSAGE, USER));

        verify(userTelegramService).getByUserId(user.getId());
        verify(telegramNotificationBot)
                .sendNotification(userTelegram.getChatId(), DEFAULT_NOTIFICATION_MESSAGE);
    }

    @Test
    @DisplayName("Send notification to user with target role by invalid user id")
    void sendNotificationByUserId_InvalidId_ShouldNotSendNotificationToUser() {
        //Given
        when(userTelegramService.getByUserId(INVALID_TEST_ID)).thenReturn(Optional.empty());

        //When
        notificationService
                .sendNotificationByUserId(DEFAULT_NOTIFICATION_MESSAGE, INVALID_TEST_ID);

        //Then
        verify(userTelegramService).getByUserId(INVALID_TEST_ID);
    }

    @Test
    @DisplayName("Should handle NotificationException and continue execution")
    void sendNotificationByUserId_WithTelegramBotThrowingException_ShouldHandleException() {
        // Given
        User user = userTelegramTestDataBuilder.getUserJohn();
        UserTelegram userTelegram = userTelegramTestDataBuilder.getUserTelegramJohn();

        when(userTelegramService.getByUserId(user.getId()))
                .thenReturn(Optional.of(userTelegram));

        doThrow(new NotificationException(NOTIFICATION_SEND_ERROR_PREFIX
                + DEFAULT_NOTIFICATION_MESSAGE))
                .when(telegramNotificationBot)
                .sendNotification(userTelegram.getChatId(), DEFAULT_NOTIFICATION_MESSAGE);

        // When
        notificationService
                .sendNotificationByUserId(DEFAULT_NOTIFICATION_MESSAGE, user.getId());

        // Then
        verify(userTelegramService).getByUserId(user.getId());
        verify(telegramNotificationBot)
                .sendNotification(userTelegram.getChatId(), DEFAULT_NOTIFICATION_MESSAGE);
        verifyNoMoreInteractions(userTelegramService);

        assertDoesNotThrow(() -> notificationService
                .sendNotificationByUserId(DEFAULT_NOTIFICATION_MESSAGE, user.getId())
        );
    }
}
