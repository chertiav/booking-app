package com.chertiavdev.bookingapp.service;

import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserRegisterRequestDto;
import com.chertiavdev.bookingapp.exception.RegistrationException;

public interface UserService {
    UserDto register(UserRegisterRequestDto requestDto) throws RegistrationException;

    UserDto findByEmail(String email);
}
