package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.CourierService;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CourierBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.DeliveryBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourierBelongsToDeliveryValidatorTest {

    private CourierService courierService;

    private CourierBelongsToDeliveryValidator courierBelongsToDeliveryValidator;

    @BeforeEach
    void setUp(){
        courierService = mock(CourierService.class);
    }

    @Test
    void process() {
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setCourierId(courierId)
                .create();

        Courier courier = new CourierBuilder()
                .setId(courierId)
                .create();


        when(courierService.getCourier(String.valueOf(courierId))).thenReturn(courier);
        courierBelongsToDeliveryValidator = new CourierBelongsToDeliveryValidator(null, courierId, courierService);

        assertTrue(courierBelongsToDeliveryValidator.process(delivery));
    }

    @Test
    void processChain(){
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setCourierId(courierId)
                .create();

        Courier courier = new CourierBuilder()
                .setId(courierId)
                .create();

        when(courierService.getCourier(String.valueOf(courierId))).thenReturn(courier);
        
        CourierBelongsToDeliveryValidator courierBelongsToDeliveryValidator2 = new CourierBelongsToDeliveryValidator(null, courierId, courierService);;
        courierBelongsToDeliveryValidator = new CourierBelongsToDeliveryValidator(courierBelongsToDeliveryValidator2, courierId, courierService);

        assertTrue(courierBelongsToDeliveryValidator.process(delivery));
    }

    @Test
    void processFailed() {
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();
        UUID courierId2 = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setCourierId(courierId2)
                .create();

        Courier courier = new CourierBuilder()
                .setId(courierId)
                .create();

        when(courierService.getCourier(String.valueOf(courierId))).thenReturn(courier);
        courierBelongsToDeliveryValidator = new CourierBelongsToDeliveryValidator(null, courierId, courierService);

        assertFalse(courierBelongsToDeliveryValidator.process(delivery));
    }
}