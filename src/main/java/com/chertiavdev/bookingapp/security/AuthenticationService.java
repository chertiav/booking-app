package com.chertiavdev.bookingapp.security;

import com.chertiavdev.bookingapp.dto.user.UserLoginRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserLoginResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        log.info("Attempting authentication for user: {}", requestDto.getEmail());
        try {
            final var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword())
            );
            String token = jwtUtil.generateToken(authentication.getName());
            log.info("Authentication successful for user: {}", requestDto.getEmail());
            return new UserLoginResponseDto(token);
        } catch (AuthenticationException ex) {
            log.error("Authentication failed for user: {}. Reason: {}",
                    requestDto.getEmail(), ex.getMessage());
            throw ex;
        }
    }
}
