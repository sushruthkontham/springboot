package com.ezc.validator;

import com.ezc.request.UniqueValue;
import org.springframework.beans.BeanWrapperImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueValueValidator implements ConstraintValidator<UniqueValue, Object> {

    @PersistenceContext
    private EntityManager entityManager;

    private Class<?> entityClass;
    private String fieldName;
    private String exceptIdField;
    private String exceptIdSourceField;

    @Override
    public void initialize(UniqueValue constraintAnnotation) {
        this.entityClass = constraintAnnotation.entity();
        this.fieldName = constraintAnnotation.field();
        this.exceptIdField = constraintAnnotation.exceptIdField();
        this.exceptIdSourceField = constraintAnnotation.exceptIdSourceField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        // Get the root object (the one being validated)
        BeanWrapperImpl wrapper = new BeanWrapperImpl(value);

        //Retrieve the value of the field to exclude (id)
        Object exceptId = null;
        if (!exceptIdSourceField.isBlank()) {
            exceptId = wrapper.getPropertyValue(exceptIdSourceField);
        }

        // Build a query to check the database
        StringBuilder jpql = new StringBuilder(
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE e." + fieldName + " = :value"
        );

        if (exceptId != null) {
            jpql.append(" AND e.").append(exceptIdField).append(" != :exceptId");
        }

        var query = entityManager.createQuery(jpql.toString(), Long.class)
                .setParameter("value", value);

        if (exceptId != null) {
            query.setParameter("exceptId", exceptId);
        }

        Long count = query.getSingleResult();
        return count != null && count == 0;  // If there is the same data, it means invalid
    }
}

