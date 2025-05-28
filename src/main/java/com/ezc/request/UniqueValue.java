package com.ezc.request;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ezc.validator.UniqueValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = UniqueValueValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueValue {

    String message() default "Data already exits";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> entity();

    String field() default "id";

    /**
     Name of the ID field that can be excluded  Usually "id"
     */
    String exceptIdField() default "";

    /**
     * Where do you get the except id value from? (usually from another request)
     */
    String exceptIdSourceField() default "";
}
