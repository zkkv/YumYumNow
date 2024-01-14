package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.DeliveryBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VendorBelongsToDeliveryValidatorTest {

    private VendorService vendorService;
    private VendorBelongsToDeliveryValidator vendorBelongsToDeliveryValidator;

    @BeforeEach
    void setUp(){
        vendorService = mock(VendorService.class);
    }

    @Test
    void process() {
        UUID vendorId = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setVendorId(vendorId)
                .create();

        when(vendorService.getVendor(String.valueOf(vendorId))).thenReturn(vendor);
        vendorBelongsToDeliveryValidator = new VendorBelongsToDeliveryValidator(null, vendorId, vendorService);

        assertTrue(vendorBelongsToDeliveryValidator.process(delivery));
    }

    @Test
    void processFailed() {
        UUID vendorId = UUID.randomUUID();
        UUID vendorId2 = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setVendorId(vendorId2)
                .create();

        when(vendorService.getVendor(String.valueOf(vendorId))).thenReturn(vendor);
        vendorBelongsToDeliveryValidator = new VendorBelongsToDeliveryValidator(null, vendorId, vendorService);

        assertFalse(vendorBelongsToDeliveryValidator.process(delivery));
    }

    @Test
    void processChain() {
        UUID vendorId = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setVendorId(vendorId)
                .create();

        when(vendorService.getVendor(String.valueOf(vendorId))).thenReturn(vendor);
        VendorBelongsToDeliveryValidator vendorBelongsToDeliveryValidator2 = new VendorBelongsToDeliveryValidator(null, vendorId, vendorService);
        vendorBelongsToDeliveryValidator = new VendorBelongsToDeliveryValidator(vendorBelongsToDeliveryValidator2, vendorId, vendorService);

        assertTrue(vendorBelongsToDeliveryValidator.process(delivery));
    }
}