package com.chertiavdev.bookingapp.annotations.responses;

import com.chertiavdev.bookingapp.annotations.examples.ExampleValues;
import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(
                implementation = CommonApiErrorResponseDto.class),
                examples = @ExampleObject(
                        name = "Unauthorized Error Example",
                        summary = "An example of an error where the user is unauthorized",
                        value = ExampleValues.UNAUTHORIZED_ERROR_EXAMPLE
                )
        )
)
public @interface UnauthorizedApiResponse {
}
