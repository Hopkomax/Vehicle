package ua.service.vehicles.manufacturer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ManufacturerControllerGetAllTest {

    final String uri = "/manufacturer/all";
    private Long id;

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @BeforeEach
    void setUp(){
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry("ua");
        manufacturerRepository.save(manufacturer);
        id = manufacturer.getId();
    }
    @AfterEach
    void tearDown(){
        manufacturerRepository.deleteAll();
    }

    @Test
    @WithUserDetails("admin")
    void verifyIfEverythingIsFineUsingMapper() throws Exception{
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<Map<String, Object>> responseList = mapper.readValue(jsonResponse, new TypeReference<List<Map<String, Object>>>() {});

        assertEquals(1, responseList.size());
        assertEquals("ua", responseList.get(0).get("country"));
    }

    @Test
    @WithUserDetails("admin")
    void whenEverythingIsFineUsingJacksonComparison() throws Exception{
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode responseAsTree = mapper.readTree(jsonResponse);

        assertEquals(1, responseAsTree.size());
        assertEquals(mapper.readTree(mapper.writeValueAsString(manufacturerRepository.findAll())), responseAsTree);
    }
    @Test
    void verifyBadRequestIfUserNotAuthenticated() throws Exception{
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode responseAsTree = mapper.readTree(jsonResponse);

        assertEquals(0, responseAsTree.size());
    }

}
