package ua.service.vehicles.vehicle;

import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.service.vehicles.Identifiable;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EntityExistenceValidator implements ConstraintValidator<EntityExists, Identifiable<Long>> {

    private final ApplicationContext applicationContext;

    public EntityExistenceValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean isValid(Identifiable<Long> identifiable, ConstraintValidatorContext constraintValidatorContext) {
        if (identifiable == null || identifiable.getId() == null) {
            return false;
        }

        String repositoryName = identifiable.getClass().getSimpleName() + "Repository";
        JpaRepository repository = applicationContext.getBean(
                Character.toLowerCase(repositoryName.charAt(0)) + repositoryName.substring(1),
                JpaRepository.class);

        return repository.existsById(identifiable.getId());
    }
}
