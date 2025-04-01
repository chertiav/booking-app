package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.ConflictDefaultApiResponses;
import com.chertiavdev.bookingapp.annotations.CreateDefaultApiResponses;
import com.chertiavdev.bookingapp.dto.booking.BookingDto;
import com.chertiavdev.bookingapp.dto.booking.CreateBookingRequestDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
                            description = "The booking was successfully created.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BookingDto.class)))
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
}
