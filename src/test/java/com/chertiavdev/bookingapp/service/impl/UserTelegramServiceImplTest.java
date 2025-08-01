package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_TELEGRAM_CHAT_ID;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.USER_TELEGRAM_NEW_CHAT_ID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.chertiavdev.bookingapp.data.builders.UserTelegramTestDataBuilder;
import com.chertiavdev.bookingapp.data.builders.UserTestDataBuilder;
import com.chertiavdev.bookingapp.dto.user.telegram.UserTelegramStatusDto;
import com.chertiavdev.bookingapp.mapper.UserTelegramMapper;
import com.chertiavdev.bookingapp.model.Role;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.model.UserTelegram;
import com.chertiavdev.bookingapp.repository.user.telegram.UserTelegramRepository;
import com.chertiavdev.bookingapp.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserTelegram Service Implementation Test")
class UserTelegramServiceImplTest {
    private UserTelegramTestDataBuilder userTelegramTestDataBuilder;
    @InjectMocks
    private UserTelegramServiceImpl userTelegramService;
    @Mock
    private UserTelegramRepository userTelegramRepository;
    @Mock
    private UserTelegramMapper userTelegramMapper;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        userTelegramTestDataBuilder = new UserTelegramTestDataBuilder(
                new UserTestDataBuilder()
        );
    }

    @Test
    @DisplayName("Create new UserTelegram when valid User and chatId are provided")
    void create_ValidUserAndChatId_ShouldCreateNewUserTelegram() {
        //Given
        User user = userTelegramTestDataBuilder.getUserJohn();
        UserTelegram userTelegramToModel = userTelegramTestDataBuilder.getUserTelegramJohn();
        UserTelegram expected = userTelegramTestDataBuilder.getUserTelegramJohn();

        when(userTelegramMapper.toModel(user, USER_TELEGRAM_CHAT_ID))
                .thenReturn(userTelegramToModel);
        when(userTelegramRepository.save(userTelegramToModel)).thenReturn(expected);

        //When
        assertDoesNotThrow(() -> userTelegramService.create(user, USER_TELEGRAM_CHAT_ID));

        //Then
        verify(userTelegramMapper).toModel(user, USER_TELEGRAM_CHAT_ID);
        verify(userTelegramRepository).save(userTelegramToModel);
        verifyNoMoreInteractions(userTelegramMapper, userTelegramRepository);
    }

    @Test
    @DisplayName("Updating a UserTelegram successfully when it isn't deleted "
            + "and valid data is provided")
    void update_ValidDataAndUserTelegramNotDeleted_ShouldUpdateSuccessfully() {
        //Given
        UserTelegram existedUserTelegram = userTelegramTestDataBuilder.getUserTelegramJohn();
        UserTelegram updatedUserTelegram = userTelegramTestDataBuilder.getUpdatedUserTelegramJohn();

        when(userTelegramRepository.save(updatedUserTelegram)).thenReturn(updatedUserTelegram);

        //When
        assertDoesNotThrow(() -> userTelegramService
                .update(existedUserTelegram, USER_TELEGRAM_NEW_CHAT_ID));

        //Then
        verify(userTelegramRepository).save(updatedUserTelegram);
        verifyNoMoreInteractions(userTelegramRepository);
    }

    @Test
    @DisplayName("Updating a UserTelegram successfully when it is deleted and valid data "
            + "is provided")
    void update_ValidDataAndUserTelegramIsDeleted_ShouldRestoreAndUpdateSuccessfully() {
        //Given
        UserTelegram existedUserTelegram = userTelegramTestDataBuilder
                .getDeletedUserTelegramSansa();

        doNothing().when(userTelegramRepository).restoreUserTelegramAndUpdateChatId(
                existedUserTelegram.getId(),
                USER_TELEGRAM_NEW_CHAT_ID
        );

        //When
        assertDoesNotThrow(() -> userTelegramService
                .update(existedUserTelegram, USER_TELEGRAM_NEW_CHAT_ID));

        //Then
        verify(userTelegramRepository).restoreUserTelegramAndUpdateChatId(
                existedUserTelegram.getId(), USER_TELEGRAM_NEW_CHAT_ID);
        verifyNoMoreInteractions(userTelegramRepository);
    }

    @Test
    @DisplayName("Linking a UserTelegram successfully when no link is present")
    void link_NoLinkPresent_ShouldLinkSuccessfully() {
        //Given
        User user = userTelegramTestDataBuilder.getUserJohn();
        UserTelegram userTelegramToModel = userTelegramTestDataBuilder.getUserTelegramJohnToModel();
        UserTelegram savedUserTelegram = userTelegramTestDataBuilder.getUserTelegramJohn();

        when(userService.findById(user.getId())).thenReturn(user);
        when(userTelegramRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(userTelegramMapper.toModel(user, USER_TELEGRAM_CHAT_ID))
                .thenReturn(userTelegramToModel);
        when(userTelegramRepository.save(userTelegramToModel)).thenReturn(savedUserTelegram);

        //When
        assertDoesNotThrow(() -> userTelegramService
                .link(user.getId(), USER_TELEGRAM_CHAT_ID));

        //Then
        verify(userService).findById(user.getId());
        verify(userTelegramRepository).findByUserId(user.getId());
        verify(userTelegramMapper).toModel(user, USER_TELEGRAM_CHAT_ID);
        verify(userTelegramRepository).save(userTelegramToModel);
        verifyNoMoreInteractions(userService, userTelegramRepository, userTelegramMapper);
    }

    @Test
    @DisplayName("Linking a UserTelegram successfully when a link is present "
            + "and it has not been deleted")
    void link_LinkPresentAndNotDeleted_ShouldLinkSuccessfully() {
        //Given
        User user = userTelegramTestDataBuilder.getUserJohn();
        UserTelegram existedUserTelegram = userTelegramTestDataBuilder.getUserTelegramJohn();
        UserTelegram updatedUserTelegram = userTelegramTestDataBuilder.getUpdatedUserTelegramJohn();

        when(userService.findById(user.getId())).thenReturn(user);
        when(userTelegramRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(existedUserTelegram));
        when(userTelegramRepository.save(updatedUserTelegram)).thenReturn(updatedUserTelegram);

        //When
        assertDoesNotThrow(() -> userTelegramService
                .link(user.getId(), USER_TELEGRAM_NEW_CHAT_ID));

        //Then
        verify(userService).findById(user.getId());
        verify(userTelegramRepository).findByUserId(user.getId());
        verify(userTelegramRepository).save(updatedUserTelegram);
        verifyNoMoreInteractions(userService, userTelegramRepository);
    }

    @Test
    @DisplayName("Linking a UserTelegram successfully when a link is present "
            + "and it has been deleted")
    void link_LinkPresentAndDeleted_ShouldRestoreAndUpdateSuccessfullyAndLinkSuccessfully() {
        //Given
        User user = userTelegramTestDataBuilder.getUserSansa();
        UserTelegram existedUserTelegram = userTelegramTestDataBuilder
                .getDeletedUserTelegramSansa();

        when(userService.findById(user.getId())).thenReturn(user);
        when(userTelegramRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(existedUserTelegram));
        doNothing().when(userTelegramRepository).restoreUserTelegramAndUpdateChatId(
                existedUserTelegram.getId(), USER_TELEGRAM_NEW_CHAT_ID);

        //When
        assertDoesNotThrow(() -> userTelegramService
                .link(user.getId(), USER_TELEGRAM_NEW_CHAT_ID));

        //Then
        verify(userService).findById(user.getId());
        verify(userTelegramRepository).findByUserId(user.getId());
        verify(userTelegramRepository).restoreUserTelegramAndUpdateChatId(
                existedUserTelegram.getId(), USER_TELEGRAM_NEW_CHAT_ID);
        verifyNoMoreInteractions(userService, userTelegramRepository);
    }

    @Test
    @DisplayName("Getting a UserTelegram status successfully when it has status true and "
            + "valid data is provided should return UserTelegramStatusDto")
    void getStatus_ValidDataAndStatusTrue_ShouldReturnUserTelegramStatusDto() {
        //Given
        User user = userTelegramTestDataBuilder.getUserJohn();
        UserTelegramStatusDto expected = userTelegramTestDataBuilder.getUserTelegramStatusDtoJohn();

        when(userTelegramRepository.existsByUserId(user.getId())).thenReturn(true);
        when(userTelegramMapper.toUserTelegramStatusDto(true)).thenReturn(expected);

        //When
        UserTelegramStatusDto actual = userTelegramService.getStatus(user.getId());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual,
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userTelegramRepository).existsByUserId(user.getId());
        verify(userTelegramMapper).toUserTelegramStatusDto(true);
        verifyNoMoreInteractions(userTelegramRepository, userTelegramMapper);
    }

    @Test
    @DisplayName("Getting a UserTelegram status successfully when it has status false and"
            + "valid data is provided should return UserTelegramStatusDto")
    void getStatus_ValidDataAndStatusFalse_ShouldReturnUserTelegramStatusDto() {
        //Given
        User user = userTelegramTestDataBuilder.getUserSansa();
        UserTelegramStatusDto expected = userTelegramTestDataBuilder
                .getUserTelegramStatusDtoSansa();

        when(userTelegramRepository.existsByUserId(user.getId())).thenReturn(false);
        when(userTelegramMapper.toUserTelegramStatusDto(false)).thenReturn(expected);

        //When
        UserTelegramStatusDto actual = userTelegramService.getStatus(user.getId());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual,
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userTelegramRepository).existsByUserId(user.getId());
        verify(userTelegramMapper).toUserTelegramStatusDto(false);
        verifyNoMoreInteractions(userTelegramRepository, userTelegramMapper);
    }

    @Test
    @DisplayName("Unlinking a UserTelegram by chat ID successfully when valid data is provided")
    void unlinkByChatId_ValidData_ShouldUnlinkSuccessfully() {
        //Given
        doNothing().when(userTelegramRepository).deleteByChatId(USER_TELEGRAM_CHAT_ID);

        //When
        assertDoesNotThrow(() -> userTelegramService.unlinkByChatId(USER_TELEGRAM_CHAT_ID));

        //Then
        verify(userTelegramRepository).deleteByChatId(USER_TELEGRAM_CHAT_ID);
        verifyNoMoreInteractions(userTelegramRepository);
    }

    @Test
    @DisplayName("Unlinking a UserTelegram by user ID successfully when valid data is provided")
    void unlinkByUserId_ValidData_ShouldUnlinkSuccessfully() {
        //Given
        doNothing().when(userTelegramRepository).deleteByUserId(SAMPLE_TEST_ID_1);

        //When
        assertDoesNotThrow(() -> userTelegramService.unlinkByUserId(SAMPLE_TEST_ID_1));

        //Then
        verify(userTelegramRepository).deleteByUserId(SAMPLE_TEST_ID_1);
        verifyNoMoreInteractions(userTelegramRepository);
    }

    @Test
    @DisplayName("Getting all UserTelegram by role successfully when valid role is provided")
    void getAllUserByRole_ValidRole_ShouldReturnListOfUserTelegram() {
        //Given
        UserTelegram userTelegram = userTelegramTestDataBuilder.getUserTelegramJohn();
        List<UserTelegram> expected = List.of(userTelegram);

        when(userTelegramRepository.findAllByUserRoles(Role.RoleName.USER)).thenReturn(expected);

        //When
        List<UserTelegram> actual = userTelegramService.getAllUserByRole(Role.RoleName.USER);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual,
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userTelegramRepository).findAllByUserRoles(Role.RoleName.USER);
        verifyNoMoreInteractions(userTelegramRepository);
    }

    @Test
    @DisplayName("Getting UserTelegram by userId successfully when valid userId is provided")
    void getByUserId_ValidUserId_ShouldReturnUserTelegram() {
        //Given
        User user = userTelegramTestDataBuilder.getUserJohn();
        UserTelegram userTelegram = userTelegramTestDataBuilder.getUserTelegramJohn();
        Optional<UserTelegram> expected = Optional.of(userTelegram);

        when(userTelegramRepository.findByUserId(user.getId())).thenReturn(expected);

        //When
        Optional<UserTelegram> actual = userTelegramService.getByUserId(user.getId());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual,
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userTelegramRepository).findByUserId(user.getId());
        verifyNoMoreInteractions(userTelegramRepository);
    }
}
