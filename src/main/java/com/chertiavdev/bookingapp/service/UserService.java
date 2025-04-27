package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserRegisterRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRoleRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserWithRoleDto;
import com.chertiavdev.bookingapp.exception.RegistrationException;
import com.chertiavdev.bookingapp.model.User;

public interface UserService {
    UserDto register(UserRegisterRequestDto requestDto) throws RegistrationException;

    UserDto findByEmail(String email);

    User findById(Long id);

    UserDto updateByEmail(String email, UserUpdateRequestDto requestDto);

    UserWithRoleDto updateRoleByUsersId(Long id, UserUpdateRoleRequestDto requestDto);
}
