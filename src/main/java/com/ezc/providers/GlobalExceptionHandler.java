package com.ezc.providers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ezc.helper.ResponseHelper;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseHelper responseHelper;

    public GlobalExceptionHandler(ResponseHelper responseHelper) {
        this.responseHelper = responseHelper;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        return responseHelper.error("Failed validation", errors, false, 400);
    }
}
