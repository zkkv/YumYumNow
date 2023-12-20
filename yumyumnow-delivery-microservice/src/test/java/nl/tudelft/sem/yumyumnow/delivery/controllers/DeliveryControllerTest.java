package nl.tudelft.sem.yumyumnow.delivery.controllers;

import nl.tudelft.sem.yumyumnow.delivery.application.services.DeliveryService;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdDeliveryTimePostRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdDeliveryTimePostRequest1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryControllerTest {

    private DeliveryService deliveryService;

    private DeliveryController deliveryController;


    @BeforeEach
    void setUp(){
        this.deliveryService = mock(DeliveryService.class);
        this.deliveryController = new DeliveryController(deliveryService);
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


        when(deliveryService.changePrepTime(id,deliveryIdDeliveryTimePostRequest.getUserId(), deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime())).thenReturn(null);

        ResponseEntity<Delivery> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdPrepTimePost(id, deliveryIdDeliveryTimePostRequest);

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

        when(deliveryService.changePrepTime(id,deliveryIdDeliveryTimePostRequest.getUserId(), deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime()))
                .thenReturn(delivery);

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdPrepTimePost(id, deliveryIdDeliveryTimePostRequest);

        assertEquals(expected, actual);
    }
}