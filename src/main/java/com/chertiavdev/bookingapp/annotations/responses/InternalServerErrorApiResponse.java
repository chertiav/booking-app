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
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(schema = @Schema(
                implementation = CommonApiErrorResponseDto.class),
                examples = @ExampleObject(
                        name = "Internal server error's Error Example",
                        summary = "An example of an error where an internal server issue occurred",
                        value = ExampleValues.INTERNAL_SERVER_ERROR_ERROR_EXAMPLE
                )
        )
)
public @interface InternalServerErrorApiResponse {
}
