package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.StatusEnum;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.VendorCustomizerRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


public class DeliveryServiceTest {
    private DeliveryRepository deliveryRepository;

    private VendorCustomizerRepository vendorCustomizerRepository;

    private DeliveryService deliveryService;

    @BeforeEach
    void setUp(){
        this.deliveryRepository = mock(DeliveryRepository.class);
        this.vendorCustomizerRepository = mock(VendorCustomizerRepository.class);

        deliveryService = new DeliveryService(deliveryRepository, vendorCustomizerRepository);
    }

    @Test
    public void updateStatusUnauthorized(){
        assertNull(deliveryService.updateStatus(UUID.fromString("1"),UUID.fromString("1"), StatusEnum.ACCEPTED));
    }
}