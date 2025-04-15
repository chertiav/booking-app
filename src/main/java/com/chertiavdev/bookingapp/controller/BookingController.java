package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.ConflictDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.CreateDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.DefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.DefaultIdParameter;
import com.chertiavdev.bookingapp.annotations.GetAllPublicDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.GetByIdPublicDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.UpdateDefaultApiResponses;
import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.BookingSearchParameters;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
import com.chertiavdev.bookingapp.dto.page.PageResponse;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.service.BookingService;
import com.chertiavdev.bookingapp.util.ApiResponseConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Create a new booking",
            description = "Allows users to create a new booking.",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED,
                            description = "The booking was successfully created.")
            }
    )
    @CreateDefaultApiResponses
    @ConflictDefaultApiResponses
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public BookingDto create(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CreateBookingRequestDto requestDto) {
        return bookingService.save(requestDto, user);
    }

    @Operation(
            summary = "Search bookings by parameters",
            description = "Retrieve a paginated list of all bookings books by parameters.",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved a paginated list of bookings.")
            }
    )
    @DefaultApiResponses
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public PageResponse<BookingDto> searchBookings(
            @ParameterObject BookingSearchParameters searchParameters,
            @ParameterObject Pageable pageable) {
        Page<BookingDto> page = bookingService.search(searchParameters, pageable);
        return PageResponse.of(page);
    }

    @Operation(
            summary = "Get all user's bookings",
            description = "Retrieve a paginated list of all user's bookings.",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved a paginated list of bookings.")
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetAllPublicDefaultApiResponses
    @DefaultApiResponses
    @GetMapping("/my")
    public PageResponse<BookingDto> getBookingsByUser(
            @AuthenticationPrincipal User user,
            @ParameterObject Pageable pageable) {
        Page<BookingDto> page = bookingService.findByUserId(user.getId(), pageable);
        return PageResponse.of(page);
    }

    @Operation(
            summary = "Get a booking by ID",
            description = "Retrieve a booking by ID",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully retrieved booking information",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookingDto.class))
                    )
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetByIdPublicDefaultApiResponses
    @DefaultApiResponses
    @DefaultIdParameter
    @GetMapping("/{id}")
    public BookingDto getBookingById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return bookingService.findByIdAndUserId(id, user.getId());
    }

    @Operation(
            summary = "Update a booking by ID",
            description = "Retrieve updated booking by ID",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_OK,
                            description = "Successfully updated booking information")
            }
    )
    @UpdateDefaultApiResponses
    @DefaultIdParameter
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{id}")
    public BookingDto update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody @Valid CreateBookingRequestDto requestDto) {
        return bookingService.updatedByIdAndUserId(id, user.getId(), requestDto);
    }

    @Operation(
            summary = "Cancel a booking by ID",
            description = "Cancels a booking by ID. Available only to users",
            responses = {
                    @ApiResponse(
                            responseCode = ApiResponseConstants.RESPONSE_CODE_NO_CONTENT,
                            description = "Successfully canceled a booking")
            }
    )
    @ConflictDefaultApiResponses
    @DefaultApiResponses
    @DefaultIdParameter
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void cancelById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        bookingService.cancelById(id, user.getId());
    }
}
