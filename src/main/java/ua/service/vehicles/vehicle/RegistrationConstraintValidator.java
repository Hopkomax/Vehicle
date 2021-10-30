package ua.service.vehicles.vehicle;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;

public class RegistrationConstraintValidator implements ConstraintValidator<RegistrationFormat, Vehicle> {

    private final Map<String, String> registrationFormats = new HashMap<>();

    public RegistrationConstraintValidator() {
        initCountryFormats();
    }

    private void initCountryFormats() {
        registrationFormats.put("UA", "[ABCEHIKMOPTX]{2}[\\d]{4}[ABCEHIKMOPTX]{2}");
        registrationFormats.put("H", "[A-Z]{3}-[\\d]{3}");
    }

    @Override
    public boolean isValid(Vehicle vehicle, ConstraintValidatorContext constraintValidatorContext) {
        String country = vehicle.getCountry();
        String registration = vehicle.getRegistration();

        if (country == null || country.isEmpty() || registration == null || registration.isEmpty()) {
            return true;
        }

        return registrationFormats.containsKey(country) && registration.matches(registrationFormats.get(country));

    }

}
