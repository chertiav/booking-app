package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.service.AmenityCategoryService;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@Tag(name = "Amenity Category Management",
        description = "Endpoints for managing amenity categories")
@RestController
@RequiredArgsConstructor
@RequestMapping("/amenity-category")
public class AmenityCategoryController {
    private final AmenityCategoryService amenityCategoryService;

    @Operation(
            summary = "Create a new amenity category",
            description = "Allows administrators to create a new amenity category.",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED,
                            description = "The category was successfully created.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AmenityCategoryDto.class))),
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
    public AmenityCategoryDto create(
            @RequestBody @Valid CreateAmenityCategoryRequestDto requestDto) {
        return amenityCategoryService.save(requestDto);
    }

    @Operation(
            summary = "Get all amenity categories",
            description = "Retrieve all amenity categories.",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved all amenity categories.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array",
                                            implementation = AmenityCategoryDto.class))),
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
    public List<AmenityCategoryDto> getAll() {
        return amenityCategoryService.findAll();
    }

    @Operation(
            summary = "Get an amenity category by ID",
            description = "Retrieve an amenity category by ID",
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
                            description = "Successfully retrieved category information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AmenityCategoryDto.class))),
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
    public AmenityCategoryDto getById(@PathVariable Long id) {
        return amenityCategoryService.findById(id);
    }

    @Operation(
            summary = "Update an amenity category by ID",
            description = "Retrieve updated amenity category by ID",
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
                            description = "Successfully updated category information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AmenityCategoryDto.class))),
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
    public AmenityCategoryDto update(
            @PathVariable Long id,
            @RequestBody @Valid CreateAmenityCategoryRequestDto requestDto) {
        return amenityCategoryService.updateById(id, requestDto);
    }

    @Operation(
            summary = "Delete an amenity category by ID",
            description = "Removes an amenity category by ID. Available only to administrators",
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
                            description = "Successfully deleted category"),
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        amenityCategoryService.deleteById(id);
    }
}
