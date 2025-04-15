package com.chertiavdev.bookingapp.validation.date;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateFieldValidator.class)
@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface DateFieldMatch {
    String startDate();
    String endDate();
    String message() default "Invalid date range";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
