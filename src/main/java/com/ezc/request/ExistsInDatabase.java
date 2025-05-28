package com.ezc.request;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ezc.validator.ExistsInDatabaseValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
@Documented
@Constraint(validatedBy = ExistsInDatabaseValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsInDatabase {

    String message() default "Field not found";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    //The entity name and field name to be checked.
    Class<?> entity();

    String field() default "id";
}
