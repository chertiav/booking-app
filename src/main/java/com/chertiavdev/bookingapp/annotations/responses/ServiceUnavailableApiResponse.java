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
@ApiResponse(responseCode = "503",
        description = "Service unavailable due to a failure in sending a notification",
        content = @Content(schema = @Schema(
                implementation = CommonApiErrorResponseDto.class),
                examples = @ExampleObject(
                        name = "Service Unavailable Error Example",
                        summary = "An example response when the notification service "
                                + "is unavailable",
                        value = ExampleValues.SERVICE_UNAVAILABLE_ERROR_EXAMPLE
                )
        )
)
public @interface ServiceUnavailableApiResponse {
}
