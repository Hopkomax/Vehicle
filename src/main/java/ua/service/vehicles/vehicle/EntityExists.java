package ua.service.vehicles.vehicle;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EntityExistenceValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityExists {

    String message() default "Manufacturer id can't be null.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
