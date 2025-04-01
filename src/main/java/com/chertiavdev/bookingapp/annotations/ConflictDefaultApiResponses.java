package com.chertiavdev.bookingapp.annotations;

import com.chertiavdev.bookingapp.dto.error.CommonApiErrorResponseDto;
import com.chertiavdev.bookingapp.util.ApiResponseConstants;
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
@ApiResponse(
        responseCode = ApiResponseConstants.RESPONSE_CODE_CONFLICT,
        description = ApiResponseConstants.CONFLICT_ERROR_DESCRIPTION,
        content = @Content(schema = @Schema(
                implementation = CommonApiErrorResponseDto.class),
                examples = @ExampleObject(
                        name = ApiResponseConstants
                                .CONFLICT_ERROR_EXAMPLE_MESSAGE,
                        summary = ApiResponseConstants
                                .CONFLICT_ERROR_EXAMPLE_DESCRIPTION,
                        value = ExampleValues.CONFLICT_ERROR_ERROR_EXAMPLE)))
public @interface ConflictDefaultApiResponses {
}
