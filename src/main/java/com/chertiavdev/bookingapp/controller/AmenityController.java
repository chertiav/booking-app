package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.CreateDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.DefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.DefaultIdParameter;
import com.chertiavdev.bookingapp.annotations.GetAllPublicDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.GetByIdPublicDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.UpdateDefaultApiResponses;
import com.chertiavdev.bookingapp.dto.amenity.AmenityDto;
import com.chertiavdev.bookingapp.dto.amenity.CreateAmenityRequestDto;
import com.chertiavdev.bookingapp.service.AmenityService;
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

@Tag(name = "Amenity Management", description = "Endpoints for managing amenities")
@RestController
@RequestMapping("/amenities")
@RequiredArgsConstructor
public class AmenityController {
    private final AmenityService amenityService;

    @Operation(
            summary = "Create a new amenity",
            description = "Allows administrators to create a new amenity.",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED,
                            description = "The amenity was successfully created.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AmenityDto.class)
                            )
                    )
            }
    )
    @CreateDefaultApiResponses
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public AmenityDto create(@RequestBody @Valid CreateAmenityRequestDto requestDto) {
        return amenityService.save(requestDto);
    }

    @Operation(
            summary = "Get all amenities",
            description = "Retrieve all amenity amenities.",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved all amenities.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            type = "array",
                                            implementation = AmenityDto.class
                                    )
                            )
                    )
            }
    )
    @GetAllPublicDefaultApiResponses
    @GetMapping
    public List<AmenityDto> getAll() {
        return amenityService.findAll();
    }

    @Operation(
            summary = "Get an amenity by ID",
            description = "Retrieve an amenity by ID",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved amenity information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AmenityDto.class))),
            }
    )
    @DefaultIdParameter
    @GetByIdPublicDefaultApiResponses
    @GetMapping("/{id}")
    public AmenityDto getById(@PathVariable Long id) {
        return amenityService.findById(id);
    }

    @Operation(
            summary = "Update an amenity by ID",
            description = "Retrieve updated amenity by ID",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully updated amenity information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AmenityDto.class))),

            }
    )
    @DefaultIdParameter
    @UpdateDefaultApiResponses
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public AmenityDto update(
            @PathVariable Long id,
            @RequestBody @Valid CreateAmenityRequestDto requestDto) {
        return amenityService.updateById(id, requestDto);
    }

    @Operation(
            summary = "Delete an amenity by ID",
            description = "Removes an amenity by ID. Available only to administrators",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_NO_CONTENT,
                            description = "Successfully deleted amenity"),

            }
    )
    @DefaultIdParameter
    @DefaultApiResponses
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        amenityService.deleteById(id);
    }
}
