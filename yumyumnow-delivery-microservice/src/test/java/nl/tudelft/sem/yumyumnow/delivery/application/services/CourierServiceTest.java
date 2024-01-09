package nl.tudelft.sem.yumyumnow.delivery.application.services;

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

        Vendor vendor = new Vendor();
        vendor.setId(vendorId);

        Courier expectedCourier = new Courier();
        expectedCourier.setId(courierId);
        expectedCourier.setVendor(vendor);

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

        Courier expectedCourier = new Courier();
        expectedCourier.setId(courierId);
        expectedCourier.setVendor(null);

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

        Vendor vendor = new Vendor();
        vendor.setId(vendorId);


        Courier courier = new Courier();
        courier.setId(courierId);
        courier.setVendor(vendor);

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
    }

    @Test
    public void testPutCourierFailure() {
        UUID courierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        Vendor vendor = new Vendor();
        vendor.setId(vendorId);

        Courier courier = new Courier();
        courier.setId(courierId);
        courier.setVendor(vendor);

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
