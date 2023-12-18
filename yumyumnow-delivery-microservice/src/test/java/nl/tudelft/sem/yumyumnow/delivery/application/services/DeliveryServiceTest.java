package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.StatusEnum;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.CourierRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.VendorCustomizerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


public class DeliveryServiceTest {
    private DeliveryRepository deliveryRepository;

    private VendorCustomizerRepository vendorCustomizerRepository;

    private CourierRepository courierRepository;

    private DeliveryService deliveryService;

    @BeforeEach
    void setUp(){
        this.deliveryRepository = mock(DeliveryRepository.class);
        this.vendorCustomizerRepository = mock(VendorCustomizerRepository.class);
        this.courierRepository = mock(CourierRepository.class);

        deliveryService = new DeliveryService(deliveryRepository, vendorCustomizerRepository, courierRepository);
    }

    @Test
    public void updateStatusUnauthorized(){
        assertNull(deliveryService.updateStatus(UUID.fromString("1"),UUID.fromString("1"), StatusEnum.ACCEPTED));
    }

    @Test
    public void setStatusToInTransitAsNonCourier(){
        assertNull(deliveryService.updateStatus(UUID.fromString("1"), UUID.fromString("1"), StatusEnum.IN_TRANSIT));
    }

    @Test
    public void setStatusToDeliveredAsNonCourier(){
        assertNull(deliveryService.updateStatus(UUID.fromString("1"), UUID.fromString("1"), StatusEnum.DELIVERED));
    }

    @Test
    public void setStatusToInTransitAsCourier(){
        UUID id = UUID.fromString("1");
        UUID userId = UUID.fromString("1");

        Delivery expected = new Delivery(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        Courier courier = new Courier(userId);
        Optional<Courier> optionalCourier = Optional.of(courier);
        when(courierRepository.findById(userId)).thenReturn(optionalCourier);

        Delivery actual =  deliveryService.updateStatus(id, userId, StatusEnum.IN_TRANSIT);
        assertEquals(expected, actual);
    }

    @Test
    public void setStatusToDeliveredAsCourier(){
        UUID id = UUID.fromString("1");
        UUID userId = UUID.fromString("1");

        Delivery expected = new Delivery(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        Courier courier = new Courier(userId);
        Optional<Courier> optionalCourier = Optional.of(courier);
        when(courierRepository.findById(userId)).thenReturn(optionalCourier);

        Delivery actual =  deliveryService.updateStatus(id, userId, StatusEnum.DELIVERED);
        assertEquals(expected, actual);
    }

}