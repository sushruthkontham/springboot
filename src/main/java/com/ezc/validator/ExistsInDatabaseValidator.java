package com.ezc.validator;

import com.ezc.request.ExistsInDatabase;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


@Component
public class ExistsInDatabaseValidator implements ConstraintValidator<ExistsInDatabase, Object> {

    @PersistenceContext
    private EntityManager entityManager;

    private Class<?> entityClass;
    private String fieldName;

    @Override
    public void initialize(ExistsInDatabase constraintAnnotation) {
        this.entityClass = constraintAnnotation.entity();
        this.fieldName = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true; // null will be validated with NotNull if necessary

        Long count = entityManager.createQuery(
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE e." + fieldName + " = :value",
                Long.class
        )
        .setParameter("value", value)
        .getSingleResult();

        return count != null && count > 0;
    }
}
