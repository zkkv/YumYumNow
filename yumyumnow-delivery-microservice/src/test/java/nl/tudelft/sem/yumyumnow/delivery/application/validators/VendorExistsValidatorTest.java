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

class VendorExistsValidatorTest {

    private VendorService vendorService;
    private VendorExistsValidator vendorExistsValidator;

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
        vendorExistsValidator = new VendorExistsValidator(null, vendorId, vendorService);

        assertTrue(vendorExistsValidator.process(delivery));
    }

    @Test
    void processFailed() {
        UUID vendorId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setVendorId(vendorId)
                .create();

        when(vendorService.getVendor(String.valueOf(vendorId))).thenReturn(null);
        vendorExistsValidator = new VendorExistsValidator(null, vendorId, vendorService);

        assertFalse(vendorExistsValidator.process(delivery));
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
        VendorExistsValidator vendorExistsValidator2 = new VendorExistsValidator(null, vendorId, vendorService);
        vendorExistsValidator = new VendorExistsValidator(vendorExistsValidator2, vendorId, vendorService);

        assertTrue(vendorExistsValidator.process(delivery));
    }
}