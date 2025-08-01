package com.chertiavdev.bookingapp.validation.json;

import com.chertiavdev.bookingapp.model.Accommodation.Type;

public class TypeDeserializer extends GenericEnumDeserializer<Type> {
    public TypeDeserializer() {
        super(Type.class);
    }
}
