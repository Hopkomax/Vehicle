package ua.service.vehicles.manufacturer;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PersonIsNotAvailableValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PersonIsNotAvailable {

    String message() default "Problems with filed with person.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
