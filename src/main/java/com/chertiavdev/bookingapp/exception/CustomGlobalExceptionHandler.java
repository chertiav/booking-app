package com.chertiavdev.bookingapp.exception;

import com.chertiavdev.bookingapp.dto.error.CommonApiResponseDto;
import com.chertiavdev.bookingapp.dto.error.ErrorDetailDto;
import java.time.LocalDateTime;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        log.error("Validation failed for request. First 5 errors: {}",
                ex.getBindingResult().getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .limit(5)
                        .toList()
        );
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
        log.error("Registration failed: {}", ex.getMessage(), ex);
        return buildResponseEntity(
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                getErrorMessage(ex, "Bad credentials for registration.")
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return buildResponseEntity(
                HttpStatus.UNAUTHORIZED,
                LocalDateTime.now(),
                getErrorMessage(ex, "Invalid email or password.")
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return buildResponseEntity(
                HttpStatus.NOT_FOUND,
                LocalDateTime.now(),
                getErrorMessage(ex, "Entity was not found.")
        );
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        log.error("Application runtime error: {}", ex.getMessage(), ex);
        return buildResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now(),
                getErrorMessage(ex, "An internal runtime error has occurred.")
        );
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGlobalException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
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
        CommonApiResponseDto commonApiResponseDto =
                new CommonApiResponseDto(status, timestamp, errorMessage);
        return new ResponseEntity<>(commonApiResponseDto, status);
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
