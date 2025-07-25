package com.chertiavdev.bookingapp.exception;

import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.dto.error.ErrorDetailDto;
import java.time.LocalDateTime;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        log.error("Validation failed for request. First 5 errors: {}",
                ex.getBindingResult().getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .limit(5)
                        .toList(), ex);
        return buildResponseEntity(
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                ex.getBindingResult().getAllErrors().stream()
                        .map(this::getErrorDetails)
                        .toList()
        );
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<Object> handleRegistrationException(RegistrationException ex) {
        log.error("RegistrationException occurred: {}", ex.getMessage(), ex);
        return buildResponseEntity(
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                getErrorMessage(ex, "Bad credentials for registration.")
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    protected ResponseEntity<Object> handleAuthorizationDeniedException(
            AuthorizationDeniedException ex) {
        log.error("AuthorizationDeniedException occurred: {}", ex.getMessage(), ex);
        return buildResponseEntity(
                HttpStatus.FORBIDDEN,
                LocalDateTime.now(),
                getErrorMessage(ex, "Access denied")
        );
    }

    @ExceptionHandler(SpecificationProviderNotFoundException.class)
    public ResponseEntity<Object> handleSpecificationProviderNotFoundException(
            SpecificationProviderNotFoundException ex
    ) {
        log.warn("SpecificationProviderNotFoundException occurred: {}", ex.getMessage());
        return buildResponseEntity(
                HttpStatus.NOT_FOUND,
                LocalDateTime.now(),
                getErrorMessage(ex, "The requested specification provider "
                        + "could not be found.")
        );
    }

    @ExceptionHandler(StripeServiceException.class)
    public ResponseEntity<Object> handleStripeServiceException(
            StripeServiceException ex
    ) {
        log.error("StripeServiceException occurred: {}", ex.getMessage());
        return buildResponseEntity(
                HttpStatus.SERVICE_UNAVAILABLE,
                LocalDateTime.now(),
                getErrorMessage(ex, "An error occurred while interacting with the "
                        + "Stripe API. Please try again later.")
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        log.error("AuthenticationException occurred: {}", ex.getMessage());
        return buildResponseEntity(
                HttpStatus.UNAUTHORIZED,
                LocalDateTime.now(),
                getErrorMessage(ex, "Invalid email or password.")
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("EntityNotFoundException occurred: {}", ex.getMessage());
        return buildResponseEntity(
                HttpStatus.NOT_FOUND,
                LocalDateTime.now(),
                getErrorMessage(ex, "Entity was not found.")
        );
    }

    @ExceptionHandler(NotificationException.class)
    protected ResponseEntity<Object> handleNotificationException(NotificationException ex) {
        log.warn("NotificationException occurred: {}", ex.getMessage());
        return buildResponseEntity(
                HttpStatus.SERVICE_UNAVAILABLE,
                LocalDateTime.now(),
                getErrorMessage(ex, "An error occurred while attempting "
                        + "to send a notification.")
        );
    }

    @ExceptionHandler(AccommodationAlreadyExistsException.class)
    public ResponseEntity<Object> handleAccommodationAlreadyExists(
            AccommodationAlreadyExistsException ex
    ) {
        log.warn("AccommodationAlreadyExistsException occurred: {}", ex.getMessage());
        return buildResponseEntity(
                HttpStatus.CONFLICT,
                LocalDateTime.now(),
                getErrorMessage(ex, "Accommodation with the same data already exists")
        );
    }

    @ExceptionHandler(AccommodationAvailabilityException.class)
    public ResponseEntity<Object> handleAccommodationAvailabilityException(
            AccommodationAvailabilityException ex
    ) {
        log.warn("AccommodationAvailabilityException occurred: {}", ex.getMessage());
        return buildResponseEntity(
                HttpStatus.CONFLICT,
                LocalDateTime.now(),
                getErrorMessage(ex, "Accommodation is not available")
        );
    }

    @ExceptionHandler(BookingAlreadyCancelledException.class)
    public ResponseEntity<Object> handleBookingAlreadyCancelledException(
            BookingAlreadyCancelledException ex
    ) {
        log.warn("BookingAlreadyCancelledException occurred: {}", ex.getMessage());
        return buildResponseEntity(
                HttpStatus.CONFLICT,
                LocalDateTime.now(),
                getErrorMessage(ex, "Accommodation is not available")
        );
    }

    @ExceptionHandler(PaymentRenewException.class)
    public ResponseEntity<Object> handlePaymentRenewException(
            PaymentRenewException ex
    ) {
        log.warn("PaymentRenewException occurred: {}", ex.getMessage());
        return buildResponseEntity(
                HttpStatus.CONFLICT,
                LocalDateTime.now(),
                getErrorMessage(ex, "Payment renewal could not be completed due to a"
                        + " conflict with the current state of the payment.")
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException ex
    ) {
        log.warn("AccessDeniedException occurred: {}", ex.getMessage());
        return buildResponseEntity(
                HttpStatus.FORBIDDEN,
                LocalDateTime.now(),
                getErrorMessage(ex, "Access denied")
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleHttpMessageConversionException(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        log.error("HttpMessageConversionException occurred: {}", ex.getMessage(), ex);
        return buildResponseEntity(
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                getErrorMessage(ex, "Invalid input. Failed to convert provided value.")
        );
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGlobalException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        return buildResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now(),
                getErrorMessage(ex, "An unexpected error occurred. "
                        + "Please try again later.")
        );
    }

    private ResponseEntity<Object> buildResponseEntity(
            HttpStatus status,
            LocalDateTime timestamp,
            Object errorMessage) {
        CommonApiErrorResponseDto commonApiErrorResponseDto =
                new CommonApiErrorResponseDto(status, timestamp, errorMessage);
        return new ResponseEntity<>(commonApiErrorResponseDto, status);
    }

    private ErrorDetailDto getErrorDetails(ObjectError objectError) {
        ErrorDetailDto errorDetail = new ErrorDetailDto();
        if (objectError instanceof FieldError) {
            errorDetail.setField(((FieldError) objectError).getField());
        } else {
            errorDetail.setField(objectError.getObjectName());
        }
        errorDetail.setMessage(objectError.getDefaultMessage() != null
                ? objectError.getDefaultMessage()
                : "Validation failed for object, please check your input");
        return errorDetail;
    }

    private String getErrorMessage(Exception ex, String defaultMessage) {
        return ex.getMessage() != null ? ex.getMessage() : defaultMessage;
    }
}
