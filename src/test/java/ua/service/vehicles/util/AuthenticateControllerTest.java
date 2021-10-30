package ua.service.vehicles.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.service.vehicles.services.MyUserDetailsService;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class AuthenticateControllerTest {

    final String uri = "/authenticate";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MyUserDetailsService userDetailService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    Map<String, Object> params = new LinkedHashMap<>();

    private final String username = "user";
    private final String password = "user";

    @BeforeEach
    void setUp() {
        params.put("username", username);
        params.put("password", password);
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void verifyBadRequestIfUserIsEmpty() throws Exception {
        params.put("username", "");
        sendRequestExpectingUnauthenticated();
    }

    @Test
    void verifyBadRequestIfUsernameIsWrong() throws Exception {
        params.put("username", "aaa");
        sendRequestExpectingUnauthenticated();
    }

    @Test
    void verifyBadRequestIfPasswordIsEmpty() throws Exception {
        params.put("password", "");
        sendRequestExpectingUnauthenticated();
    }

    @Test
    void verifyBadRequestIfPasswordIsWrong() throws Exception {
        params.put("password", "aaa");
        sendRequestExpectingUnauthenticated();
    }



    @Test
    void verifyTokenIsValid() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Map<String, String> result = mapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<Map<String, String>>() {});

        assertTrue(jwtTokenUtil.validateToken(result.get("jwt"), username));
    }

    @Test
    void verifyTokenAccepted() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get("/vehicle/all")
                .header("Authorization",
                        "Bearer " + jwtTokenUtil.generateToken(userDetailService.loadUserByUsername(username))))
                .andExpect(status().isOk())
                .andReturn();
    }

    private void sendRequestExpectingUnauthenticated() throws Exception {
        mvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(params)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}