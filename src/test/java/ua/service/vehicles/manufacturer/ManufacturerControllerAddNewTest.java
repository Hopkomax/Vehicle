package ua.service.vehicles.manufacturer;

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
import ua.service.vehicles.person.Person;
import ua.service.vehicles.person.PersonRepository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ManufacturerControllerAddNewTest {

    final String uri = "/manufacturer/addNew";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    @Autowired
    private PersonRepository personRepository;

    Map<String, Object> params = new LinkedHashMap<>();

    private long ceoId;
    private final String name = "Maks";
    private final String country = "Ukraine";

    @BeforeEach
    void setUp() {
        Person ceo = new Person();
        personRepository.save(ceo);
        ceoId=ceo.getId();

        params.put("name", name);
        params.put("country", country);
        params.put("ceo", Collections.singletonMap("id", ceoId));
    }

    @AfterEach
    void tearDown(){
        manufacturerRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    @WithUserDetails("admin")
    void whenEverythingIsFine() throws Exception {
        sendRequestExpectingOkStatus();

        List<Manufacturer> all = manufacturerRepository.findAll();

        assertEquals(1, all.size());
        Manufacturer manufacturer = all.get(0);
        assertEquals(name, manufacturer.getName());
        assertEquals(country, manufacturer.getCountry());
        assertEquals(ceoId, manufacturer.getCeo().getId());

    }

    @Test
    @WithUserDetails("admin")
    void verifyNoContentRequestResultsInBadRequest() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("[\"request content is not supplied\"]", mvcResult.getResponse().getContentAsString());
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfContentInvalid() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{]"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("[\"request content unreadable\"]", mvcResult.getResponse().getContentAsString());
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfNameIsNull() throws Exception {
        params.remove("name");
        sendRequestExpectingBadStatus();
    }
    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfNameIsEmpty() throws Exception {
        params.put("name", "");
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfCountryIsNull() throws Exception{
        params.put("country", null);
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfCountryIsEmpty() throws Exception{
        params.put("country", "");
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfCeoIsNull() throws Exception{
        params.put("ceo", null);
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfCeoIdIsNull() throws Exception{
        params.put("ceo", Collections.singletonMap("id", null));
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestThatPersonRepositoryIsEmpty() throws Exception{
        personRepository.deleteAll();
        sendRequestExpectingBadStatus();
    }

    private void sendRequestExpectingBadStatus() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals(0, manufacturerRepository.count());
    }

     private void sendRequestExpectingOkStatus() throws Exception {
         MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void verifyBadRequestIfUserNotAuthenticated() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertEquals(0, manufacturerRepository.count());
    }

    @Test
    @WithUserDetails("user")
    void verifyBadRequestIfUserDoesntHaveAdminRole() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isForbidden())
                .andReturn();

        assertEquals(0, manufacturerRepository.count());
    }

}