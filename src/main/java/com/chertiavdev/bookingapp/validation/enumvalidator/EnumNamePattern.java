package com.chertiavdev.bookingapp.validation.enumvalidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumNamePatternValidator.class)
@Target({ElementType.FIELD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumNamePattern {
    String regexp();
    String message() default "Value must be any of {regexp}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

