package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.examples.ExampleValues;
import com.chertiavdev.bookingapp.annotations.operations.ApiOperationDetails;
import com.chertiavdev.bookingapp.annotations.responses.UnauthorizedApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.groups.GetApiResponses;
import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserLoginRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserLoginResponseDto;
import com.chertiavdev.bookingapp.dto.user.UserRegisterRequestDto;
import com.chertiavdev.bookingapp.exception.RegistrationException;
import com.chertiavdev.bookingapp.security.AuthenticationService;
import com.chertiavdev.bookingapp.service.UserService;
import com.chertiavdev.bookingapp.util.constants.ApiResponseConstants;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

    @ApiOperationDetails(
            summary = "Registration a new user",
            description = "Registers a new user with the provided details",
            responseDescription = "Successfully registered a new user",
            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED
    )
    @GetApiResponses
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public UserDto register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data required to register a new user"
            )
            @RequestBody
            @Valid
            UserRegisterRequestDto requestDto
    ) throws RegistrationException {
        return userService.register(requestDto);
    }

    @ApiOperationDetails(
            summary = "User login",
            description = "User login",
            responseDescription = "Successful user login"
    )
    @GetApiResponses
    @UnauthorizedApiResponse
    @PostMapping("/login")
    public UserLoginResponseDto login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login data",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "Example User login",
                                            summary = "Example of a valid User login request",
                                            value = ExampleValues.EXAMPLE_USER_LOGGING
                                    ),
                                    @ExampleObject(
                                            name = "Example Admin login",
                                            summary = "Example of a valid Admin login request",
                                            value = ExampleValues.EXAMPLE_ADMIN_LOGGING
                                    )
                            }
                    )
            )
            @RequestBody
            @Valid
            UserLoginRequestDto requestDto
    ) {
        return authenticationService.authenticate(requestDto);
    }
}
