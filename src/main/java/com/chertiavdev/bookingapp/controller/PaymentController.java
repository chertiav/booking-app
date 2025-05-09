package com.chertiavdev.bookingapp.controller;

import com.chertiavdev.bookingapp.annotations.operations.ApiOperationDetails;
import com.chertiavdev.bookingapp.annotations.responses.BadRequestApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.groups.BaseAuthApiResponses;
import com.chertiavdev.bookingapp.annotations.responses.groups.CreateApiResponses;
import com.chertiavdev.bookingapp.dto.page.PageResponse;
import com.chertiavdev.bookingapp.dto.payment.CreatePaymentRequestDto;
import com.chertiavdev.bookingapp.dto.payment.PaymentDto;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.service.PaymentService;
import com.chertiavdev.bookingapp.util.constants.ApiResponseConstants;
import com.chertiavdev.bookingapp.util.helpers.RoleUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment Management", description = "Endpoints for managing payments")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @ApiOperationDetails(
            summary = "Retrieve payments based on user ID",
            description = "Returns a paginated list of payments associated with the specified user."
                    + " Admins can fetch payments for any user, while regular users can only "
                    + "access their own payments.",
            responseDescription = "Paginated list of payment details."
    )
    @GetMapping
    @BadRequestApiResponse
    @BaseAuthApiResponses
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public PageResponse<PaymentDto> getPayments(
            @RequestParam(name = "user_id", required = false)
            @Positive(message = "user_id must be positive")
            Long userId,
            @AuthenticationPrincipal User user,
            @ParameterObject Pageable pageable
    ) {
        if (user.getAuthorities().stream().anyMatch(RoleUtil::isAdminRole)) {
            return PageResponse.of(paymentService.getPayments(userId, pageable));
        }
        return PageResponse.of(paymentService.getPayments(user.getId(), pageable));
    }

    @ApiOperationDetails(
            summary = "Initiate a payment for a booking",
            description = "Allows a user to initiate a payment process for a particular booking.",
            responseDescription = "Details of the initiated payment are returned upon successful "
                    + "creation.",
            responseCode = ApiResponseConstants.RESPONSE_CODE_CREATED
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CreateApiResponses
    @PreAuthorize("hasRole('ROLE_USER')")
    public PaymentDto createPayment(
            @RequestBody @Valid CreatePaymentRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return paymentService.initiatePayment(requestDto, user);
    }
}
