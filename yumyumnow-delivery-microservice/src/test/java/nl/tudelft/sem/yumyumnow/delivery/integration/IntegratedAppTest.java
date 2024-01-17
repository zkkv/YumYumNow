package nl.tudelft.sem.yumyumnow.delivery.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import nl.tudelft.sem.yumyumnow.delivery.controllers.DeliveryController;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryPostRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.hibernate.type.OffsetDateTimeType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import wiremock.org.apache.hc.client5.http.classic.methods.HttpGet;
import wiremock.org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import wiremock.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class IntegratedAppTest {
    @Autowired
    private DeliveryController deliveryController;

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateBuilder restTemplate;

    @Autowired
    private MockMvc mockMvc;

    private static ObjectMapper mapper;

    private static ObjectWriter ow;


    public static WireMockServer wireMockServerUser;
    public static WireMockServer wireMockServerOrder;




    @BeforeAll
    static void startWireMock(){
        WireMockConfiguration configurationUser = WireMockConfiguration.options()
                .port(8081);
        wireMockServerUser = new WireMockServer(configurationUser);
        WireMockConfiguration configurationOrder = WireMockConfiguration.options()
                .port(8083);
        wireMockServerOrder = new WireMockServer(configurationOrder);

        wireMockServerUser.start();
        wireMockServerOrder.start();

        mapper = new ObjectMapper();
        ow = mapper.writer().withDefaultPrettyPrinter();

    }

    @AfterAll
    static void endWireMock(){
        wireMockServerOrder.stop();
        wireMockServerUser.stop();
    }

    @Test
    void contextLoads() {
        assertThat(deliveryController).isNotNull();
    }


    @Test
    void getDelivery() {
        assertThrows(HttpClientErrorException.BadRequest.class, () -> this.restTemplate.build().getForObject("http://localhost:" + port + "/delivery/1",
                String.class));
    }

    @Test
    void createDeliveryTest() throws Exception {
        UUID vendorUUID = UUID.fromString("7ddf1a10-8dfa-11ee-b9d1-0242ac120002");
        UUID orderUUID = UUID.fromString("7ddf1a10-8dfa-11ee-b9d1-0242ac120002");
        UUID customerUUID = UUID.randomUUID();

        DeliveryPostRequest deliveryPostRequest = new DeliveryPostRequest();
        deliveryPostRequest.setOrderId(vendorUUID);
        deliveryPostRequest.setVendorId(orderUUID);
        String requestJson = ow.writeValueAsString(deliveryPostRequest);

        Location location = new Location();
        location.setLatitude(BigDecimal.ONE);
        location.setLongitude(BigDecimal.ONE);

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);
        location.setTimestamp(offsetDateTime);

        String vendorString = "{\n" +
                "  \"userID\" : \"7ddf1a10-8dfa-11ee-b9d1-0242ac120002\",\n" +
                "  \"location\" : {\n" +
                "    \"timestamp\" : {\n" +
                "      \"offset\" : {\n" +
                "        \"totalSeconds\" : 0,\n" +
                "        \"id\" : \"Z\",\n" +
                "        \"rules\" : {\n" +
                "          \"fixedOffset\" : true,\n" +
                "          \"transitions\" : [ ],\n" +
                "          \"transitionRules\" : [ ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"year\" : 2023,\n" +
                "      \"monthValue\" : 12,\n" +
                "      \"dayOfMonth\" : 10,\n" +
                "      \"hour\" : 12,\n" +
                "      \"minute\" : 0,\n" +
                "      \"second\" : 0,\n" +
                "      \"nano\" : 0,\n" +
                "      \"dayOfWeek\" : \"SUNDAY\",\n" +
                "      \"dayOfYear\" : 344,\n" +
                "      \"month\" : \"DECEMBER\"\n" +
                "    },\n" +
                "    \"latitude\" : 1,\n" +
                "    \"longitude\" : 1\n" +
                "  },\n" +
                "  \"contactInfo\" : {\n" +
                "    \"phoneNumber\" : \"\"\n" +
                "  },\n" +
                "  \"allowOnlyOwnCouriers\" : false,\n" +
                "  \"maxDeliveryZoneKm\" : 10\n" +
                "}";

        String customerString = "{\n" +
                "  \"userID\" : \"" + customerUUID + "\",\n" +
                "  \"name\" : \"Alex\",\n" +
                "  \"location\" : {\n" +
                "    \"timestamp\" : {\n" +
                "      \"offset\" : {\n" +
                "        \"totalSeconds\" : 0,\n" +
                "        \"id\" : \"Z\",\n" +
                "        \"rules\" : {\n" +
                "          \"fixedOffset\" : true,\n" +
                "          \"transitions\" : [ ],\n" +
                "          \"transitionRules\" : [ ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"year\" : 2023,\n" +
                "      \"monthValue\" : 12,\n" +
                "      \"dayOfMonth\" : 10,\n" +
                "      \"hour\" : 12,\n" +
                "      \"minute\" : 0,\n" +
                "      \"second\" : 0,\n" +
                "      \"nano\" : 0,\n" +
                "      \"dayOfWeek\" : \"SUNDAY\",\n" +
                "      \"dayOfYear\" : 344,\n" +
                "      \"month\" : \"DECEMBER\"\n" +
                "    },\n" +
                "    \"latitude\" : 1,\n" +
                "    \"longitude\" : 1\n" +
                "  },\n" +
                "  \"contactInfo\" : {\n" +
                "    \"email\" : \"\",\n" +
                "    \"phoneNumber\" : \"\"\n" +
                "  }\n" +
                "}";

        String orderString = "{\n" +
                "  \"id\" : \"7ddf1a10-8dfa-11ee-b9d1-0242ac120002\",\n" +
                "  \"vendor\" : {\n" +
                "    \"id\" : null,\n" +
                "    \"address\" : null,\n" +
                "    \"phone\" : null,\n" +
                "    \"allowsOnlyOwnCouriers\" : false,\n" +
                "    \"maxDeliveryZoneKm\" : null\n" +
                "  },\n" +
                "  \"customerID\" : \""+ customerUUID +"\",\n" +
                "  \"vendorID\" : \""+ vendorUUID +"\",\n" +
                "  \"customer\" : {\n" +
                "    \"id\" : \"6b0d92c0-378b-4369-9e3c-495c48c13154\",\n" +
                "    \"name\" : \"Alex\",\n" +
                "    \"location\" : {\n" +
                "      \"timestamp\" : {\n" +
                "        \"offset\" : {\n" +
                "          \"totalSeconds\" : 0,\n" +
                "          \"id\" : \"Z\",\n" +
                "          \"rules\" : {\n" +
                "            \"fixedOffset\" : true,\n" +
                "            \"transitions\" : [ ],\n" +
                "            \"transitionRules\" : [ ]\n" +
                "          }\n" +
                "        },\n" +
                "        \"year\" : 2023,\n" +
                "        \"monthValue\" : 12,\n" +
                "        \"dayOfMonth\" : 10,\n" +
                "        \"hour\" : 12,\n" +
                "        \"minute\" : 0,\n" +
                "        \"second\" : 0,\n" +
                "        \"nano\" : 0,\n" +
                "        \"dayOfWeek\" : \"SUNDAY\",\n" +
                "        \"dayOfYear\" : 344,\n" +
                "        \"month\" : \"DECEMBER\"\n" +
                "      },\n" +
                "      \"latitude\" : 1,\n" +
                "      \"longitude\" : 1\n" +
                "    },\n" +
                "    \"email\" : \"\",\n" +
                "    \"phone\" : \"\"\n" +
                "  }\n" +
                "}";

        wireMockServerUser.stubFor(get(urlEqualTo("/vendor/7ddf1a10-8dfa-11ee-b9d1-0242ac120002"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(vendorString)
                ));

        wireMockServerUser.stubFor(get(urlEqualTo("/customer/" + customerUUID))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(customerString)));

        wireMockServerOrder.stubFor(get(urlEqualTo("/order/7ddf1a10-8dfa-11ee-b9d1-0242ac120002"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(orderString)));


        this.mockMvc.perform(post("/delivery").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"orderId\":\"7ddf1a10-8dfa-11ee-b9d1-0242ac120002\",\"courierId\":null,\"vendorId\":\"7ddf1a10-8dfa-11ee-b9d1-0242ac120002\",\"status\":\"PENDING\",\"estimatedDeliveryTime\":null,\"estimatedPreparationFinishTime\":null,\"currentLocation\":null}")));

    }
}
