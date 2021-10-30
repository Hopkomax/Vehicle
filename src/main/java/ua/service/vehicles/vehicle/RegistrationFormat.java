package ua.service.vehicles.vehicle;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RegistrationConstraintValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistrationFormat {

    String message() default "Registration format invalid for the specified country.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
