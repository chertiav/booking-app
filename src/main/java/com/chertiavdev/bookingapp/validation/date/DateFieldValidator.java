package com.chertiavdev.bookingapp.validation.date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class DateFieldValidator implements ConstraintValidator<DateFieldMatch, Object> {
    private String firstFieldValue;
    private String secondFieldValue;

    @Override
    public void initialize(DateFieldMatch constraintAnnotation) {
        firstFieldValue = constraintAnnotation.startDate();
        secondFieldValue = constraintAnnotation.endDate();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        try {
            BeanWrapper beanWrapper = new BeanWrapperImpl(value);
            LocalDate startDate = (LocalDate) beanWrapper.getPropertyValue(firstFieldValue);
            LocalDate endDate = (LocalDate) beanWrapper.getPropertyValue(secondFieldValue);

            return startDate != null
                    && endDate != null
                    && !startDate.isBefore(LocalDate.now())
                    && !endDate.isBefore(startDate.plusDays(1));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
