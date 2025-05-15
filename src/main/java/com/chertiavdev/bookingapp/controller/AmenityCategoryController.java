package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.operations.ApiOperationDetails;
import com.chertiavdev.bookingapp.annotations.parameters.DefaultIdParameter;
import com.chertiavdev.bookingapp.annotations.responses.NotFoundApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.groups.BaseAuthApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.CreateApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.GetApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.UpdateApiResponses;
import com.chertiavdev.bookingapp.dto.amenity.category.AmenityCategoryDto;
import com.chertiavdev.bookingapp.dto.amenity.category.CreateAmenityCategoryRequestDto;
import com.chertiavdev.bookingapp.service.AmenityCategoryService;
import com.chertiavdev.bookingapp.util.constants.ApiResponseConstants;
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

    @ApiOperationDetails(
            summary = "Create a new amenity category",
            description = "Allows administrators to create a new amenity category",
            responseDescription = "The category was successfully created",
            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED
    )
    @CreateApiResponses
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public AmenityCategoryDto create(
            @RequestBody @Valid CreateAmenityCategoryRequestDto requestDto) {
        return amenityCategoryService.save(requestDto);
    }

    @ApiOperationDetails(
            summary = "Get all amenity categories",
            description = "Retrieve all amenity categories",
            responseDescription = "Successfully retrieved all amenity categories"
    )
    @GetApiResponses
    @GetMapping
    public List<AmenityCategoryDto> getAll() {
        return amenityCategoryService.findAll();
    }

    @ApiOperationDetails(
            summary = "Get an amenity category by ID",
            description = "Retrieve an amenity category by ID",
            responseDescription = "Successfully retrieved category information"
    )
    @GetApiResponses
    @NotFoundApiResponse
    @DefaultIdParameter
    @GetMapping("/{id}")
    public AmenityCategoryDto getById(@PathVariable Long id) {
        return amenityCategoryService.findById(id);
    }

    @ApiOperationDetails(
            summary = "Update an amenity category by ID",
            description = "Retrieve updated amenity category by ID",
            responseDescription = "Successfully updated category information"
    )
    @UpdateApiResponses
    @DefaultIdParameter
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public AmenityCategoryDto update(
            @PathVariable Long id,
            @RequestBody @Valid CreateAmenityCategoryRequestDto requestDto) {
        return amenityCategoryService.updateById(id, requestDto);
    }

    @ApiOperationDetails(
            summary = "Delete an amenity category by ID",
            description = "Removes an amenity category by ID. Available only to administrators",
            responseDescription = "Successfully deleted category",
            responseCode = ApiResponseConstants.RESPONSE_CODE_NO_CONTENT
    )
    @BaseAuthApiResponses
    @DefaultIdParameter
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        amenityCategoryService.deleteById(id);
    }
}
