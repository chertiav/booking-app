package com.chertiavdev.bookingapp.validation.json;

import com.chertiavdev.bookingapp.model.Payment.Status;

public class PaymentStatusDeserializer extends GenericEnumDeserializer<Status> {
    public PaymentStatusDeserializer() {
        super(Status.class);
    }
}
