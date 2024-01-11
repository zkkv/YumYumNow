package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.OrderBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    private OrderService orderService;
    private RestTemplate restTemplate;
    private CustomerService customerService;
    private VendorService vendorService;

    private final String testWebsite = "test://website";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        customerService = mock(CustomerService.class);
        vendorService = mock(VendorService.class);

        orderService = new OrderService(
                restTemplate,
                testWebsite,
                customerService,
                vendorService
        );
    }

    @Test
    public void testFindOrderByIdSuccess() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(customerId);

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .createVendor();

        Order expectedOrder = new OrderBuilder()
                .setOrderId(orderId)
                .setOrderCustomer(customer)
                .setOrderVendor(vendor)
                .createOrder();


        when(restTemplate.getForObject(
                testWebsite + "/order/" + orderId.toString(),
                Map.class
        )).thenReturn(Map.of(
                "orderID", orderId.toString(),
                "customerID", UUID.randomUUID().toString(),
                "vendorID", UUID.randomUUID().toString()
        ));

        when(customerService.getCustomer(customerId.toString()
        )).thenReturn(customer);

        when(vendorService.getVendor(vendorId.toString()
        )).thenReturn(vendor);

        Order gotOrder = orderService.findOrderById(orderId);

        assertEquals(expectedOrder, gotOrder);
        assertEquals(expectedOrder.getCustomer(), customer);
        assertEquals(expectedOrder.getVendor(), vendor);
    }

    @Test
    public void testFindOrderByIdEmptyResponse() {
        UUID orderId = UUID.randomUUID();

        when(restTemplate.getForObject(
                testWebsite + "/order/" + orderId.toString(),
                Map.class
        )).thenReturn(null);

        assertNull(orderService.findOrderById(orderId));
    }

    @Test
    public void testIsPaidSuccess() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<Boolean> response = new ResponseEntity<>(true, HttpStatus.ACCEPTED);

        when(restTemplate.getForEntity(
                testWebsite + "/order/" + orderId.toString() + "/isPaid",
                Boolean.class
        )).thenReturn(response);

        assertTrue(orderService.isPaid(orderId));
    }

    @Test
    public void testIsPaidFalse() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<Boolean> response = new ResponseEntity<>(false, HttpStatus.ACCEPTED);

        when(restTemplate.getForEntity(
                testWebsite + "/order/" + orderId.toString() + "/isPaid",
                Boolean.class
        )).thenReturn(response);

        assertFalse(orderService.isPaid(orderId));
    }

    @Test
    public void testChangePaidStatus() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);

        orderService.changePaidStatus(orderId);

        verify(restTemplate, times(1)).put(
                testWebsite + "/order/" + orderId.toString() + "/isPaid",
                null
        );
    }

    @Test
    public void testGetStatusSuccess() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<String> response = new ResponseEntity<>("test", HttpStatus.ACCEPTED);

        when(restTemplate.getForEntity(
                testWebsite + "/order/" + orderId.toString() + "/status",
                String.class
        )).thenReturn(response);

        assertEquals("test", orderService.getStatus(orderId));
    }

    @Test
    public void testGetStatusEmptyResponse() {
        UUID orderId = UUID.randomUUID();

        orderService.updateStatus(orderId, "test");

        verify(restTemplate, times(1)).put(
                eq(testWebsite + "/order/" + orderId + "/status"),
                any(HttpEntity.class)
        );
    }
}
