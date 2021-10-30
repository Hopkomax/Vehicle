package ua.service.vehicles.vehicle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class VehicleControllerDeleteTest {

    final String uri = "/vehicle/delete/";
    private Long id;

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private VehicleRepository vehicleRepository;


    @BeforeEach
    void setUp() {
        Vehicle vehicle = new Vehicle();
        vehicle.setCountry("ua");
        vehicleRepository.save(vehicle);
        id = vehicle.getId();
    }

    @AfterEach
    void tearDown() {
        vehicleRepository.deleteAll();
    }

    @Test
    void verifyAVehicleWasSavedInSetup() {
        assertEquals(1, vehicleRepository.count());
        assertEquals(id, vehicleRepository.findAll().get(0).getId());
    }

    @Test
    @WithUserDetails("admin")
    void verifyVehicleWasDeleted() throws  Exception {
       mvc.perform(MockMvcRequestBuilders
               .delete(uri + "{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andReturn();

       assertEquals(0, vehicleRepository.count());
   }

    @Test
    void verifyBadRequestIfUserNotAuthenticated() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .delete(uri + "{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertEquals(1, vehicleRepository.count());
    }

    @Test
    @WithUserDetails("user")
    void verifyBadRequestIfUserAuthenticatedAsUser() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .delete(uri + "{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        assertEquals(1, vehicleRepository.count());

}
}