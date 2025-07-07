package com.chertiavdev.bookingapp.data.builders;

import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_1;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_2;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.SAMPLE_TEST_ID_3;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USERNAME_FIRST_JOHN;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USERNAME_FIRST_SANSA;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USERNAME_LAST_DOE;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USERNAME_LAST_STARK;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_EMAIL_JOHN;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_EMAIL_SANSA;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_FIRST_USERNAME_UPDATED;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.USER_LAST_USERNAME_UPDATED;
import static com.chertiavdev.bookingapp.utils.constants.ServiceTestConstants.VALID_USER_PASSWORD;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestUser;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestUserRegisterRequest;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestUserRole;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestUserUpdateRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.createTestUserUpdateRoleRequestDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.mapToUserDto;
import static com.chertiavdev.bookingapp.utils.helpers.ServiceTestUtils.mapToUserWithRoleDto;

import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserRegisterRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRoleRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserWithRoleDto;
import com.chertiavdev.bookingapp.model.Role;
import com.chertiavdev.bookingapp.model.User;
import lombok.Getter;

@Getter
public class UserTestDataBuilder {
    private final Role userRole;
    private final Role adminRole;

    private final User userJohn;
    private final User userSansa;
    private final User updatedUserJohn;
    private final User updatedRoleUserJohn;

    private final UserRegisterRequestDto userJohnRegisterRequestDto;
    private final UserUpdateRequestDto updatedUserJohnRequestDto;
    private final UserUpdateRoleRequestDto userJohnUpdateRoleRequestDto;

    private final User userJohnToModel;

    private final UserDto userJohnDto;
    private final UserDto updatedUserJohnDto;
    private final UserWithRoleDto updatedRoleUserJohnDto;

    public UserTestDataBuilder() {
        userRole = createUserRole();
        adminRole = createAdminRole();

        userJohn = createUserJohn();
        userSansa = createUserSansa();
        updatedUserJohn = createUpdatedUserJohn();
        updatedRoleUserJohn = createUpdatedRoleUserJohn();

        userJohnRegisterRequestDto = createUserJohnRegisterRequestDto();
        updatedUserJohnRequestDto = createUpdatedUserJohnRequestDto();
        userJohnUpdateRoleRequestDto = createUserJohnUpdateRoleRequestDto();

        userJohnToModel = createUserJohnToModel();

        userJohnDto = createUserJohnDto();
        updatedUserJohnDto = createUpdatedUserJohnDto();
        updatedRoleUserJohnDto = createUpdatedRoleUserJohnDto();
    }

    private User createUserJohn() {
        return createTestUser(
                SAMPLE_TEST_ID_2,
                USERNAME_FIRST_JOHN,
                USERNAME_LAST_DOE,
                VALID_USER_PASSWORD,
                USER_EMAIL_JOHN,
                userRole
        );
    }

    private User createUserSansa() {
        return createTestUser(
                SAMPLE_TEST_ID_3,
                USERNAME_FIRST_SANSA,
                USERNAME_LAST_STARK,
                VALID_USER_PASSWORD,
                USER_EMAIL_SANSA,
                userRole
        );
    }

    private User createUpdatedUserJohn() {
        return createTestUser(
                SAMPLE_TEST_ID_2,
                USER_FIRST_USERNAME_UPDATED,
                USER_LAST_USERNAME_UPDATED,
                VALID_USER_PASSWORD,
                USER_EMAIL_JOHN,
                userRole
        );
    }

    private User createUpdatedRoleUserJohn() {
        return createTestUser(
                SAMPLE_TEST_ID_2,
                USERNAME_FIRST_JOHN,
                USERNAME_LAST_DOE,
                VALID_USER_PASSWORD,
                USER_EMAIL_JOHN,
                adminRole
        );
    }

    private Role createUserRole() {
        return createTestUserRole(Role.RoleName.USER, SAMPLE_TEST_ID_2);
    }

    private Role createAdminRole() {
        return createTestUserRole(Role.RoleName.ADMIN, SAMPLE_TEST_ID_1);
    }

    private UserRegisterRequestDto createUserJohnRegisterRequestDto() {
        return createTestUserRegisterRequest(
                USER_EMAIL_JOHN,
                VALID_USER_PASSWORD,
                VALID_USER_PASSWORD,
                USERNAME_FIRST_JOHN,
                USERNAME_LAST_DOE
        );
    }

    private UserUpdateRequestDto createUpdatedUserJohnRequestDto() {
        return createTestUserUpdateRequestDto(
                USER_FIRST_USERNAME_UPDATED,
                USER_LAST_USERNAME_UPDATED
        );
    }

    private UserUpdateRoleRequestDto createUserJohnUpdateRoleRequestDto() {
        return createTestUserUpdateRoleRequestDto(Role.RoleName.ADMIN);
    }

    private User createUserJohnToModel() {
        return createTestUser(
                null,
                USERNAME_FIRST_JOHN,
                USERNAME_LAST_DOE,
                VALID_USER_PASSWORD,
                USER_EMAIL_JOHN,
                userRole
        );
    }

    private UserDto createUserJohnDto() {
        return mapToUserDto(userJohn);
    }

    private UserDto createUpdatedUserJohnDto() {
        return mapToUserDto(updatedUserJohn);
    }

    private UserWithRoleDto createUpdatedRoleUserJohnDto() {
        return mapToUserWithRoleDto(updatedRoleUserJohn);
    }
}
