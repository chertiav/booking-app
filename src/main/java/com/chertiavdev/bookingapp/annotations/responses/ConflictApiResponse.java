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
        responseCode = "409",
        description = "Client error",
        content = @Content(schema = @Schema(
                implementation = CommonApiErrorResponseDto.class),
                examples = @ExampleObject(
                        name = "Conflict Error Example",
                        summary = "A conflict occurred while processing the request, such as "
                                + "duplicate data or violation of constraints.",
                        value = ExampleValues.CONFLICT_ERROR_EXAMPLE)))
public @interface ConflictApiResponse {
}
