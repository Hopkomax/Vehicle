package ua.service.vehicles.vehicle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
class CountryBasedSequenceIdentifierGeneratorTest {

    @Autowired
    private VehicleRepository vehicleRepository;
    Vehicle vehicle = new Vehicle();

    @AfterEach
    void tearDown() {
        vehicleRepository.deleteAll();
    }

    @Test
    @WithUserDetails("admin")
    void whenWithUaIdEverythingIsFine() {
        vehicle.setCountry("ua");
        assertWithinTheRange(vehicleRepository.save(vehicle).getId(), 200000000);
    }

    @Test
    @WithUserDetails("admin")
    void testForSk() {
        vehicle.setCountry("Sk");
        assertWithinTheRange(vehicleRepository.save(vehicle).getId(), 500000000);
    }

    @Test
    @WithUserDetails("admin")
    void testForUa() {
        for (int i = 0; i <= 100; i++) {
            Vehicle vehicle = vehicleRepository.save(initializationCountry(new Vehicle(),"ua"));
            assertWithinTheRange(vehicle.getId(), 200000000);
        }
    }

    @Test
    @WithUserDetails("admin")
    void whenSequenceForOtherIdIsFine() {
        for (int i = 0; i <= 100; i++) {
            Vehicle vehicle = vehicleRepository.save(initializationCountry(new Vehicle(),"GB"));
            assertWithinTheRange(vehicle.getId(), 500000000);
        }
    }

    public Vehicle initializationCountry(Vehicle vehicle, String country){
        vehicle.setCountry(country);
        return vehicle;
    }

    private void assertWithinTheRange(long id, long initialValue) {
        assertTrue(id >= initialValue && id < initialValue + 1000);
    }
}