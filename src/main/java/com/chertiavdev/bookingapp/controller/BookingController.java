package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.operations.ApiOperationDetails;
import com.chertiavdev.bookingapp.annotations.parameters.DefaultIdParameter;
import com.chertiavdev.bookingapp.annotations.responses.BadRequestApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.ConflictApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.NotFoundApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.ServiceUnavailableApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.groups.BaseAuthApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.CreateApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.UpdateApiResponses;
import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.BookingSearchParameters;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.dto.page.PageResponse;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.service.BookingService;
import com.chertiavdev.bookingapp.util.ApiResponseConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking Management", description = "Endpoints for managing bookings.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @ApiOperationDetails(
            summary = "Create a new booking",
            description = "Allows users to create a new booking",
            responseDescription = "The booking was successfully created",
            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED
    )
    @CreateApiResponses
    @ConflictApiResponse
    @ServiceUnavailableApiResponse
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public BookingDto create(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CreateBookingRequestDto requestDto) {
        return bookingService.save(requestDto, user);
    }

    @ApiOperationDetails(
            summary = "Search bookings by parameters",
            description = "Retrieve a paginated list of all bookings books by parameters",
            responseDescription = "Successfully retrieved a paginated list of all bookings "
                    + "books by parameters"
    )
    @BaseAuthApiResponses
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public PageResponse<BookingDto> searchBookings(
            @ParameterObject BookingSearchParameters searchParameters,
            @ParameterObject Pageable pageable) {
        Page<BookingDto> page = bookingService.search(searchParameters, pageable);
        return PageResponse.of(page);
    }

    @ApiOperationDetails(
            summary = "Get all user's bookings",
            description = "Retrieve a paginated list of all user's bookings",
            responseDescription = "Successfully retrieved a paginated list of bookings"
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @BadRequestApiResponse
    @BaseAuthApiResponses
    @GetMapping("/my")
    public PageResponse<BookingDto> getBookingsByUser(
            @AuthenticationPrincipal User user,
            @ParameterObject Pageable pageable) {
        Page<BookingDto> page = bookingService.findByUserId(user.getId(), pageable);
        return PageResponse.of(page);
    }

    @ApiOperationDetails(
            summary = "Get a booking by ID",
            description = "Retrieve a booking by ID",
            responseDescription = "Successfully retrieved booking information"
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @BaseAuthApiResponses
    @DefaultIdParameter
    @GetMapping("/{id}")
    public BookingDto getBookingById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return bookingService.findByIdAndUserId(id, user.getId());
    }

    @ApiOperationDetails(
            summary = "Update a booking by ID",
            description = "Retrieve updated booking by ID",
            responseDescription = "Successfully updated booking information"
    )
    @UpdateApiResponses
    @DefaultIdParameter
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{id}")
    public BookingDto update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid CreateBookingRequestDto requestDto) {
        return bookingService.updatedByIdAndUserId(id, user.getId(), requestDto);
    }

    @ApiOperationDetails(
            summary = "Cancel a booking by ID",
            description = "Cancels a booking by ID. Available only to users",
            responseDescription = "Successfully canceled a booking",
            responseCode = ApiResponseConstants.RESPONSE_CODE_NO_CONTENT
    )
    @ConflictApiResponse
    @BaseAuthApiResponses
    @ServiceUnavailableApiResponse
    @DefaultIdParameter
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void cancelById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        bookingService.cancelById(id, user);
    }
}
