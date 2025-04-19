package com.chertiavdev.bookingapp.annotations.responses;

import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.util.ExampleValues;
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
@ApiResponse(responseCode = "404",
        description = "Resource not found",
        content = @Content(schema = @Schema(
                implementation = CommonApiErrorResponseDto.class),
                examples = @ExampleObject(
                        name = "Not Found Error Example",
                        summary = "An example of an error where the requested resource isn't found",
                        value = ExampleValues.NOT_FOUND_ERROR_EXAMPLE
                )
        )
)
public @interface NotFoundApiResponse {
}
