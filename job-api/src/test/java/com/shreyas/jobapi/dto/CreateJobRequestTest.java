package com.shreyas.jobapi.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateJobRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldFailValidationWhenTypeIsBlank() {
        var request = new CreateJobRequest("", "PENDING");

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("type")));
    }

    @Test
    void shouldPassValidationWhenAllFieldsAreValid() {
        var request = new CreateJobRequest("EMAIL_NOTIFICATION", "PENDING");

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}
