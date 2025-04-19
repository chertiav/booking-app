package com.chertiavdev.bookingapp.annotations.operations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
        summary = "${summary}",
        description = "${description}")
@ApiResponse(
        responseCode = "${responseCode}",
        description = "${responseDescription}"
)
public @interface ApiOperationDetails {
    @AliasFor(annotation = Operation.class, attribute = "summary")
    String summary() default "Default API Summary";

    @AliasFor(annotation = Operation.class, attribute = "description")
    String description() default "Default API Description";

    @AliasFor(annotation = ApiResponse.class, attribute = "responseCode")
    String responseCode() default "200";

    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String responseDescription() default "Default response description";
}
