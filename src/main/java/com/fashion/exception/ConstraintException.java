package com.fashion.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.ConstraintViolation;
import java.util.Set;

@AllArgsConstructor
@Getter
public class ConstraintException extends RuntimeException {

    private Object requestBody;
    private Set<ConstraintViolation<Object>> constraints;

    public ConstraintException(String message) {
        super(message);
    }
}
