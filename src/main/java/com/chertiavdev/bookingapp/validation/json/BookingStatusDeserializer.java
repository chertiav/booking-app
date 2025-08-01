package com.chertiavdev.bookingapp.validation.json;

import com.chertiavdev.bookingapp.model.Booking.Status;

public class BookingStatusDeserializer extends GenericEnumDeserializer<Status> {
    public BookingStatusDeserializer() {
        super(Status.class);
    }
}
