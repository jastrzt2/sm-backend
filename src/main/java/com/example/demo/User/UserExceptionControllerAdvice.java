package com.example.demo.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class UserExceptionControllerAdvice {

    @ExceptionHandler({EmailAlreadyUsedException.class, UsernameAlreadyUsedException.class})
    public ResponseEntity<List<Map<String, String>>> handleUserExceptions(RuntimeException ex) {
        List<Map<String, String>> errors = new ArrayList<>();

        if (ex instanceof EmailAlreadyUsedException) {
            errors.add(createErrorMap("email", ex.getMessage()));
        } else if (ex instanceof UsernameAlreadyUsedException) {
            errors.add(createErrorMap("username", ex.getMessage()));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    private Map<String, String> createErrorMap(String field, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("field", field);
        error.put("message", message);
        return error;
    }
}