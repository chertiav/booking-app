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
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(
                implementation = CommonApiErrorResponseDto.class),
                examples = {
                        @ExampleObject(
                                name = "Validation Error Example",
                                summary = "An example of a validation",
                                value = ExampleValues.VALIDATION_ERROR_EXAMPLE
                        ),
                        @ExampleObject(
                                name = "General Error Example",
                                summary = "An example of a general error ",
                                value = ExampleValues.COMMON_ERROR_EXAMPLE
                        )
                }
        )
)
public @interface BadRequestApiResponse {
}
