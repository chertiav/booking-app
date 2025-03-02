package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRoleRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserWithRoleDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Retrieve the current user's information",
            description = "Returns detailed information about the currently authenticated user, "
                    + "including their roles.",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved the user's information along with"
                                    + " their roles.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_NOT_FOUND,
                            description = ApiResponseConstants.NOT_FOUND_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .RESOURCE_NOT_FOUND_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .NOT_FOUND_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues.NOT_FOUND_ERROR_EXAMPLE))),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_UNAUTHORIZED,
                            description = ApiResponseConstants.UNAUTHORIZED_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = ApiResponseConstants.UNAUTHORIZED_ERROR_EXAMPLE_MESSAGE,
                                    summary = ApiResponseConstants
                                            .USER_UNAUTHORIZED_ERROR_EXAMPLE_DESCRIPTION,
                                    value = ExampleValues.UNAUTHORIZED_ERROR_EXAMPLE))),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_INTERNAL_SERVER_ERROR,
                            description = ApiResponseConstants.INTERNAL_SERVER_ERROR_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .INTERNAL_SERVER_ERROR_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .INTERNAL_SERVER_ERROR_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues
                                                    .INTERNAL_SERVER_ERROR_ERROR_EXAMPLE))),
            }
    )
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/me")
    public UserDto findByEmail(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }

    @Operation(
            summary = "Update a user's information by Email",
            description = "Updates the details of a specific user based on the provided email "
                    + "address and request body",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully updated the user's information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_BAD_REQUEST,
                            description = ApiResponseConstants.INVALID_REQUEST_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = ApiResponseConstants
                                                            .VALIDATION_ERROR_EXAMPLE_MESSAGE,
                                                    summary = ApiResponseConstants
                                                            .VALIDATION_ERROR_EXAMPLE_DESCRIPTION,
                                                    value = ExampleValues.VALIDATION_ERROR_EXAMPLE
                                            ),
                                            @ExampleObject(
                                                    name = ApiResponseConstants
                                                            .GENERAL_ERROR_EXAMPLE_MESSAGE,
                                                    summary = ApiResponseConstants
                                                            .GENERAL_ERROR_EXAMPLE_DESCRIPTION,
                                                    value = ExampleValues.COMMON_ERROR_EXAMPLE
                                            )
                                    })),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_NOT_FOUND,
                            description = ApiResponseConstants.NOT_FOUND_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .RESOURCE_NOT_FOUND_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .NOT_FOUND_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues.NOT_FOUND_ERROR_EXAMPLE))),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_UNAUTHORIZED,
                            description = ApiResponseConstants.UNAUTHORIZED_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .UNAUTHORIZED_ERROR_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .USER_UNAUTHORIZED_ERROR_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues.UNAUTHORIZED_ERROR_EXAMPLE))),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_INTERNAL_SERVER_ERROR,
                            description = ApiResponseConstants.INTERNAL_SERVER_ERROR_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .INTERNAL_SERVER_ERROR_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .INTERNAL_SERVER_ERROR_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues
                                                    .INTERNAL_SERVER_ERROR_ERROR_EXAMPLE))),
            }
    )
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/me")
    public UserDto updateByEmail(
            Authentication authentication,
            @RequestBody @Valid UserUpdateRequestDto requestDto) {
        return userService.updateByEmail(authentication.getName(), requestDto);
    }

    @Operation(
            summary = "Update a user's role by ID",
            description = "Updates the role of a user identified by their unique ID. This "
                    + "operation is restricted to administrators.",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "The user's role was successfully updated.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserWithRoleDto.class))),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_BAD_REQUEST,
                            description = ApiResponseConstants.INVALID_REQUEST_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = ApiResponseConstants
                                                            .VALIDATION_ERROR_EXAMPLE_MESSAGE,
                                                    summary = ApiResponseConstants
                                                            .VALIDATION_ERROR_EXAMPLE_DESCRIPTION,
                                                    value = ExampleValues.VALIDATION_ERROR_EXAMPLE
                                            ),
                                            @ExampleObject(
                                                    name = ApiResponseConstants
                                                            .GENERAL_ERROR_EXAMPLE_MESSAGE,
                                                    summary = ApiResponseConstants
                                                            .GENERAL_ERROR_EXAMPLE_DESCRIPTION,
                                                    value = ExampleValues.COMMON_ERROR_EXAMPLE
                                            )
                                    })),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_FORBIDDEN,
                            description = ApiResponseConstants.FORBIDDEN_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .FORBIDDEN_ERROR_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .FORBIDDEN_ERROR_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues.FORBIDDEN_ERROR_ERROR_EXAMPLE))),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_NOT_FOUND,
                            description = ApiResponseConstants.NOT_FOUND_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .RESOURCE_NOT_FOUND_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .NOT_FOUND_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues.NOT_FOUND_ERROR_EXAMPLE))),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_UNAUTHORIZED,
                            description = ApiResponseConstants.UNAUTHORIZED_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .UNAUTHORIZED_ERROR_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .USER_UNAUTHORIZED_ERROR_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues.UNAUTHORIZED_ERROR_EXAMPLE))),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_INTERNAL_SERVER_ERROR,
                            description = ApiResponseConstants.INTERNAL_SERVER_ERROR_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .INTERNAL_SERVER_ERROR_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .INTERNAL_SERVER_ERROR_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues
                                                    .INTERNAL_SERVER_ERROR_ERROR_EXAMPLE))),
            }
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/role")
    public UserWithRoleDto updateRole(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRoleRequestDto requestDto) {
        return userService.updateRoleByUsersId(id, requestDto);
    }
}
