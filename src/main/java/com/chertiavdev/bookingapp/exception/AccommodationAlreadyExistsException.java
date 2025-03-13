package com.chertiavdev.bookingapp.exception;

public class AccommodationAlreadyExistsException extends RuntimeException {
    public AccommodationAlreadyExistsException(String message) {
        super(message);
    }
}
