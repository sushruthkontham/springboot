package com.ezc.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.Builder;

@Component
@Builder
public class ResponseHelper {

    // ======= SUCCESS METHODS =======

    // 1. Full parameter
    public <T> ResponseEntity<Map<String, Object>> success(String message, T data, boolean status, int statusCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("statusCode", statusCode);
        body.put("message", message);
        body.put("data", data);
        body.put("errors", null);
        return ResponseEntity.status(statusCode).body(body);
    }

    // 2. message + data (status=true, statusCode=200)
    public <T> ResponseEntity<Map<String, Object>> success(String message, T data) {
        return success(message, data, true, 200);
    }

    // 3. message only (data=null, status=true, statusCode=200)
    public ResponseEntity<Map<String, Object>> success(String message) {
        return success(message, null, true, 200);
    }

    // ======= ERROR METHODS =======

    // 1. Full parameter
    public <E> ResponseEntity<Map<String, Object>> error(String message, E errors, boolean status, int statusCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("statusCode", statusCode);
        body.put("message", message);
        body.put("data", null);
        body.put("errors", errors);
        return ResponseEntity.status(statusCode).body(body);
    }

    // 2. message + errors (status=true, statusCode=200)
    public <E> ResponseEntity<Map<String, Object>> error(String message, E errors) {
        return error(message, errors, true, 400);
    }

    // 3. message only (errors=null, status=true, statusCode=200)
    public ResponseEntity<Map<String, Object>> error(String message) {
        return error(message, null, true, 400);
    }

    public  ResponseEntity<Map<String, Object>> error(String message, int statusCode){
        return error(message, null, false, statusCode);
    }
}
