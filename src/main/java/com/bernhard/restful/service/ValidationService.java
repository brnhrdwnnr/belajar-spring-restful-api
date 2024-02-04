package com.bernhard.restful.service;

import com.bernhard.restful.model.RegisterUserRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.validation.Validator;

import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor_ = { @Autowired})
public class ValidationService {

    private final Validator validator;

    public void validate(Object object) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
        if(!constraintViolations.isEmpty()){
            throw new ConstraintViolationException(constraintViolations);
        }
    }

}
