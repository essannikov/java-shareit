package ru.practicum.shareit.check;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContentNotBlankValidator implements ConstraintValidator<ContentNotBlank, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        return !value.trim().isEmpty();
    }
}
