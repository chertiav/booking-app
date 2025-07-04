package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.NUMBER_OF_MINUTES;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.TELEGRAM_LINK_TEMPLATE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.TEST_BOT_USERNAME;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.TEST_TOKEN_CURRENT;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.calculateExpirationInstant;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.chertiavdev.bookingapp.data.builders.UserTelegramLinkTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.UserTestDataBuilder;
import com.chertiavdev.bookingapp.dto.user.telegram.TelegramLinkDto;
import com.chertiavdev.bookingapp.mapper.TelegramLinkMapper;
import com.chertiavdev.bookingapp.model.TelegramLink;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.telegram.link.TelegramLinkRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("TelegramLink Service Implementation Test")
class TelegramLinkServiceImplTest {
    private UserTelegramLinkTestDataBuilder userTelegramLinkTestDataBuilder;
    @InjectMocks
    private TelegramLinkServiceImpl telegramLinkService;
    @Mock
    private TelegramLinkRepository telegramLinkRepository;
    @Mock
    private TelegramLinkMapper telegramLinkMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils
                .setField(telegramLinkService, "telegramBotUsername", TEST_BOT_USERNAME);
        userTelegramLinkTestDataBuilder = new UserTelegramLinkTestDataBuilder(
                new UserTestDataBuilder()
        );
    }

    @Test
    @DisplayName("Create link for user successfully when valid data is provided")
    void createLink_ValidData_ShouldReturnTelegramLinkRequestDto() {
        //Given
        User user = userTelegramLinkTestDataBuilder.getUserJohn();
        TelegramLink telegramLinkToModel = userTelegramLinkTestDataBuilder
                .getTelegramLinkJohnToModel();
        TelegramLink savedTelegramLink = userTelegramLinkTestDataBuilder.getTelegramLinkJohn();
        TelegramLinkDto expected = userTelegramLinkTestDataBuilder.getTelegramLinkDtoJohn();

        when(telegramLinkRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(telegramLinkMapper.toModel(eq(user), anyString(), eq(NUMBER_OF_MINUTES)))
                .thenReturn(telegramLinkToModel);
        when(telegramLinkRepository.save(eq(telegramLinkToModel))).thenReturn(savedTelegramLink);
        when(telegramLinkMapper
                .toDto(eq(savedTelegramLink), eq(TELEGRAM_LINK_TEMPLATE), eq(TEST_BOT_USERNAME)))
                .thenReturn(expected);

        //When
        TelegramLinkDto actual = telegramLinkService.createLink(user);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(telegramLinkRepository).findByUserId(eq(user.getId()));
        verify(telegramLinkMapper).toModel(eq(user), anyString(), eq(NUMBER_OF_MINUTES));
        verify(telegramLinkRepository).save(eq(telegramLinkToModel));
        verify(telegramLinkMapper)
                .toDto(eq(savedTelegramLink), eq(TELEGRAM_LINK_TEMPLATE), eq(TEST_BOT_USERNAME));
        verifyNoMoreInteractions(telegramLinkRepository, telegramLinkMapper);
    }

    @Test
    @DisplayName("Retrieving the user ID if the user has a current link, "
            + "and deleting the link afterwards when valid token is provided.")
    void getUserIdByToken_ExistingCurrentLink_ShouldReturnUserIdAndDeleteLink() {
        //Given
        User user = userTelegramLinkTestDataBuilder.getUserJohn();
        TelegramLink telegramLink = userTelegramLinkTestDataBuilder.getTelegramLinkJohn();

        when(telegramLinkRepository.findByToken(telegramLink.getToken()))
                .thenReturn(Optional.of(telegramLink));
        doNothing().when(telegramLinkRepository).delete(telegramLink);

        //When
        Optional<Long> actual = telegramLinkService.getUserIdByToken(telegramLink.getToken());

        //Then
        Optional<Long> expected = Optional.of(user.getId());

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(telegramLinkRepository).findByToken(TEST_TOKEN_CURRENT);
        verify(telegramLinkRepository).delete(telegramLink);
        verifyNoMoreInteractions(telegramLinkRepository);
    }

    @Test
    @DisplayName("Retrieving the user ID if the user hasn't a current link when valid token is"
            + " provided, should return EmptyOptional ")
    void getUserIdByToken_NonExistingLink_ShouldReturnEmptyOptional() {
        //Given
        TelegramLink expiredTelegramLink = userTelegramLinkTestDataBuilder
                .getExpiredTelegramLinkJohn();

        when(telegramLinkRepository.findByToken(expiredTelegramLink.getToken()))
                .thenReturn(Optional.of(expiredTelegramLink));

        //When
        Optional<Long> actual = telegramLinkService
                .getUserIdByToken(expiredTelegramLink.getToken());

        //Then
        assertEquals(Optional.empty(), actual,
                EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(telegramLinkRepository).findByToken(expiredTelegramLink.getToken());
        verifyNoMoreInteractions(telegramLinkRepository);
    }

    @Test
    @DisplayName("Retrieving a user ID should return EmptyOptional "
            + "if an invalid token is provided.")
    void getUserIdByToken_InvalidToken_ShouldReturnEmptyOptional() {
        //Given
        when(telegramLinkRepository.findByToken(TEST_TOKEN_CURRENT)).thenReturn(Optional.empty());

        //When
        Optional<Long> actual = telegramLinkService.getUserIdByToken(TEST_TOKEN_CURRENT);

        //Then
        assertEquals(Optional.empty(), actual,
                EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(telegramLinkRepository).findByToken(TEST_TOKEN_CURRENT);
        verifyNoMoreInteractions(telegramLinkRepository);
    }

    @Test
    @DisplayName("Should delete expired link entries when the expiration date is before now")
    void deleteByExpiresAtBefore_ValidDate_ShouldDeleteExpiredTelegramLinks() {
        // Given
        Instant expirationInstant = calculateExpirationInstant(NUMBER_OF_MINUTES, false);

        doNothing().when(telegramLinkRepository).deleteByExpiresAtBefore(expirationInstant);

        // When
        assertDoesNotThrow(() -> telegramLinkService.deleteByExpiresAtBefore(expirationInstant));

        // Then
        verify(telegramLinkRepository).deleteByExpiresAtBefore(expirationInstant);
        verifyNoMoreInteractions(telegramLinkRepository);
    }
}
