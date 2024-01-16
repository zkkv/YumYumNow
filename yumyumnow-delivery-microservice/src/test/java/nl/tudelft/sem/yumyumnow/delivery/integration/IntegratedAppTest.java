package nl.tudelft.sem.yumyumnow.delivery.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import nl.tudelft.sem.yumyumnow.delivery.controllers.DeliveryController;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryPostRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

import java.math.BigDecimal;
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
        wireMockServerUser = new WireMockServer(8081);
        wireMockServerUser.start();
        wireMockServerOrder = new WireMockServer(8083);
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

        Vendor vendor = new Vendor(vendorUUID, new Location(), "", false, BigDecimal.TEN);
        String vendorJson = ow.writeValueAsString(vendor);

        Customer customer = new Customer(customerUUID, "Alex", new Location(), "", "");
        String customerJson = ow.writeValueAsString(customer);

        Order order = new Order(orderUUID, vendor,customer);
        String orderJson = ow.writeValueAsString(order);

//        wireMockServerUser.stubFor(get(urlEqualTo("/vendor/7ddf1a10-8dfa-11ee-b9d1-0242ac120002"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(vendorJson)
//                        ));

        wireMockServerUser.stubFor(get(urlEqualTo("/customer/" + customerUUID))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(customerJson)));

        wireMockServerOrder.stubFor(get(urlEqualTo("/order/7ddf1a10-8dfa-11ee-b9d1-0242ac120002"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(orderJson)));


        this.mockMvc.perform(post("/delivery").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, World")));

    }
}
