package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.CourierService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CourierBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CustomerBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.DeliveryBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatusPermissionValidatorTest {

    private VendorService vendorService;
    private CourierService courierService;
    private StatusPermissionValidator statusPermissionValidator;

    @BeforeEach
    void setUp(){
        vendorService = mock(VendorService.class);
        courierService = mock(CourierService.class);
    }

    @Test
    void processVendor() {
        UUID vendorId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum statusEnum = DeliveryIdStatusPutRequest.StatusEnum.GIVEN_TO_COURIER;

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setVendorId(vendorId)
                .create();

        when(vendorService.getVendor(String.valueOf(vendorId))).thenReturn(vendor);
        when(courierService.getCourier(String.valueOf(vendorId))).thenReturn(null);

        VendorExistsValidator vendorExistsValidator = mock(VendorExistsValidator.class);
        CourierExistsValidator courierExistsValidator = mock(CourierExistsValidator.class);

        when(vendorExistsValidator.process(delivery)).thenReturn(true);
        when(courierExistsValidator.process(delivery)).thenReturn(false);

        statusPermissionValidator = new StatusPermissionValidator(
                Map.of(
                        Vendor.class, vendorExistsValidator,
                        Courier.class, courierExistsValidator
                ), statusEnum, vendorId, vendorService, courierService);

        assertTrue(statusPermissionValidator.process(delivery));
    }

    @Test
    void processVendorFailed() {
        UUID vendorId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum statusEnum = DeliveryIdStatusPutRequest.StatusEnum.PENDING;

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setVendorId(vendorId)
                .create();

        when(vendorService.getVendor(String.valueOf(vendorId))).thenReturn(vendor);
        when(courierService.getCourier(String.valueOf(vendorId))).thenReturn(null);

        VendorExistsValidator vendorExistsValidator = mock(VendorExistsValidator.class);
        CourierExistsValidator courierExistsValidator = mock(CourierExistsValidator.class);

        when(vendorExistsValidator.process(delivery)).thenReturn(true);
        when(courierExistsValidator.process(delivery)).thenReturn(false);

        statusPermissionValidator = new StatusPermissionValidator(
                Map.of(
                        Vendor.class, vendorExistsValidator,
                        Courier.class, courierExistsValidator
                ), statusEnum, vendorId, vendorService, courierService);

        assertFalse(statusPermissionValidator.process(delivery));
    }

    @Test
    void processCourier() {
        UUID courierId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum statusEnum = DeliveryIdStatusPutRequest.StatusEnum.IN_TRANSIT;

        Courier courier = new CourierBuilder()
                .setId(courierId)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setCourierId(courierId)
                .create();

        when(vendorService.getVendor(String.valueOf(courierId))).thenReturn(null);
        when(courierService.getCourier(String.valueOf(courierId))).thenReturn(courier);

        VendorExistsValidator vendorExistsValidator = mock(VendorExistsValidator.class);
        CourierExistsValidator courierExistsValidator = mock(CourierExistsValidator.class);

        when(vendorExistsValidator.process(delivery)).thenReturn(false);
        when(courierExistsValidator.process(delivery)).thenReturn(true);

        statusPermissionValidator = new StatusPermissionValidator(
                Map.of(
                        Vendor.class, vendorExistsValidator,
                        Courier.class, courierExistsValidator
                ), statusEnum, courierId, vendorService, courierService);

        assertTrue(statusPermissionValidator.process(delivery));
    }

    @Test
    void processCourierFailed() {
        UUID courierId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum statusEnum = DeliveryIdStatusPutRequest.StatusEnum.PENDING;

        Courier courier = new CourierBuilder()
                .setId(courierId)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setCourierId(courierId)
                .create();

        when(vendorService.getVendor(String.valueOf(courierId))).thenReturn(null);
        when(courierService.getCourier(String.valueOf(courierId))).thenReturn(courier);

        VendorExistsValidator vendorExistsValidator = mock(VendorExistsValidator.class);
        CourierExistsValidator courierExistsValidator = mock(CourierExistsValidator.class);

        when(vendorExistsValidator.process(delivery)).thenReturn(false);
        when(courierExistsValidator.process(delivery)).thenReturn(true);

        statusPermissionValidator = new StatusPermissionValidator(
                Map.of(
                        Vendor.class, vendorExistsValidator,
                        Courier.class, courierExistsValidator
                ), statusEnum, courierId, vendorService, courierService);

        assertFalse(statusPermissionValidator.process(delivery));
    }

    @Test
    void processNotVendorOrCourier() {
        UUID id = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum statusEnum = DeliveryIdStatusPutRequest.StatusEnum.PENDING;

        Delivery delivery = new DeliveryBuilder()
                .create();

        when(vendorService.getVendor(String.valueOf(id))).thenReturn(null);
        when(courierService.getCourier(String.valueOf(id))).thenReturn(null);

        VendorExistsValidator vendorExistsValidator = mock(VendorExistsValidator.class);
        CourierExistsValidator courierExistsValidator = mock(CourierExistsValidator.class);

        when(vendorExistsValidator.process(delivery)).thenReturn(false);
        when(courierExistsValidator.process(delivery)).thenReturn(false);

        statusPermissionValidator = new StatusPermissionValidator(
                Map.of(
                        Vendor.class, vendorExistsValidator,
                        Courier.class, courierExistsValidator
                ), statusEnum, id, vendorService, courierService);

        assertFalse(statusPermissionValidator.process(delivery));
    }

    @Test
    void processNotChain() {
        UUID id = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum statusEnum = DeliveryIdStatusPutRequest.StatusEnum.IN_TRANSIT;

        Courier courier = new CourierBuilder()
                .setId(id)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setCourierId(id)
                .create();

        when(vendorService.getVendor(String.valueOf(id))).thenReturn(null);
        when(courierService.getCourier(String.valueOf(id))).thenReturn(courier);

        statusPermissionValidator = new StatusPermissionValidator(
                null, statusEnum, id, vendorService, courierService);

        assertTrue(statusPermissionValidator.process(delivery));
    }
}