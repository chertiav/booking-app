package com.chertiavdev.bookingapp.validation.json;

import com.chertiavdev.bookingapp.model.Accommodation.Type;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class TypeDeserializer extends JsonDeserializer<Type> {
    @Override
    public Type deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText().toUpperCase();
        try {
            return Type.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

