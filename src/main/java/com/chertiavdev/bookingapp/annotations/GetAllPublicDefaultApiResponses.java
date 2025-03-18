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
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = ApiResponseConstants.RESPONSE_CODE_BAD_REQUEST,
                description = ApiResponseConstants.INVALID_REQUEST_DESCRIPTION,
                content = @Content(schema = @Schema(
                        implementation = CommonApiErrorResponseDto.class),
                        examples = @ExampleObject(
                                name = ApiResponseConstants
                                        .GENERAL_ERROR_EXAMPLE_MESSAGE,
                                summary = ApiResponseConstants
                                        .GENERAL_ERROR_EXAMPLE_DESCRIPTION,
                                value = ExampleValues.COMMON_ERROR_EXAMPLE))),
        @ApiResponse(
                responseCode = ApiResponseConstants.RESPONSE_CODE_INTERNAL_SERVER_ERROR,
                description = ApiResponseConstants.INTERNAL_SERVER_ERROR_DESCRIPTION,
                content = @Content(schema = @Schema(
                        implementation = CommonApiErrorResponseDto.class),
                        examples = @ExampleObject(
                                name = ApiResponseConstants
                                        .INTERNAL_SERVER_ERROR_EXAMPLE_MESSAGE,
                                summary = ApiResponseConstants
                                        .INTERNAL_SERVER_ERROR_EXAMPLE_DESCRIPTION,
                                value = ExampleValues
                                        .INTERNAL_SERVER_ERROR_ERROR_EXAMPLE)))
})
public @interface GetAllPublicDefaultApiResponses {
}
