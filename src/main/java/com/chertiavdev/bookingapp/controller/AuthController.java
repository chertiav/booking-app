package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.RegisterDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.UpdateDefaultApiResponses;
import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserLoginRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserLoginResponseDto;
import com.chertiavdev.bookingapp.dto.user.UserRegisterRequestDto;
import com.chertiavdev.bookingapp.exception.RegistrationException;
import com.chertiavdev.bookingapp.security.AuthenticationService;
import com.chertiavdev.bookingapp.service.UserService;
import com.chertiavdev.bookingapp.util.ApiResponseConstants;
import com.chertiavdev.bookingapp.util.ExampleValues;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth Management", description = "Endpoints responsible for managing authentication and"
        + " retrieving user information.")
@RequiredArgsConstructor
@RestController
@RequestMapping
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(
            summary = ApiResponseConstants.USER_REGISTRATION_DESCRIPTION,
            description = ApiResponseConstants.USER_REGISTRATION_DESCRIPTION,
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED,
                            description = "Successfully registered a new user",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data required to register a new user",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRegisterRequestDto.class)
                    )
            )
    )
    @RegisterDefaultApiResponses
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody UserRegisterRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @Operation(
            summary = ApiResponseConstants.USER_LOGIN_DESCRIPTION,
            description = ApiResponseConstants.USER_LOGIN_DESCRIPTION,
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successful user login",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserLoginResponseDto.class)))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserLoginRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Example User logging",
                                            summary = "Example of a valid User logging request",
                                            value = ExampleValues.EXAMPLE_USER_LOGGING),
                                    @ExampleObject(
                                            name = "Example Admin logging",
                                            summary = "Example of a valid Admin logging request",
                                            value = ExampleValues.EXAMPLE_ADMIN_LOGGING)
                            }
                    )
            )
    )
    @UpdateDefaultApiResponses
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
