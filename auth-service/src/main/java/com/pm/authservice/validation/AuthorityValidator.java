package com.pm.authservice.validation;

import com.pm.authservice.repository.RoleRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class AuthorityValidator implements ConstraintValidator<Authority, Integer> {

    private final RoleRepository repository;

    public AuthorityValidator(RoleRepository repository){
        this.repository=repository;
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value != null) {
            return repository.existsById(value);
        }
        return true;
    }
}
