package com.chertiavdev.bookingapp.validation.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.io.IOException;
import java.util.Arrays;

public class GenericEnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> {
    private final Class<T> enumType;

    public GenericEnumDeserializer(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();
        if (value == null || value.trim().isEmpty()) {
            throw InvalidFormatException.from(
                    parser,
                    String.format("Value for %s cannot be null or empty", enumType.getSimpleName()),
                    value,
                    enumType
            );
        }
        try {
            return Enum.valueOf(enumType, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw InvalidFormatException.from(
                    parser,
                    String.format("Invalid value '%s' for %s. Expected one of %s",
                            value, enumType.getSimpleName(),
                            Arrays.toString(enumType.getEnumConstants())),
                    value,
                    enumType
            );
        }
    }
}

