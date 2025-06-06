package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.model.Role.RoleName.USER;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.DEFAULT_NOTIFICATION_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.NOTIFICATION_SEND_ERROR_PREFIX;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_TELEGRAM_CHAT_ID;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestUserTelegram;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createUserRegisterRequest;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createUserRole;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.initializeUser;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.chertiavdev.bookingapp.exception.NotificationException;
import com.chertiavdev.bookingapp.model.Role;
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
    @InjectMocks
    private NotificationServiceImpl notificationService;
    @Mock
    private UserTelegramService userTelegramService;
    @Mock
    private TelegramNotificationBot telegramNotificationBot;

    @BeforeEach
    void setUp() {
        AsyncTaskExecutor executor = new ConcurrentTaskExecutor(new SyncTaskExecutor());
    }

    @Test
    @DisplayName("Send notification to all users with target role")
    void sendNotification_Valid_ShouldSendNotificationToAllUsersWithTargetRole() {
        //Given
        User user = initializeUser(createUserRegisterRequest(),
                createUserRole(Role.RoleName.USER, SAMPLE_TEST_ID_1), SAMPLE_TEST_ID_1);
        UserTelegram userTelegram = createTestUserTelegram(user, USER_TELEGRAM_CHAT_ID);
        userTelegram.setId(SAMPLE_TEST_ID_1);

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
        User user = initializeUser(createUserRegisterRequest(),
                createUserRole(Role.RoleName.USER, SAMPLE_TEST_ID_1), SAMPLE_TEST_ID_1);
        UserTelegram userTelegram = createTestUserTelegram(user, USER_TELEGRAM_CHAT_ID);
        userTelegram.setId(SAMPLE_TEST_ID_1);

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
        when(userTelegramService.getByUserId(SAMPLE_TEST_ID_1)).thenReturn(Optional.empty());

        //When
        notificationService
                .sendNotificationByUserId(DEFAULT_NOTIFICATION_MESSAGE, SAMPLE_TEST_ID_1);

        //Then
        verify(userTelegramService).getByUserId(SAMPLE_TEST_ID_1);
    }

    @Test
    @DisplayName("Should handle NotificationException and continue execution")
    void sendNotificationByUserId_WithTelegramBotThrowingException_ShouldHandleException() {
        // Given
        User user = initializeUser(createUserRegisterRequest(),
                createUserRole(Role.RoleName.USER, SAMPLE_TEST_ID_1), SAMPLE_TEST_ID_1);
        UserTelegram userTelegram = createTestUserTelegram(user, USER_TELEGRAM_CHAT_ID);
        userTelegram.setId(SAMPLE_TEST_ID_1);

        when(userTelegramService.getByUserId(SAMPLE_TEST_ID_1))
                .thenReturn(Optional.of(userTelegram));

        doThrow(new NotificationException(NOTIFICATION_SEND_ERROR_PREFIX
                + DEFAULT_NOTIFICATION_MESSAGE))
                .when(telegramNotificationBot)
                .sendNotification(userTelegram.getChatId(), DEFAULT_NOTIFICATION_MESSAGE);

        // When
        notificationService
                .sendNotificationByUserId(DEFAULT_NOTIFICATION_MESSAGE, SAMPLE_TEST_ID_1);

        // Then
        verify(userTelegramService).getByUserId(SAMPLE_TEST_ID_1);
        verify(telegramNotificationBot)
                .sendNotification(userTelegram.getChatId(), DEFAULT_NOTIFICATION_MESSAGE);
        verifyNoMoreInteractions(userTelegramService);

        assertDoesNotThrow(() -> notificationService
                .sendNotificationByUserId(DEFAULT_NOTIFICATION_MESSAGE, SAMPLE_TEST_ID_1)
        );
    }
}
