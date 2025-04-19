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
@ApiResponse(responseCode = "403",
        description = "Access denied",
        content = @Content(schema = @Schema(
                implementation = CommonApiErrorResponseDto.class),
                examples = @ExampleObject(
                        name = "Forbidden Error Example",
                        summary = "An example response for a forbidden access attempt when the "
                                + "user does not have the required permissions.",
                        value = ExampleValues.FORBIDDEN_ERROR_EXAMPLE
                )
        )
)
public @interface ForbiddenApiResponse {
}
