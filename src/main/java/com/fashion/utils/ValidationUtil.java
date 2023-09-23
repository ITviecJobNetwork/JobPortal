package com.fashion.utils;

import lombok.experimental.UtilityClass;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.Set;

@UtilityClass
public class ValidationUtil {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private Validator validator = factory.getValidator();

    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?> type) {
        return validator.validate(object, type);
    }

    public Set<ConstraintViolation<Object>> validate(Object obj) {
        return validate(obj, Default.class);
    }
}
