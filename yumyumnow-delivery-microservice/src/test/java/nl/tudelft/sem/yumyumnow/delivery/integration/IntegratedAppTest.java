package nl.tudelft.sem.yumyumnow.delivery.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import nl.tudelft.sem.yumyumnow.delivery.application.services.*;
import nl.tudelft.sem.yumyumnow.delivery.controllers.DeliveryController;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.DeliveryBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.OrderBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import org.hibernate.type.OffsetDateTimeType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import wiremock.org.apache.hc.client5.http.classic.methods.HttpGet;
import wiremock.org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import wiremock.org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import wiremock.org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @MockBean
    private VendorService vendorService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private AdminValidatorService adminValidatorService;

    @MockBean
    private DeliveryRepository deliveryRepository;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CourierService courierService;

    private static ObjectMapper mapper;

    private static ObjectWriter ow;





    @BeforeAll
    static void setUp() {

        mapper = new ObjectMapper();
        ow = mapper.writer().withDefaultPrettyPrinter();

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


        DeliveryPostRequest deliveryPostRequest = new DeliveryPostRequest();
        deliveryPostRequest.setOrderId(vendorUUID);
        deliveryPostRequest.setVendorId(orderUUID);
        String requestJson = ow.writeValueAsString(deliveryPostRequest);

        when(orderService.findOrderById(orderUUID)).thenReturn(new OrderBuilder().create());
        when(vendorService.getVendor(vendorUUID.toString())).thenReturn(new VendorBuilder().create());


        this.mockMvc.perform(post("/delivery").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"orderId\":\"7ddf1a10-8dfa-11ee-b9d1-0242ac120002\",\"courierId\":null,\"vendorId\":\"7ddf1a10-8dfa-11ee-b9d1-0242ac120002\",\"status\":\"PENDING\",\"estimatedDeliveryTime\":null,\"estimatedPreparationFinishTime\":null,\"currentLocation\":null}")));
    }

    @Test
    void getDeliveryTest() throws Exception {
        UUID deliveryUUID = UUID.randomUUID();
        when(deliveryRepository.findById(deliveryUUID)).thenReturn(Optional.of(new Delivery()));

        this.mockMvc.perform(get("/delivery/" + deliveryUUID))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"id\":null,\"orderId\":null,\"courierId\":null,\"vendorId\":null,\"status\":null,\"estimatedDeliveryTime\":null,\"estimatedPreparationFinishTime\":null,\"currentLocation\":null}")));

    }

    @Test
    void updateStatus() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Customer customer = new Customer(customerId, "a", new Location(), ".", "");

        when(customerService.getCustomer(customerId.toString())).thenReturn(customer);

        Vendor vendor = new VendorBuilder()
                .setId(userId)
                .create();
        Order order = new Order(orderId, vendor,customer);

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setVendorId(vendor.getId())
                .setStatus(Delivery.StatusEnum.PENDING)
                .setOrderId(orderId)
                .create();



        when(orderService.findOrderById(orderId)).thenReturn(order);

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(vendorService.getVendor(vendor.getId().toString())).thenReturn(vendor);
        when(orderService.isPaid(id)).thenReturn(true);
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();

        deliveryIdStatusPutRequest.setStatus(DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED);
        deliveryIdStatusPutRequest.setUserId(userId);

        String requestJson = ow.writeValueAsString(deliveryIdStatusPutRequest);

        this.mockMvc.perform(put("/delivery/" + id + "/status").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"status\":\"ACCEPTED\"")));
    }

    @Test
    void updateDeliveryTime() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();

        Customer customer = new Customer(customerId, "a", new Location(), ".", "");

        when(customerService.getCustomer(customerId.toString())).thenReturn(customer);

        Vendor vendor = new VendorBuilder()
                .setId(userId)
                .create();
        Order order = new Order(orderId, vendor,customer);

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setVendorId(vendor.getId())
                .setStatus(Delivery.StatusEnum.PENDING)
                .setOrderId(orderId)
                .create();

        Courier courier = new Courier(courierId, vendor);
        when(courierService.getCourier(courierId.toString())).thenReturn(courier);



        when(orderService.findOrderById(orderId)).thenReturn(order);

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(vendorService.getVendor(vendor.getId().toString())).thenReturn(vendor);
        when(orderService.isPaid(id)).thenReturn(true);
        DeliveryIdAssignPutRequest deliveryIdAssignPutRequest = new DeliveryIdAssignPutRequest();

        deliveryIdAssignPutRequest.setCourierId(courierId);



        String requestJson = ow.writeValueAsString(deliveryIdAssignPutRequest);
        this.mockMvc.perform(put("/delivery/" + id + "/assign").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"status\":\"PENDING\"")));
    }

    @Test
    public void getAvailable() throws Exception {
        UUID courierId = UUID.randomUUID();

        when(courierService.getCourier(courierId.toString())).thenReturn(new Courier(courierId,null));

        List<Delivery> allDeliveries = new ArrayList<>();



        UUID delivery2vendorId = UUID.randomUUID();
        UUID delivery3vendorId = UUID.randomUUID();

        DeliveryCurrentLocation delivery2location = new  DeliveryCurrentLocation();
        delivery2location.setLatitude(BigDecimal.ONE);
        delivery2location.setLongitude(BigDecimal.ONE);

        DeliveryCurrentLocation delivery4location = new  DeliveryCurrentLocation();
        delivery4location.setLatitude(BigDecimal.ZERO);
        delivery4location.setLongitude(BigDecimal.ZERO);

        DeliveryCurrentLocation delivery5location = new  DeliveryCurrentLocation();
        delivery5location.setLatitude(BigDecimal.valueOf(100));
        delivery5location.setLongitude(BigDecimal.valueOf(100));

        Vendor delivery2vendor = new Vendor(delivery2vendorId, new Location(), new String(), false, BigDecimal.TEN);

        Vendor delivery3vendor = new Vendor(delivery3vendorId, new Location(), new String(), true, BigDecimal.TEN);


        Delivery delivery1 = new Delivery();
        delivery1.setCourierId(UUID.randomUUID());

        Delivery delivery2 = new Delivery();
        delivery2.setVendorId(delivery2vendorId);
        when(vendorService.getVendor(delivery2vendorId.toString())).thenReturn(delivery2vendor);
        delivery2.setCurrentLocation(delivery2location);

        Delivery delivery3 = new Delivery();
        delivery3.setVendorId(delivery3vendorId);
        when(vendorService.getVendor(delivery3vendorId.toString())).thenReturn(delivery3vendor);
        delivery3.setCurrentLocation(delivery2location);

        Delivery delivery4 = new Delivery();
        delivery4.setVendorId(delivery2vendorId);
        delivery4.setCurrentLocation(delivery4location);

        Delivery delivery5 = new Delivery();
        delivery5.setVendorId(delivery2vendorId);
        delivery5.setCurrentLocation(delivery5location);


        allDeliveries.add(delivery1);
        allDeliveries.add(delivery2);
        allDeliveries.add(delivery3);
        allDeliveries.add(delivery4);
        allDeliveries.add(delivery5);


        when(deliveryRepository.findAll()).thenReturn(allDeliveries);

        Location courierLocation = new Location();
        courierLocation.setLongitude(delivery4location.getLongitude());
        courierLocation.setLatitude(delivery4location.getLatitude());


        this.mockMvc.perform(get("/delivery/available")
                        .param("radius", String.valueOf(BigDecimal.valueOf(200)))
                        .param("location", ow.writeValueAsString(courierLocation))
                        .param("courierId", String.valueOf(courierId)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"id\":null,\"")));
    }

    @Test
    public void setMaxZoneVendor() throws Exception {
        UUID vendorId = UUID.randomUUID();
        DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest = new DeliveryVendorIdMaxZonePutRequest();

        deliveryVendorIdMaxZonePutRequest.setVendorId(vendorId);
        deliveryVendorIdMaxZonePutRequest.setRadiusKm(BigDecimal.valueOf(5));

        Vendor vendor = new VendorBuilder()
                .setAllowsOnlyOwnCouriers(true)
                .setMaxDeliveryZoneKm(BigDecimal.valueOf(2))
                .create();

        when(vendorService.getVendor(vendorId.toString())).thenReturn(vendor);
        when(vendorService.putVendor(vendor)).thenReturn(true);

        String requestJson = ow.writeValueAsString(deliveryVendorIdMaxZonePutRequest);
        this.mockMvc.perform(put("/delivery/vendor/" + vendorId + "/max-zone").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("radiusKm\":5")));
    }

    @Test
    public void setMaxZoneAdmin() throws Exception {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        when(adminValidatorService.validate(adminId)).thenReturn(true);
        when(vendorService.getDefaultMaxDeliveryZone()).thenReturn(BigDecimal.valueOf(20));

        AdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new  AdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        String requestJson = ow.writeValueAsString(deliveryAdminMaxZoneGet200Response);
        this.mockMvc.perform(put("/admin/max-zone").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("radiusKm\":20")));


    }
}
