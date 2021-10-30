package ua.service.vehicles.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@MockServerTest("server.url=http://localhost:1080")
@ContextConfiguration(classes = VinDecoderService.class)
public class VinDecoderServiceTest {

   private final String vin = "KM8JM12D56U303366";
   private static ClientAndServer clientAndServer;
   private String year = "2005";
   private String make = "BMW";
   private String model = "M4";

   @Autowired
   private VinDecoderService vinDecoderService;

   @BeforeAll
   public static void startServer() {
      clientAndServer = startClientAndServer(1080);
   }
   @AfterAll
   public static void stopServer() {
      clientAndServer.stop();
   }

   @Test
   public void whenEverythingIsFine() throws IOException {

      clientAndServer
              .when(
                      request()
                              .withMethod("GET")
                              .withPath("/v3.0/decode")
                              .withHeader("Content-Type", "application/json")
                              .withHeader("Authorization", "Basic YTUzN2M2YmQtZDQwNi00OGZhLWEwZTctMzVhYjExMGQ1YjE2")
                              .withHeader("partner-token", "8ad06ec2e7c045a19376c983208d1e31")
                              .withQueryStringParameter("vin", vin),
                      exactly(1))
              .respond(
                      response()
                              .withStatusCode(200)
                              .withContentType(MediaType.parse("application/json"))
                              .withBody(
                                      "{\n \"message\":{\n" +
                                      "    \"code\":0,\n" +
                                      "    \"message\":\"make\",\n" +
                                      "    \"credentials\":\"valid\",\n" +
                                      "    \"version\":\"v3.0.0\",\n" +
                                      "    \"endpoint\":\"tsb\",\n" +
                                      "    \"counter\":101\n  },\n" +
                                          "\"data\":{\"year\":" + year +
                                              ",\"make\":" +make +
                                              ",\"model\":" + model + "}}")
                              .withDelay(TimeUnit.SECONDS, 1));
      vinDecoderService.decodeVin(vin);
   }
}