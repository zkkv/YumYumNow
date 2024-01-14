package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.CourierService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CourierBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.DeliveryBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourierBelongsToVendorValidatorTest {

    private VendorService vendorService;
    private CourierService courierService;
    private CourierBelongsToVendorValidator courierBelongsToVendorValidator;

    @BeforeEach
    void setUp(){
        vendorService = mock(VendorService.class);
        courierService = mock(CourierService.class);
    }

    @Test
    void process() {
        UUID courierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .setAllowsOnlyOwnCouriers(true)
                .create();

        Courier courier = new CourierBuilder()
                .setId(courierId)
                .setVendor(vendor)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setVendorId(vendorId)
                .create();

        when(courierService.getCourier(String.valueOf(courierId))).thenReturn(courier);
        courierBelongsToVendorValidator = new CourierBelongsToVendorValidator(null, courierId, courierService, vendorService);

        when(vendorService.getVendor(String.valueOf(vendorId))).thenReturn(vendor);
        assertTrue(courierBelongsToVendorValidator.process(delivery));
    }

    @Test
    void processCourierNotAssignedToAnyVendor() {
        UUID courierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        Courier courier = new CourierBuilder()
                .setId(courierId)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setVendorId(vendorId)
                .create();

        when(courierService.getCourier(String.valueOf(courierId))).thenReturn(courier);
        courierBelongsToVendorValidator = new CourierBelongsToVendorValidator(null, courierId, courierService, vendorService);

        when(vendorService.getVendor(String.valueOf(vendorId))).thenReturn(null);
        assertTrue(courierBelongsToVendorValidator.process(delivery));
    }

    @Test
    void processFailed() {
        UUID courierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();
        UUID vendorId2 = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .setAllowsOnlyOwnCouriers(true)
                .create();

        Vendor vendor2 = new
                VendorBuilder()
                .setId(vendorId2)
                .setAllowsOnlyOwnCouriers(true)
                .create();

        Courier courier = new CourierBuilder()
                .setId(courierId)
                .setVendor(vendor2)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setVendorId(vendorId)
                .create();

        when(courierService.getCourier(String.valueOf(courierId))).thenReturn(courier);
        courierBelongsToVendorValidator = new CourierBelongsToVendorValidator(null, courierId, courierService, vendorService);

        when(vendorService.getVendor(String.valueOf(vendorId))).thenReturn(vendor);
        assertFalse(courierBelongsToVendorValidator.process(delivery));
    }

    @Test
    void processChain() {
        UUID courierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .setAllowsOnlyOwnCouriers(true)
                .create();

        Courier courier = new CourierBuilder()
                .setId(courierId)
                .setVendor(vendor)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setVendorId(vendorId)
                .create();

        when(courierService.getCourier(String.valueOf(courierId))).thenReturn(courier);
        CourierBelongsToVendorValidator courierBelongsToVendorValidator2 = new CourierBelongsToVendorValidator(null, courierId, courierService, vendorService);
        courierBelongsToVendorValidator = new CourierBelongsToVendorValidator(courierBelongsToVendorValidator2, courierId, courierService, vendorService);

        when(vendorService.getVendor(String.valueOf(vendorId))).thenReturn(vendor);
        assertTrue(courierBelongsToVendorValidator.process(delivery));
    }
}