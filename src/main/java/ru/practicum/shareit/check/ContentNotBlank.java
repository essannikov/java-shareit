package ru.practicum.shareit.check;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ContentNotBlankValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentNotBlank {
    String message() default "Значение не должно быть пустым";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}