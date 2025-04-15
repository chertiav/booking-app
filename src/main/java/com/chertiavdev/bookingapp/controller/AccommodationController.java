package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.ConflictDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.CreateDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.DefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.DefaultIdParameter;
import com.chertiavdev.bookingapp.annotations.GetAllPublicDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.GetByIdPublicDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.UpdateDefaultApiResponses;
import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.chertiavdev.bookingapp.dto.page.PageResponse;
import com.chertiavdev.bookingapp.service.AccommodationService;
import com.chertiavdev.bookingapp.util.ApiResponseConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

@Tag(name = "Accommodation Management", description = "Endpoints for managing accommodations")
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
            }
    )
    @CreateDefaultApiResponses
    @ConflictDefaultApiResponses
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
                                    schema = @Schema(implementation = PageResponse.class)))
            }
    )
    @GetAllPublicDefaultApiResponses
    @GetMapping
    public PageResponse<AccommodationDto> getAllAvailable(@ParameterObject Pageable pageable) {
        return PageResponse.of(accommodationService.findAllAvailable(pageable));
    }

    @Operation(
            summary = "Get an available accommodation by ID",
            description = "Retrieve an available accommodation by ID",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved accommodation information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AccommodationDto.class)))
            }
    )
    @GetByIdPublicDefaultApiResponses
    @DefaultIdParameter
    @GetMapping("/{id}")
    public AccommodationDto getAvailableById(@PathVariable Long id) {
        return accommodationService.findAvailableById(id);
    }

    @Operation(
            summary = "Update an accommodation by ID",
            description = "Retrieve updated accommodation by ID",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully updated accommodation information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AccommodationDto.class)))
            }
    )
    @UpdateDefaultApiResponses
    @DefaultIdParameter
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
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_NO_CONTENT,
                            description = "Successfully deleted an accommodation")
            }
    )
    @DefaultApiResponses
    @DefaultIdParameter
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }
}
