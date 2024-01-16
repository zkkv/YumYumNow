package nl.tudelft.sem.yumyumnow.delivery.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.tudelft.sem.yumyumnow.delivery.controllers.DeliveryController;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryPostRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
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
        MockRestServiceServer userServer = MockRestServiceServer.createServer(restTemplate.rootUri("http://localhost:8081").build());
        MockRestServiceServer orderServer = MockRestServiceServer.createServer(restTemplate.rootUri("http://localhost:8083").build());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

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

        userServer.expect(requestTo("http://localhost:8081/vendor/7ddf1a10-8dfa-11ee-b9d1-0242ac120002"))
                .andRespond(withSuccess(vendorJson, APPLICATION_JSON));

        orderServer.expect(requestTo("http://localhost:8083/order/7ddf1a10-8dfa-11ee-b9d1-0242ac120002"))
                .andRespond(withSuccess(orderJson, APPLICATION_JSON));

        orderServer.expect(requestTo("http://localhost:8083/order/" + customerUUID))
                .andRespond(withSuccess(customerJson, APPLICATION_JSON));

        this.mockMvc.perform(post("/delivery").contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, World")));

    }
}
