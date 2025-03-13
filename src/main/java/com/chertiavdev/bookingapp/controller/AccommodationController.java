package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.service.AccommodationService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodation management", description = "Endpoints for managing accommodations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;

    @Operation(
            summary = "Create a new accommodation",
            description = "Allows administrators to create a new accommodation.",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED,
                            description = "The accommodation was successfully created.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AccommodationDto.class))),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_CONFLICT,
                            description = ApiResponseConstants.CONFLICT_ERROR_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .CONFLICT_ERROR_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .CONFLICT_ERROR_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues.CONFLICT_ERROR_ERROR_EXAMPLE))),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_FORBIDDEN,
                            description = ApiResponseConstants.FORBIDDEN_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .FORBIDDEN_ERROR_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .FORBIDDEN_ERROR_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues.FORBIDDEN_ERROR_EXAMPLE))),
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
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @PostMapping
    public AccommodationDto create(@RequestBody @Valid CreateAccommodationRequestDto requestDto) {
        return accommodationService.save(requestDto);
    }
}
