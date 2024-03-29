package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryVendorIdCustomCouriersPutRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class VendorServiceTest {

    private VendorService vendorService;
    private RestTemplate restTemplate;
    private final String testWebsite = "test://website";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        vendorService = new VendorService(
                restTemplate,
                testWebsite
        );
    }

    @Test
    public void testGetVendorSuccess() {
        UUID vendorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(restTemplate.getForObject(
                testWebsite + "/vendor/" + vendorId,
                Map.class
        )).thenReturn(Map.of(
                "userID", userId.toString(),
                "location", Map.of(
                        "latitude", 0,
                        "longitude", 0
                ),
                "contactInfo", Map.of(
                        "phoneNumber", "123456789"
                        ),
                "allowOnlyOwnCouriers", true,
                "maxDeliveryZone", 5
        ));

        Location address = new Location();
        OffsetDateTime timestamp = OffsetDateTime.now();
        address.setTimestamp(timestamp);
        address.setLatitude(BigDecimal.valueOf(0));
        address.setLongitude(BigDecimal.valueOf(0));

        Vendor expectedVendor = new VendorBuilder()
                .setId(userId)
                .setAllowsOnlyOwnCouriers(true)
                .setMaxDeliveryZoneKm(BigDecimal.valueOf(5))
                .setPhoneNumber("123456789")
                .setAddress(address)
                .create();


        expectedVendor.setAddress(address);

        Vendor gotVendor = vendorService.getVendor(vendorId.toString());

        assertEquals(expectedVendor, gotVendor);
        assertEquals(gotVendor.getAddress().getLatitude(), address.getLatitude());
        assertEquals(gotVendor.getAddress().getLongitude(), address.getLongitude());
        assertTrue(ChronoUnit.SECONDS.between(timestamp, gotVendor.getAddress().getTimestamp()) < 10);
        assertEquals(gotVendor.getPhone(), "123456789");
        assertEquals(gotVendor.getAllowsOnlyOwnCouriers(), true);
        assertEquals(gotVendor.getMaxDeliveryZoneKm(), BigDecimal.valueOf(5));
    }

    @Test
    public void testGetVendorUseDefaultMazZone() {
        UUID vendorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(restTemplate.getForObject(
                testWebsite + "/vendor/" + vendorId,
                Map.class
        )).thenReturn(Map.of(
                "userID", userId.toString(),
                "location", Map.of(
                        "latitude", 0,
                        "longitude", 0
                ),
                "contactInfo", Map.of(
                        "phoneNumber", "123456789"
                ),
                "allowOnlyOwnCouriers", false,
                "maxDeliveryZone", 5
        ));

        Location address = new Location();
        OffsetDateTime timestamp = OffsetDateTime.now();
        address.setTimestamp(timestamp);
        address.setLatitude(BigDecimal.valueOf(0));
        address.setLongitude(BigDecimal.valueOf(0));

        vendorService.setDefaultMaxDeliveryZone(BigDecimal.valueOf(10));

        Vendor expectedVendor = new VendorBuilder()
                .setId(userId)
                .setAllowsOnlyOwnCouriers(false)
                .setMaxDeliveryZoneKm(BigDecimal.valueOf(5))
                .setPhoneNumber("123456789")
                .setAddress(address)
                .create();

        expectedVendor.setAddress(address);

        Vendor gotVendor = vendorService.getVendor(vendorId.toString());

        assertEquals(expectedVendor, gotVendor);
        assertEquals(gotVendor.getAddress().getLatitude(), address.getLatitude());
        assertEquals(gotVendor.getAddress().getLongitude(), address.getLongitude());
        assertTrue(ChronoUnit.SECONDS.between(timestamp, gotVendor.getAddress().getTimestamp()) < 10);
        assertEquals(gotVendor.getPhone(), "123456789");
        assertEquals(gotVendor.getAllowsOnlyOwnCouriers(), false);
        assertEquals(gotVendor.getMaxDeliveryZoneKm(), BigDecimal.valueOf(10));
    }

    @Test
    public void testGetVendorEmptyResponse() {
        String uuid = UUID.randomUUID().toString();

        when(restTemplate.getForObject(
                testWebsite + "/vendor/" + uuid, Map.class
        )).thenReturn(null);

        assertNull(vendorService.getVendor(uuid));
    }

    @Test
    public void testPutVendorSuccess() {
        UUID vendorId = UUID.randomUUID();

        Location address = new Location();
        address.setTimestamp(null);
        address.setLatitude(BigDecimal.valueOf(0));
        address.setLongitude(BigDecimal.valueOf(0));

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .setAllowsOnlyOwnCouriers(false)
                .setMaxDeliveryZoneKm(BigDecimal.ZERO)
                .setPhoneNumber("123456789")
                .setAddress(address)
                .create();


        Map<String, Object> originalMap = new HashMap<>(Map.of(
                "userID", vendorId.toString(),
                "location", Map.of(
                        "latitude", 0.0,
                        "longitude", 0.0
                ),
                "contactInfo", Map.of(
                        "phoneNumber", "123456789"
                ),
                "allowsOnlyOwnCouriers", false,
                "maxDeliveryZone", 0.0
        ));

        when(restTemplate.getForObject(
                testWebsite + "/vendor/" + vendorId,
                Map.class
        )).thenReturn(originalMap);

        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.exchange(
                eq(testWebsite + "/vendor/" + vendorId),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        assertTrue(vendorService.putVendor(vendor));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(originalMap, headers);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.PUT), eq(requestEntity), eq(String.class));
    }

    @Test
    public void testPutVendorFailure() {
        UUID vendorId = UUID.randomUUID();

        Location address = new Location();
        address.setTimestamp(null);
        address.setLatitude(BigDecimal.valueOf(0));
        address.setLongitude(BigDecimal.valueOf(0));

        Vendor vendor = new VendorBuilder()
            .setId(vendorId)
            .setAllowsOnlyOwnCouriers(false)
            .setMaxDeliveryZoneKm(BigDecimal.ZERO)
            .setPhoneNumber("123456789")
            .setAddress(address)
            .create();


        Map<String, Object> originalMap = new HashMap<>(Map.of(
                "userID", vendorId.toString(),
                "location", Map.of(
                        "latitude", 0.0,
                        "longitude", 0.0
                ),
                "contactInfo", Map.of(
                        "phoneNumber", "123456789"
                ),
                "allowsOnlyOwnCouriers", false,
                "maxDeliveryZone", 0.0
        ));

        when(restTemplate.getForObject(
                testWebsite + "/vendor/" + vendorId,
                Map.class
        )).thenReturn(originalMap);

        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                eq(testWebsite + "/vendor/" + vendorId),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        assertFalse(vendorService.putVendor(vendor));
    }

    @Test
    public void getDefaultMaxDeliveryZone() {
        vendorService.setDefaultMaxDeliveryZone(BigDecimal.valueOf(20));
        BigDecimal maxZone = vendorService.getDefaultMaxDeliveryZone();

        assertEquals(BigDecimal.valueOf(20), maxZone);
    }

    @Test
    public void setOwnCouriersFail(){
        assertThrows(BadArgumentException.class, () -> vendorService.setOwnCouriers(UUID.randomUUID(), true));
    }

    @Test
    public void setOwnCourier() throws BadArgumentException {
        UUID vendorId = UUID.randomUUID();

        Location address = new Location();
        address.setTimestamp(null);
        address.setLatitude(BigDecimal.valueOf(0));
        address.setLongitude(BigDecimal.valueOf(0));

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .setAllowsOnlyOwnCouriers(false)
                .setMaxDeliveryZoneKm(BigDecimal.ZERO)
                .setPhoneNumber("123456789")
                .setAddress(address)
                .create();


        Map<String, Object> originalMap = new HashMap<>(Map.of(
                "userID", vendorId.toString(),
                "location", Map.of(
                        "latitude", 0.0,
                        "longitude", 0.0
                ),
                "contactInfo", Map.of(
                        "phoneNumber", "123456789"
                ),
                "allowOnlyOwnCouriers", false,
                "maxDeliveryZone", 0.0
        ));

        when(restTemplate.getForObject(
                testWebsite + "/vendor/" + vendorId,
                Map.class
        )).thenReturn(originalMap);

        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.exchange(
                eq(testWebsite + "/vendor/" + vendorId),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);


        DeliveryVendorIdCustomCouriersPutRequest expected = new DeliveryVendorIdCustomCouriersPutRequest();
        expected.setVendorId(vendorId);
        expected.setAllowsOnlyOwnCouriers(true);

        assertEquals(expected, vendorService.setOwnCouriers(vendorId,true));
    }

}
