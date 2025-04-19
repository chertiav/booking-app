package com.chertiavdev.bookingapp.annotations.responses.groups;

import com.chertiavdev.bookingapp.annotations.responses.BadRequestApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@BadRequestApiResponse
@BaseAuthApiResponses
public @interface CreateApiResponses {
}
