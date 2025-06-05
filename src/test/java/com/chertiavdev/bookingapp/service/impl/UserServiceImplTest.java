package com.chertiavdev.bookingapp.service.impl;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.EMAIL_PREFIX;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.ROLE_NOT_FOUND_ERROR_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USERNAME_UPDATE_FIRST;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USERNAME_UPDATE_LAST;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_ALREADY_EXISTS_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_EMAIL_EXAMPLE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_ID_PREFIX;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_NOT_FOUND_ERROR_MESSAGE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE;
import static com.chertiavdev.bookingapp.utils.constants.TestConstants.ACTUAL_RESULT_SHOULD_NOT_BE_NULL;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createUserFromDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createUserRegisterRequest;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createUserRole;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createUserUpdateRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createUserUpdateRoleRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.initializeUser;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.mapToUserDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.mapToUserWithRoleDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.updateNamesUser;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.updateNamesUserDto;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserRegisterRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRoleRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserWithRoleDto;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.exception.RegistrationException;
import com.chertiavdev.bookingapp.mapper.UserMapper;
import com.chertiavdev.bookingapp.model.Role;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.role.RoleRepository;
import com.chertiavdev.bookingapp.repository.user.UserRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Implementation Test")
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Registering a user successfully when valid data is provided")
    void register_ValidData_ShouldReturnSavedUserDto() throws RegistrationException {
        //Given
        UserRegisterRequestDto requestDto = createUserRegisterRequest();
        User userToModel = createUserFromDto(requestDto);
        Role userRole = createUserRole(Role.RoleName.USER, SAMPLE_TEST_ID_1);
        User savedUser = initializeUser(requestDto, userRole, SAMPLE_TEST_ID_1);
        UserDto expected = mapToUserDto(savedUser);

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(userToModel);
        when(roleRepository.findByName(Role.RoleName.USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(userToModel)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expected);

        //When
        UserDto actual = userService.register(requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userRepository).existsByEmail(requestDto.getEmail());
        verify(userMapper).toModel(requestDto);
        verify(roleRepository).findByName(Role.RoleName.USER);
        verify(userRepository).save(userToModel);
        verify(userMapper).toDto(savedUser);
        verifyNoMoreInteractions(userRepository, userMapper, roleRepository);
    }

    @Test
    @DisplayName("Registering a user should throw an exception when the user's "
            + "email already exists")
    void register_ExistingEmail_ShouldReturnException() {
        //Given
        UserRegisterRequestDto requestDto = createUserRegisterRequest();

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        //When
        Exception exception = assertThrows(RegistrationException.class,
                () -> userService.register(requestDto));

        //Then
        String expected = String.format(USER_ALREADY_EXISTS_MESSAGE, requestDto.getEmail());
        String actual = exception.getMessage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userRepository).existsByEmail(requestDto.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Registering a user should trow an exception when an invalid role is provided")
    void register_InvalidRole_ShouldReturnException() {
        //Given
        UserRegisterRequestDto requestDto = createUserRegisterRequest();
        User userToModel = createUserFromDto(requestDto);

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(userToModel);
        when(roleRepository.findByName(Role.RoleName.USER)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> userService.register(requestDto));

        //Then
        String expected = ROLE_NOT_FOUND_ERROR_MESSAGE + Role.RoleName.USER.name();
        String actual = exception.getMessage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userRepository).existsByEmail(requestDto.getEmail());
        verify(userMapper).toModel(requestDto);
        verify(roleRepository).findByName(Role.RoleName.USER);
        verifyNoMoreInteractions(userRepository, userMapper, roleRepository);
    }

    @Test
    @DisplayName("Finding a user by email should return UserDto when a valid email is provided")
    void findByEmail_ValidEmail_ShouldReturnUserDto() {
        //Given
        User user = initializeUser(createUserRegisterRequest(),
                createUserRole(Role.RoleName.USER, SAMPLE_TEST_ID_1), SAMPLE_TEST_ID_1);
        UserDto expected = mapToUserDto(user);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expected);

        //When
        UserDto actual = userService.findByEmail(user.getEmail());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userRepository).findByEmail(user.getEmail());
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Finding a user by email should trow an exception when an invalid email "
            + "is provided")
    void findByEmail_InvalidEmail_ShouldReturnException() {
        //Given
        when(userRepository.findByEmail(USER_EMAIL_EXAMPLE)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> userService.findByEmail(USER_EMAIL_EXAMPLE));

        //Then
        String expected = String
                .format(USER_NOT_FOUND_ERROR_MESSAGE, EMAIL_PREFIX, USER_EMAIL_EXAMPLE);
        String actual = exception.getMessage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userRepository).findByEmail(USER_EMAIL_EXAMPLE);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Finding a user by ID should return UserDto when a valid ID is provided")
    void findById_ValidId_ShouldReturnUserDto() {
        //Given
        User expected = initializeUser(
                createUserRegisterRequest(),
                createUserRole(Role.RoleName.USER, SAMPLE_TEST_ID_1),
                SAMPLE_TEST_ID_1
        );

        when(userRepository.findById(expected.getId())).thenReturn(Optional.of(expected));

        //When
        User actual = userService.findById(expected.getId());

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userRepository).findById(expected.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Finding a user by ID should trow an exception when an invalid ID is provided")
    void findById_InvalidId_ShouldReturnException() {
        //Given
        when(userRepository.findById(SAMPLE_TEST_ID_1)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> userService.findById(SAMPLE_TEST_ID_1));

        //Then
        String expected = String
                .format(USER_NOT_FOUND_ERROR_MESSAGE, USER_ID_PREFIX, SAMPLE_TEST_ID_1);
        String actual = exception.getMessage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userRepository).findById(SAMPLE_TEST_ID_1);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Updating a user by email successfully when valid data is provided")
    void updateByEmail_ValidEmail_ShouldReturnUserDto() {
        //Given
        User existUser = initializeUser(
                createUserRegisterRequest(),
                createUserRole(Role.RoleName.USER, SAMPLE_TEST_ID_1),
                SAMPLE_TEST_ID_1);
        UserUpdateRequestDto requestDto = createUserUpdateRequestDto(
                USERNAME_UPDATE_FIRST, USERNAME_UPDATE_LAST);
        UserDto expected = updateNamesUserDto(
                mapToUserDto(existUser), USERNAME_UPDATE_FIRST, USERNAME_UPDATE_LAST);

        when(userRepository.findByEmail(existUser.getEmail())).thenReturn(Optional.of(existUser));
        doAnswer(invocation -> {
            UserUpdateRequestDto userUpdateDto = invocation.getArgument(0);
            User user = invocation.getArgument(1);
            updateNamesUser(user, userUpdateDto.getFirstName(), userUpdateDto.getLastName());
            return null;
        }).when(userMapper).updateUserFromDto(requestDto, existUser);
        when(userRepository.save(existUser)).thenReturn(existUser);
        when(userMapper.toDto(existUser)).thenReturn(expected);

        //When
        UserDto actual = userService.updateByEmail(USER_EMAIL_EXAMPLE, requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(USERNAME_UPDATE_FIRST, actual.getFirstName(),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);
        assertEquals(USERNAME_UPDATE_LAST, actual.getLastName(),
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userRepository).findByEmail(existUser.getEmail());
        verify(userMapper).updateUserFromDto(requestDto, existUser);
        verify(userRepository).save(existUser);
        verify(userMapper).toDto(existUser);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Updating a user by email should trow an exception when an invalid email "
            + "is provided")
    void updateByEmail_InvalidEmail_ShouldReturnException() {
        //Given
        when(userRepository.findByEmail(USER_EMAIL_EXAMPLE)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> userService.findByEmail(USER_EMAIL_EXAMPLE));

        //Then
        String expected = String
                .format(USER_NOT_FOUND_ERROR_MESSAGE, EMAIL_PREFIX, USER_EMAIL_EXAMPLE);
        String actual = exception.getMessage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userRepository).findByEmail(USER_EMAIL_EXAMPLE);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Updating a user's role by user's ID successfully when valid data is provided")
    void updateRoleByUsersId_ValidData_ShouldUpdateRoleAndReturnUserWithRoleDto() {
        //Given
        Role adminRole = createUserRole(Role.RoleName.ADMIN, SAMPLE_TEST_ID_2);
        User existUser = initializeUser(
                createUserRegisterRequest(),
                createUserRole(Role.RoleName.USER, SAMPLE_TEST_ID_1),
                SAMPLE_TEST_ID_1);
        existUser.setRoles(new HashSet<>(Set.of(adminRole)));
        UserUpdateRoleRequestDto requestDto = createUserUpdateRoleRequestDto(adminRole.getName());
        UserWithRoleDto expected = mapToUserWithRoleDto(existUser);

        when(userRepository.findById(existUser.getId())).thenReturn(Optional.of(existUser));
        when(roleRepository.findByName(adminRole.getName())).thenReturn(Optional.of(adminRole));
        when(userRepository.save(existUser)).thenReturn(existUser);
        when(userMapper.toUserWithRoleDto(existUser)).thenReturn(expected);

        //When
        UserWithRoleDto actual = userService.updateRoleByUsersId(existUser.getId(), requestDto);

        //Then
        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual,
                ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userRepository).findById(existUser.getId());
        verify(roleRepository).findByName(adminRole.getName());
        verify(userRepository).save(existUser);
        verify(userMapper).toUserWithRoleDto(existUser);
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
    }

    @Test
    @DisplayName("Updating a user's role by user's ID should throw an exception when an invalid ID "
            + "is provided")
    void updateRoleByUsersId_InvalidId_ShouldReturnException() {
        //Given
        Role adminRole = createUserRole(Role.RoleName.ADMIN, SAMPLE_TEST_ID_2);
        UserUpdateRoleRequestDto requestDto = createUserUpdateRoleRequestDto(adminRole.getName());

        when(userRepository.findById(SAMPLE_TEST_ID_1)).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateRoleByUsersId(SAMPLE_TEST_ID_1, requestDto));

        //Then
        String expected = String
                .format(USER_NOT_FOUND_ERROR_MESSAGE, USER_ID_PREFIX, SAMPLE_TEST_ID_1);
        String actual = exception.getMessage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userRepository).findById(SAMPLE_TEST_ID_1);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Updating a user's role by user's ID should throw an exception when an invalid "
            + "Role is provided")
    void updateRoleByUsersId_InvalidRole_ShouldReturnException() {
        //Given
        User existUser = initializeUser(
                createUserRegisterRequest(),
                createUserRole(Role.RoleName.USER, SAMPLE_TEST_ID_1),
                SAMPLE_TEST_ID_1);
        Role adminRole = createUserRole(Role.RoleName.ADMIN, SAMPLE_TEST_ID_2);
        UserUpdateRoleRequestDto requestDto = createUserUpdateRoleRequestDto(adminRole.getName());

        when(userRepository.findById(existUser.getId())).thenReturn(Optional.of(existUser));
        when(roleRepository.findByName(adminRole.getName())).thenReturn(Optional.empty());

        //When
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateRoleByUsersId(SAMPLE_TEST_ID_1, requestDto));

        //Then
        String expected = ROLE_NOT_FOUND_ERROR_MESSAGE + adminRole.getAuthority();
        String actual = exception.getMessage();

        assertNotNull(actual, ACTUAL_RESULT_SHOULD_NOT_BE_NULL);
        assertEquals(expected, actual, ACTUAL_RESULT_SHOULD_BE_EQUAL_TO_THE_EXPECTED_ONE);

        verify(userRepository).findById(existUser.getId());
        verify(roleRepository).findByName(adminRole.getName());
        verifyNoMoreInteractions(userRepository, roleRepository);
    }
}
