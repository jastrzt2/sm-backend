package com.example.demo.util;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse {
    private List<FieldError> errors;

    public ValidationErrorResponse() {
        this.errors = new ArrayList<>();
    }

    public void addFieldError(String field, String message) {
        FieldError error = new FieldError(field, message);
        this.errors.add(error);
    }

    // Getters and setters
}
