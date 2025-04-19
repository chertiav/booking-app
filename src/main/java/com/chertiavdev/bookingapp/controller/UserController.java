package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.operations.ApiOperationDetails;
import com.chertiavdev.bookingapp.annotations.parameters.DefaultIdParameter;
import com.chertiavdev.bookingapp.annotations.responses.NotFoundApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.UnauthorizedApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.groups.GetApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.UpdateApiResponses;
import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRoleRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserWithRoleDto;
import com.chertiavdev.bookingapp.service.UserService;
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

@Tag(name = "User Management", description = "Endpoints for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @ApiOperationDetails(
            summary = "Retrieve the current user's information",
            description = "Returns detailed information about the currently authenticated user, "
                    + "including their roles",
            responseDescription = "Successfully retrieved the user's information along with"
                    + " their roles"
    )
    @GetApiResponses
    @NotFoundApiResponse
    @UnauthorizedApiResponse
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/me")
    public UserDto findByEmail(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }

    @ApiOperationDetails(
            summary = "Update a user's information by Email",
            description = "Updates the details of a specific user based on the provided email "
                    + "address and request body",
            responseDescription = "Successfully updated the user's information"
    )
    @UpdateApiResponses
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/me")
    public UserDto updateByEmail(
            Authentication authentication,
            @RequestBody @Valid UserUpdateRequestDto requestDto) {
        return userService.updateByEmail(authentication.getName(), requestDto);
    }

    @ApiOperationDetails(
            summary = "Update a user's role by ID",
            description = "Updates the role of a user identified by their unique ID. This "
                    + "operation is restricted to administrators",
            responseDescription = "Successfully updated user's role"
    )
    @UpdateApiResponses
    @DefaultIdParameter
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/role")
    public UserWithRoleDto updateRole(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRoleRequestDto requestDto) {
        return userService.updateRoleByUsersId(id, requestDto);
    }
}
