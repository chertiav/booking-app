package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.dto.page.PageResponse;
import com.chertiavdev.bookingapp.service.AccommodationService;
import com.chertiavdev.bookingapp.util.ApiResponseConstants;
import com.chertiavdev.bookingapp.util.ExampleValues;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    @PostMapping
    public AccommodationDto create(@RequestBody @Valid CreateAccommodationRequestDto requestDto) {
        return accommodationService.save(requestDto);
    }

    @Operation(
            summary = "Get all available accommodations",
            description = "Retrieve a paginated list of all available accommodations.",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved list of accommodations.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class))),
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_BAD_REQUEST,
                            description = ApiResponseConstants.INVALID_REQUEST_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .GENERAL_ERROR_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .GENERAL_ERROR_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues.COMMON_ERROR_EXAMPLE))),
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
    @GetMapping
    public PageResponse<AccommodationDto> getAllAvailable(@ParameterObject Pageable pageable) {
        return PageResponse.of(accommodationService.findAllAvailable(pageable));
    }

    @Operation(
            summary = "Get an available accommodation by ID",
            description = "Retrieve an available accommodation by ID",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Unique identifier of the accommodation.",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved accommodation information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AccommodationDto.class))),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_NOT_FOUND,
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
                            responseCode = ApiResponseConstants.RESPONSE_CODE_BAD_REQUEST,
                            description = ApiResponseConstants.INVALID_REQUEST_DESCRIPTION,
                            content = @Content(schema = @Schema(
                                    implementation = CommonApiErrorResponseDto.class),
                                    examples = @ExampleObject(
                                            name = ApiResponseConstants
                                                    .GENERAL_ERROR_EXAMPLE_MESSAGE,
                                            summary = ApiResponseConstants
                                                    .GENERAL_ERROR_EXAMPLE_DESCRIPTION,
                                            value = ExampleValues.COMMON_ERROR_EXAMPLE))),

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
    @GetMapping("/{id}")
    public AccommodationDto getAvailableById(@PathVariable Long id) {
        return accommodationService.findAvailableById(id);
    }

    @Operation(
            summary = "Update an accommodation by ID",
            description = "Retrieve updated accommodation by ID",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Unique identifier of the accommodation.",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully updated accommodation information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AccommodationDto.class))),
                    @ApiResponse(responseCode = ApiResponseConstants.RESPONSE_CODE_NOT_FOUND,
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public AccommodationDto update(
            @PathVariable Long id,
            @RequestBody @Valid CreateAccommodationRequestDto requestDto) {
        return accommodationService.updateById(id, requestDto);
    }

    @Operation(
            summary = "Delete an accommodation by ID",
            description = "Removes an accommodation by ID. Available only to administrators",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Unique identifier of the accommodation.",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_NO_CONTENT,
                            description = "Successfully deleted an accommodation"),
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }
}
