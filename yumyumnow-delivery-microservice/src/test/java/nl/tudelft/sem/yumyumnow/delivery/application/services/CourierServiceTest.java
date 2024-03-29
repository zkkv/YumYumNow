package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CourierBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CourierServiceTest {
    private CourierService courierService;
    private RestTemplate restTemplate;
    private VendorService vendorService;

    private final String testWebsite = "test://website";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        vendorService = mock(VendorService.class);

        courierService = new CourierService(
                restTemplate,
                testWebsite,
                vendorService
        );
    }

    @Test
    public void testGetCourierSuccess() {
        UUID courierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();

        Courier expectedCourier = new CourierBuilder()
                .setId(courierId)
                .setVendor(vendor)
                .create();

        when(restTemplate.getForObject(
                testWebsite + "/courier/" + courierId.toString(),
                Map.class
        )).thenReturn(Map.of(
                "userID", courierId.toString(),
                "vendor", vendorId.toString()
        ));

        when(vendorService.getVendor(vendorId.toString())).thenReturn(vendor);

        Courier gotCourier = courierService.getCourier(courierId.toString());

        assertEquals(expectedCourier, gotCourier);
        assertEquals(expectedCourier.getVendor(), vendor);
    }

    @Test
    public void testGetCourierEmptyResponse() {
        String uuid = UUID.randomUUID().toString();

        when(restTemplate.getForObject(
                testWebsite + "/courier/" + uuid, Map.class
        )).thenReturn(null);

        assertNull(courierService.getCourier(uuid));
    }

    @Test
    public void testGetCourierNullVendor() {
        UUID courierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        Courier expectedCourier = new CourierBuilder()
                .setId(courierId)
                .create();

        when(restTemplate.getForObject(
                testWebsite + "/courier/" + courierId.toString(),
                Map.class
        )).thenReturn(Map.of(
                "userID", courierId.toString(),
                "vendor", vendorId.toString()
        ));

        when(vendorService.getVendor(vendorId.toString())).thenReturn(null);

        Courier gotCourier = courierService.getCourier(courierId.toString());

        assertEquals(expectedCourier, gotCourier);
        assertNull(gotCourier.getVendor());
    }

    @Test
    public void testPutCourierSuccess() {
        UUID courierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();

        Courier courier = new CourierBuilder()
                .setId(courierId)
                .setVendor(vendor)
                .create();

        Map<String, String> expectedMap = Map.of(
                "userID", courierId.toString(),
                "vendor", vendorId.toString()
        );

        Map<String, String> originalMap = new HashMap<>(Map.of(
                "userID", courierId.toString(),
                "vendor", UUID.randomUUID().toString()
        ));

        when(restTemplate.getForObject(
                testWebsite + "/courier/" + courierId,
                Map.class
        )).thenReturn(originalMap);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(HttpStatus.OK);

        when(restTemplate.exchange(
                eq(testWebsite + "/courier/" + courierId),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        when(vendorService.getVendor(vendorId.toString())).thenReturn(vendor);

        assertTrue(courierService.putCourier(courier));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(originalMap, headers);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.PUT), eq(requestEntity), eq(String.class));
    }

    @Test
    public void testPutCourierFailure() {
        UUID courierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();

        Courier courier = new CourierBuilder()
                .setId(courierId)
                .setVendor(vendor)
                .create();

        Map<String, String> originalMap = new HashMap<>(Map.of(
                "userID", courierId.toString(),
                "vendor", UUID.randomUUID().toString()
        ));

        when(restTemplate.getForObject(
                testWebsite + "/courier/" + courierId,
                Map.class
        )).thenReturn(originalMap);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                eq(testWebsite + "/courier/" + courierId),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        when(vendorService.getVendor(vendorId.toString())).thenReturn(vendor);

        assertFalse(courierService.putCourier(courier));
    }
}
