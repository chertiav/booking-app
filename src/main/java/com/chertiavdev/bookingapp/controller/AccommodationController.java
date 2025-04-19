package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.operations.ApiOperationDetails;
import com.chertiavdev.bookingapp.annotations.parameters.DefaultIdParameter;
import com.chertiavdev.bookingapp.annotations.responses.ConflictApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.NotFoundApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.groups.BaseAuthApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.CreateApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.GetApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.UpdateApiResponses;
import com.chertiavdev.bookingapp.dto.accommodation.AccommodationDto;
import com.chertiavdev.bookingapp.dto.accommodation.CreateAccommodationRequestDto;
import com.chertiavdev.bookingapp.dto.page.PageResponse;
import com.chertiavdev.bookingapp.service.AccommodationService;
import com.chertiavdev.bookingapp.util.ApiResponseConstants;
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

    @ApiOperationDetails(
            summary = "Create a new accommodation",
            description = "Allows administrators to create a new accommodation",
            responseDescription = "The accommodation was successfully created",
            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED
    )
    @CreateApiResponses
    @ConflictApiResponse
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public AccommodationDto create(@RequestBody @Valid CreateAccommodationRequestDto requestDto) {
        return accommodationService.save(requestDto);
    }

    @ApiOperationDetails(
            summary = "Get all available accommodations",
            description = "Retrieve a paginated list of all available accommodations",
            responseDescription = "Successfully retrieved list of accommodations"
    )
    @GetApiResponses
    @GetMapping
    public PageResponse<AccommodationDto> getAllAvailable(@ParameterObject Pageable pageable) {
        return PageResponse.of(accommodationService.findAllAvailable(pageable));
    }

    @ApiOperationDetails(
            summary = "Get an available accommodation by ID",
            description = "Retrieve an available accommodation by ID",
            responseDescription = "Successfully retrieved accommodation information"
    )
    @GetApiResponses
    @NotFoundApiResponse
    @DefaultIdParameter
    @GetMapping("/{id}")
    public AccommodationDto getAvailableById(@PathVariable Long id) {
        return accommodationService.findAvailableById(id);
    }

    @ApiOperationDetails(
            summary = "Update an accommodation by ID",
            description = "Retrieve updated accommodation by ID",
            responseDescription = "Successfully updated accommodation information"
    )
    @UpdateApiResponses
    @DefaultIdParameter
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public AccommodationDto update(
            @PathVariable Long id,
            @RequestBody @Valid CreateAccommodationRequestDto requestDto) {
        return accommodationService.updateById(id, requestDto);
    }

    @ApiOperationDetails(
            summary = "Delete an accommodation by ID",
            description = "Removes an accommodation by ID. Available only to administrators",
            responseDescription = "Successfully deleted an accommodation",
            responseCode = ApiResponseConstants.RESPONSE_CODE_NO_CONTENT
    )
    @BaseAuthApiResponses
    @DefaultIdParameter
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }
}
