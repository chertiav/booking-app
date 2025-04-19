package com.chertiavdev.bookingapp.annotations.responses.groups;

import com.chertiavdev.bookingapp.annotations.responses.ForbiddenApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.InternalServerErrorApiResponse;
import com.chertiavdev.bookingapp.annotations.responses.UnauthorizedApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ForbiddenApiResponse
@UnauthorizedApiResponse
@InternalServerErrorApiResponse
public @interface BaseAuthApiResponses {
}
