package ua.service.vehicles.manufacturer;

import ua.service.vehicles.person.Person;
import ua.service.vehicles.person.PersonRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PersonIsNotAvailableValidator implements ConstraintValidator<PersonIsNotAvailable, Person> {

    public PersonRepository personRepository;

    public PersonIsNotAvailableValidator(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public boolean isValid(Person person, ConstraintValidatorContext context) {
       if (person == null || person.getId() == null){
           return false;
       }
        return personRepository.existsById(person.getId());
    }
}
