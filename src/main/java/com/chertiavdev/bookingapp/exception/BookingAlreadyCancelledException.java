package com.chertiavdev.bookingapp.exception;

public class BookingAlreadyCancelledException extends RuntimeException {
    public BookingAlreadyCancelledException(String message) {
        super(message);
    }
}
