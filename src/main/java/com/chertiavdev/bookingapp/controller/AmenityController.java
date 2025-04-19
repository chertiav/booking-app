package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.operations.ApiOperationDetails;
import com.chertiavdev.bookingapp.annotations.parameters.DefaultIdParameter;
import com.chertiavdev.bookingapp.annotations.responses.NotFoundApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.groups.BaseAuthApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.CreateApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.GetApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.UpdateApiResponses;
import com.chertiavdev.bookingapp.dto.amenity.AmenityDto;
import com.chertiavdev.bookingapp.dto.amenity.CreateAmenityRequestDto;
import com.chertiavdev.bookingapp.service.AmenityService;
import com.chertiavdev.bookingapp.util.ApiResponseConstants;
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

    @ApiOperationDetails(
            summary = "Create a new amenity",
            description = "Allows administrators to create a new amenity",
            responseDescription = "The amenity was successfully created",
            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED
    )
    @CreateApiResponses
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public AmenityDto create(@RequestBody @Valid CreateAmenityRequestDto requestDto) {
        return amenityService.save(requestDto);
    }

    @ApiOperationDetails(
            summary = "Get all amenities",
            description = "Retrieve all amenity amenities",
            responseDescription = "Successfully retrieved all amenities"
    )
    @GetApiResponses
    @GetMapping
    public List<AmenityDto> getAll() {
        return amenityService.findAll();
    }

    @ApiOperationDetails(
            summary = "Get an amenity by ID",
            description = "Retrieve an amenity by ID",
            responseDescription = "Successfully retrieved amenity information"
    )
    @GetApiResponses
    @NotFoundApiResponse
    @DefaultIdParameter
    @GetMapping("/{id}")
    public AmenityDto getById(@PathVariable Long id) {
        return amenityService.findById(id);
    }

    @ApiOperationDetails(
            summary = "Update an amenity by ID",
            description = "Retrieve updated amenity by ID",
            responseDescription = "Successfully updated amenity information"
    )
    @UpdateApiResponses
    @DefaultIdParameter
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public AmenityDto update(
            @PathVariable Long id,
            @RequestBody @Valid CreateAmenityRequestDto requestDto) {
        return amenityService.updateById(id, requestDto);
    }

    @ApiOperationDetails(
            summary = "Delete an amenity by ID",
            description = "Removes an amenity by ID. Available only to administrators",
            responseDescription = "Successfully deleted amenity",
            responseCode = ApiResponseConstants.RESPONSE_CODE_NO_CONTENT
    )
    @BaseAuthApiResponses
    @DefaultIdParameter
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        amenityService.deleteById(id);
    }
}
