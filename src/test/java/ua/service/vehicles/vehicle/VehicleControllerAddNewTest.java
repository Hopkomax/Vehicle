package ua.service.vehicles.vehicle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.service.vehicles.manufacturer.Manufacturer;
import ua.service.vehicles.manufacturer.ManufacturerRepository;
import ua.service.vehicles.person.Person;
import ua.service.vehicles.person.PersonRepository;
import ua.service.vehicles.services.DecodedVin;
import ua.service.vehicles.services.VinDecoderService;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class VehicleControllerAddNewTest {

    final String uri = "/vehicle/addNew";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    @MockBean
    private VinDecoderService vinDecoderServiceMock;
    Map<String, Object> params = new LinkedHashMap<>();
    private final Long id  = 1l;
    private final String vin = "5TFEV54198X043410";
    private final String country_ua = "UA";
    private final String country_h = "H";
    private final String make = "BMW";
    private final String model = "M4";
    private final int year = 2020;
    DecodedVin decodedVin = new DecodedVin(make, model, year);
    private final String registration_ua = "AO1486AI";
    private final String registration_h = "RND-150";
    private final String fuelType = "DIESEL";
    private final String transmissionType = "MANUAL";
    private final int doorsCount = 4;
    private final boolean rwd = false;
    private long ownerId;
    private long manufacturerId;


    @BeforeEach
    void setUp() throws IOException {
        Manufacturer manufacturer = new Manufacturer();
        Person owner = new Person();
        manufacturerRepository.save(manufacturer);
        personRepository.save(owner);
        ownerId = owner.getId();
        manufacturerId = manufacturer.getId();

        doReturn(decodedVin).when(vinDecoderServiceMock).decodeVin(eq(vin));

        params.put("id", id);
        params.put("vin", vin);
        params.put("manufacturer", Collections.singletonMap("id", manufacturerId));
        params.put("make", make);
        params.put("model", model);
        params.put("owner", Collections.singletonMap("id", ownerId));
        params.put("year", year);
        params.put("country", country_ua);
        params.put("registration", registration_ua);
        params.put("fuelType", fuelType);
        params.put("transmissionType", transmissionType);
        params.put("doorsCount", doorsCount);
        params.put("rwd", rwd);
    }

    @AfterEach
    void tearDown() {
        vehicleRepository.deleteAll();
        manufacturerRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    @WithUserDetails("admin")
    void verifyMakeIsTakenFromVinDecoder() throws Exception {
        params.remove("make");

        sendRequestExpectingOkStatus();

        assertEquals(make, vehicleRepository.findAll().get(0).getMake());
    }

    @Test
    @WithUserDetails("admin")
    void verifyModelIsTakenFromVinDecoder() throws Exception {
        params.remove("model");

        sendRequestExpectingOkStatus();

        assertEquals(model, vehicleRepository.findAll().get(0).getModel());
    }

    @Test
    @WithUserDetails("admin")
    void whenEverythingIsFine() throws Exception {
        sendRequestExpectingOkStatus();

        List<Vehicle> all = vehicleRepository.findAll();

        assertEquals(1, all.size());
        Vehicle vehicle = all.get(0);

        assertEquals(id, vehicle.getId());
        assertEquals(vin, vehicle.getVin());
        assertEquals(make, vehicle.getMake());
        assertEquals(model, vehicle.getModel());
        assertEquals(manufacturerId, vehicle.getManufacturer().getId());
        assertEquals(ownerId, vehicle.getOwner().getId());
        assertEquals(year, vehicle.getYear());
        assertEquals(country_ua, vehicle.getCountry());
        assertEquals(registration_ua, vehicle.getRegistration());
        assertEquals(fuelType, vehicle.getFuelType().toString());
        assertEquals(transmissionType, vehicle.getTransmissionType().toString());
        assertEquals(doorsCount, vehicle.getDoorsCount());
        assertEquals(rwd, vehicle.isRwd());
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
    void verifyBadRequestIfVinIsNull() throws Exception {
        params.remove("vin");
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfVinIsEmpty() throws Exception {
        params.put("vin", "");
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfCountryIsEmpty() throws Exception {
        params.put("country", "");
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfManufacturerIsNull() throws Exception{
        params.put("manufacturer", null);
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfManufacturerIdIsNull() throws Exception{
        params.put("manufacturer",Collections.singletonMap("id", null));
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfOwnerIsNull() throws Exception{
        params.put("owner", null);
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestThatManufacturerRepositoryIsEmpty() throws Exception{
       manufacturerRepository.deleteAll();
       sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfOwnerGetIdIsNull() throws  Exception{
        params.put("owner",Collections.singletonMap("id", null));
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestThatPersonRepositoryIsEmpty() throws Exception{
        personRepository.deleteAll();
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfRegistrationIsNull() throws Exception{
        params.put("registration", null);
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfRegistrationIsEmpty() throws  Exception{
        params.put("registration","");
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfRegistrationFormatInvalidUa() throws Exception {
        params.put("registration", "AOA213UA");
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfRegistrationFormatInvalidH() throws Exception {
        params.put("country", country_h);
        params.put("registration", country_ua);
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void whenEverythingIsFineWithHRegistration() throws Exception {
        params.put("country", country_h);
        params.put("registration", registration_h);
        sendRequestExpectingOkStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfFuelTypeIsNull() throws Exception{
        params.put("fuelType", null);
        sendRequestExpectingBadStatus();
    }

    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfTransmissionTypeIsNull() throws Exception{
        params.put("transmissionType", null);
        sendRequestExpectingBadStatus();
    }
    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfDoorsCountIsIncorrect() throws Exception{
        params.put("doorsCount", "");
        sendRequestExpectingBadStatus();
    }
    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfRwdIsNull() throws Exception{
        params.put("rwd", null);
        sendRequestExpectingBadStatus();
    }
    @Test
    @WithUserDetails("admin")
    void verifyBadRequestIfYearIsIncorrect() throws Exception{
        params.put("year", 1);
        sendRequestExpectingBadStatus();
    }

    private void sendRequestExpectingBadStatus() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isBadRequest())
                .andReturn();


        System.out.println(mvcResult.getResponse().getContentAsString());

        assertEquals(0, vehicleRepository.count());
    }

    private void sendRequestExpectingOkStatus() throws Exception {
        mvc.perform(MockMvcRequestBuilders
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
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertEquals(0, vehicleRepository.count());
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

        assertEquals(0, vehicleRepository.count());
    }

}