package nl.tudelft.sem.yumyumnow.delivery.controllers;

import nl.tudelft.sem.yumyumnow.delivery.application.services.DeliveryService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.OrderService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.UserService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdDeliveryTimePostRequest1;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryVendorIdMaxZonePutRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DeliveryControllerTest {

    private DeliveryService deliveryService;
    private DeliveryController deliveryController;
    private UserService userService;
    private VendorService vendorService;
    private OrderService orderService;

    @BeforeEach
    void setUp(){
        this.deliveryService = mock(DeliveryService.class);
        this.userService = mock(UserService.class);
        this.vendorService = mock(VendorService.class);
        this.orderService = mock(OrderService.class);
        this.deliveryController = new DeliveryController(deliveryService, userService, vendorService, orderService);
    }

    @Test
    void deliveryIdPrepTimePostFail() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest1();

        deliveryIdDeliveryTimePostRequest.setUserId(userId);

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        deliveryIdDeliveryTimePostRequest.setEstimatedNewDeliveryTime(offsetDateTime);


        when(deliveryService.changePrepTime(id, deliveryIdDeliveryTimePostRequest.getUserId(), deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime())).thenReturn(null);

        ResponseEntity<Delivery> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdPrepTimePost(id, deliveryIdDeliveryTimePostRequest);
    }

    @Test
    public void updateStatusFail() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.PENDING;
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();
        deliveryIdStatusPutRequest.setUserId(userId);
        deliveryIdStatusPutRequest.setStatus(status);

        when(deliveryService.updateStatus(id,deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus())).thenReturn(null);

        ResponseEntity<Delivery> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdStatusPut(id, deliveryIdStatusPutRequest);


        assertEquals(expected, actual);
    }

    @Test
    void deliveryIdPrepTimePutFail() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest1();

        deliveryIdDeliveryTimePostRequest.setUserId(userId);

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        deliveryIdDeliveryTimePostRequest.setEstimatedNewDeliveryTime(offsetDateTime);


        when(deliveryService.changePrepTime(id,deliveryIdDeliveryTimePostRequest.getUserId(), deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime())).thenReturn(null);

        ResponseEntity<Delivery> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdPrepTimePut(id, deliveryIdDeliveryTimePostRequest);

        assertEquals(expected, actual);
    }

    @Test
    void deliveryIdPrepTimePutSuccess() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest1();

        deliveryIdDeliveryTimePostRequest.setUserId(userId);

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        deliveryIdDeliveryTimePostRequest.setEstimatedNewDeliveryTime(offsetDateTime);


        Delivery delivery = new Delivery();
        delivery.setId(id);
        delivery.setStatus(Delivery.StatusEnum.ACCEPTED);
        delivery.setEstimatedPreparationFinishTime(offsetDateTime);

        when(deliveryService.changePrepTime(id,deliveryIdDeliveryTimePostRequest.getUserId(), deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime()))
                .thenReturn(delivery);

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdPrepTimePut(id, deliveryIdDeliveryTimePostRequest);

        assertEquals(expected, actual);
    }

    @Test
    void deliveryIdPrepTimePostSuccess() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest1();

        deliveryIdDeliveryTimePostRequest.setUserId(userId);

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        deliveryIdDeliveryTimePostRequest.setEstimatedNewDeliveryTime(offsetDateTime);


        Delivery delivery = new Delivery();
        delivery.setId(id);
        delivery.setStatus(Delivery.StatusEnum.ACCEPTED);
        delivery.setEstimatedPreparationFinishTime(offsetDateTime);

        when(deliveryService.changePrepTime(id, deliveryIdDeliveryTimePostRequest.getUserId(), deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime()))
                .thenReturn(delivery);

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdPrepTimePost(id, deliveryIdDeliveryTimePostRequest);

    }

    @Test
    void updateStatusSuccess() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.PREPARING;
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();
        deliveryIdStatusPutRequest.setUserId(userId);
        deliveryIdStatusPutRequest.setStatus(status);

        Delivery delivery = new Delivery();
        delivery.setStatus(Delivery.StatusEnum.PREPARING);
        delivery.setId(id);

        when(deliveryService.updateStatus(id,deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus())).thenReturn(delivery);

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdStatusPut(id, deliveryIdStatusPutRequest);

        assertEquals(expected, actual);
    }

    @Test
    void deliveryVendorIdMaxZonePutSuccessTest(){
        UUID vendorId = UUID.randomUUID();
        DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest = new DeliveryVendorIdMaxZonePutRequest();

        deliveryVendorIdMaxZonePutRequest.setVendorId(vendorId);
        deliveryVendorIdMaxZonePutRequest.setRadiusKm(BigDecimal.valueOf(5));

        when(deliveryService.vendorMaxZone(vendorId,deliveryVendorIdMaxZonePutRequest,vendorService)).thenReturn(deliveryVendorIdMaxZonePutRequest);

        ResponseEntity<DeliveryVendorIdMaxZonePutRequest> response = deliveryController.deliveryVendorIdMaxZonePut(vendorId, deliveryVendorIdMaxZonePutRequest);
        assertEquals(response, ResponseEntity.ok(deliveryVendorIdMaxZonePutRequest));
    }

    @Test
    void deliveryVendorIdMaxZonePutFailedTest(){
        UUID vendorId = UUID.randomUUID();
        DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest = new DeliveryVendorIdMaxZonePutRequest();

        deliveryVendorIdMaxZonePutRequest.setVendorId(vendorId);
        deliveryVendorIdMaxZonePutRequest.setRadiusKm(BigDecimal.valueOf(5));

        when(deliveryService.vendorMaxZone(vendorId,deliveryVendorIdMaxZonePutRequest,vendorService)).thenReturn(null);

        ResponseEntity<DeliveryVendorIdMaxZonePutRequest> response = deliveryController.deliveryVendorIdMaxZonePut(vendorId, deliveryVendorIdMaxZonePutRequest);
        assertEquals(response, ResponseEntity.badRequest().body(deliveryVendorIdMaxZonePutRequest));
    }
}