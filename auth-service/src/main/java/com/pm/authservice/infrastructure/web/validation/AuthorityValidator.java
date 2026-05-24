package com.pm.authservice.infrastructure.web.validation;

import com.pm.authservice.infrastructure.persistence.repository.RoleJpaRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class AuthorityValidator implements ConstraintValidator<Authority, String> {

    private final RoleJpaRepository repository;

    public AuthorityValidator(RoleJpaRepository repository){
        this.repository=repository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            return repository.existsByName(value);
        }
        return true;
    }
}
