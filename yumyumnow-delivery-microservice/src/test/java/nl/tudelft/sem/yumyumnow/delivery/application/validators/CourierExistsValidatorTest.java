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

class CourierExistsValidatorTest {

    private CourierService courierService;
    private CourierExistsValidator courierExistsValidator;

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
        courierExistsValidator = new CourierExistsValidator(null, courierId, courierService);

        assertTrue(courierExistsValidator.process(delivery));
    }

    @Test
    void processFailed() {
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setCourierId(courierId)
                .create();

        when(courierService.getCourier(String.valueOf(courierId))).thenReturn(null);
        courierExistsValidator = new CourierExistsValidator(null, courierId, courierService);

        assertFalse(courierExistsValidator.process(delivery));
    }

    @Test
    void processChain() {
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
        CourierExistsValidator courierExistsValidator2 = new CourierExistsValidator(null, courierId, courierService);
        courierExistsValidator = new CourierExistsValidator(courierExistsValidator2, courierId, courierService);

        assertTrue(courierExistsValidator.process(delivery));
    }
}