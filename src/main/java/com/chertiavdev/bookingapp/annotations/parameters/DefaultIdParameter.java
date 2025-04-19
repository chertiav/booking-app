package com.chertiavdev.bookingapp.annotations.parameters;

import io.swagger.v3.oas.annotations.Parameter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
        name = "id",
        description = "Unique identifier",
        required = true,
        example = "1"
)
public @interface DefaultIdParameter {
}

