package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.CreateDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.DefaultIdParameter;
import com.chertiavdev.bookingapp.annotations.DeleteDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.GetAllPublicDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.GetByIdPublicDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.UpdateDefaultApiResponses;
import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import com.chertiavdev.bookingapp.service.AmenityCategoryService;
import com.chertiavdev.bookingapp.util.ApiResponseConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
                                    schema = @Schema(implementation = AmenityCategoryDto.class)
                            )
                    )
            }
    )
    @CreateDefaultApiResponses
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
                                    schema = @Schema(
                                            type = "array",
                                            implementation = AmenityCategoryDto.class
                                    )
                            )
                    )
            }
    )
    @GetAllPublicDefaultApiResponses
    @GetMapping
    public List<AmenityCategoryDto> getAll() {
        return amenityCategoryService.findAll();
    }

    @Operation(
            summary = "Get an amenity category by ID",
            description = "Retrieve an amenity category by ID",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved category information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AmenityCategoryDto.class))),
            }
    )
    @DefaultIdParameter
    @GetByIdPublicDefaultApiResponses
    @GetMapping("/{id}")
    public AmenityCategoryDto getById(@PathVariable Long id) {
        return amenityCategoryService.findById(id);
    }

    @Operation(
            summary = "Update an amenity category by ID",
            description = "Retrieve updated amenity category by ID",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully updated category information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AmenityCategoryDto.class))),

            }
    )
    @DefaultIdParameter
    @UpdateDefaultApiResponses
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
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_NO_CONTENT,
                            description = "Successfully deleted category"),

            }
    )
    @DefaultIdParameter
    @DeleteDefaultApiResponses
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        amenityCategoryService.deleteById(id);
    }
}
