package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.DEFAULT_TEST_TOKEN;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.NUMBER_OF_MINUTES;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.TELEGRAM_LINK_TEMPLATE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.TEST_BOT_USERNAME;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.calculateExpirationInstant;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTelegramLink;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTelegramLinkRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.chertiavdev.bookingapp.dto.user.telegram.TelegramLinkRequestDto;
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
    }

    @Test
    @DisplayName("Create link for user successfully when valid data is provided")
    void createLink_ValidData_ShouldReturnTelegramLinkRequestDto() {
        //Given
        User user = createTestUser();
        Instant expirationInstant = calculateExpirationInstant(NUMBER_OF_MINUTES, true);

        TelegramLink telegramLink = createTelegramLink(
                user, DEFAULT_TEST_TOKEN, expirationInstant, false);
        TelegramLink savedTelegramLink = createTelegramLink(
                user, DEFAULT_TEST_TOKEN, expirationInstant, false);
        savedTelegramLink.setId(SAMPLE_TEST_ID_1);

        TelegramLinkRequestDto expected = createTelegramLinkRequestDto(savedTelegramLink);

        when(telegramLinkRepository.findByUserId(eq(user.getId()))).thenReturn(Optional.empty());
        when(telegramLinkMapper.toModel(eq(user), anyString(), eq(NUMBER_OF_MINUTES)))
                .thenReturn(telegramLink);
        when(telegramLinkRepository.save(eq(telegramLink))).thenReturn(savedTelegramLink);
        when(telegramLinkMapper
                .toDto(eq(savedTelegramLink), eq(TELEGRAM_LINK_TEMPLATE), eq(TEST_BOT_USERNAME)))
                .thenReturn(expected);

        //When
        TelegramLinkRequestDto actual = telegramLinkService.createLink(user);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(telegramLinkRepository).findByUserId(eq(user.getId()));
        verify(telegramLinkMapper).toModel(eq(user), anyString(), eq(NUMBER_OF_MINUTES));
        verify(telegramLinkRepository).save(eq(telegramLink));
        verify(telegramLinkMapper)
                .toDto(eq(savedTelegramLink), eq(TELEGRAM_LINK_TEMPLATE), eq(TEST_BOT_USERNAME));
        verifyNoMoreInteractions(telegramLinkRepository, telegramLinkMapper);
    }

    @Test
    @DisplayName("Retrieving the user ID if the user has a current link, "
            + "and deleting the link afterwards when valid token is provided.")
    void getUserIdByToken_ExistingCurrentLink_ShouldReturnUserIdAndDeleteLink() {
        //Given
        User user = createTestUser();
        Instant expirationInstant = calculateExpirationInstant(NUMBER_OF_MINUTES, true);

        TelegramLink telegramLink = createTelegramLink(
                user, DEFAULT_TEST_TOKEN, expirationInstant, false);
        telegramLink.setId(SAMPLE_TEST_ID_1);

        when(telegramLinkRepository.findByToken(DEFAULT_TEST_TOKEN))
                .thenReturn(Optional.of(telegramLink));
        doNothing().when(telegramLinkRepository).delete(telegramLink);

        //When
        Optional<Long> actual = telegramLinkService.getUserIdByToken(DEFAULT_TEST_TOKEN);

        //Then
        Optional<Long> expected = Optional.of(user.getId());

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(telegramLinkRepository).findByToken(DEFAULT_TEST_TOKEN);
        verify(telegramLinkRepository).delete(telegramLink);
        verifyNoMoreInteractions(telegramLinkRepository);
    }

    @Test
    @DisplayName("Retrieving the user ID if the user hasn't a current link when valid token is"
            + " provided, should return EmptyOptional ")
    void getUserIdByToken_NonExistingLink_ShouldReturnEmptyOptional() {
        //Given
        User user = createTestUser();
        Instant expirationInstant = calculateExpirationInstant(NUMBER_OF_MINUTES, false);

        TelegramLink telegramLink = createTelegramLink(
                user, DEFAULT_TEST_TOKEN, expirationInstant, false);
        telegramLink.setId(SAMPLE_TEST_ID_1);

        when(telegramLinkRepository.findByToken(DEFAULT_TEST_TOKEN))
                .thenReturn(Optional.of(telegramLink));

        //When
        Optional<Long> actual = telegramLinkService.getUserIdByToken(DEFAULT_TEST_TOKEN);

        //Then
        assertEquals(Optional.empty(), actual,
                EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(telegramLinkRepository).findByToken(DEFAULT_TEST_TOKEN);
        verifyNoMoreInteractions(telegramLinkRepository);
    }

    @Test
    @DisplayName("Retrieving a user ID should return EmptyOptional "
            + "if an invalid token is provided.")
    void getUserIdByToken_InvalidToken_ShouldReturnEmptyOptional() {
        //Given
        when(telegramLinkRepository.findByToken(DEFAULT_TEST_TOKEN)).thenReturn(Optional.empty());

        //When
        Optional<Long> actual = telegramLinkService.getUserIdByToken(DEFAULT_TEST_TOKEN);

        //Then
        assertEquals(Optional.empty(), actual,
                EXCEPTION_MESSAGE_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(telegramLinkRepository).findByToken(DEFAULT_TEST_TOKEN);
        verifyNoMoreInteractions(telegramLinkRepository);
    }

    @Test
    @DisplayName("Should delete expired link entries when the expiration date is before now")
    void deleteByExpiresAtBefore_ValidDate_ShouldDeleteExpiredTelegramLinks() {
        // Given
        Instant expirationInstant = calculateExpirationInstant(NUMBER_OF_MINUTES, false);

        doNothing().when(telegramLinkRepository).deleteByExpiresAtBefore(expirationInstant);

        // When
        telegramLinkService.deleteByExpiresAtBefore(expirationInstant);

        // Then
        verify(telegramLinkRepository).deleteByExpiresAtBefore(expirationInstant);
        verifyNoMoreInteractions(telegramLinkRepository);
    }
}
