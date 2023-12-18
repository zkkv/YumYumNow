package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.CourierToDelivery;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.StatusEnum;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.CourierToDeliveryRepository;
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

    private CourierToDeliveryRepository courierToDeliveryRepository;

    private DeliveryService deliveryService;

    @BeforeEach
    void setUp(){
        this.deliveryRepository = mock(DeliveryRepository.class);
        this.vendorCustomizerRepository = mock(VendorCustomizerRepository.class);
        this.courierToDeliveryRepository = mock(CourierToDeliveryRepository.class);

        deliveryService = new DeliveryService(
                deliveryRepository,
                vendorCustomizerRepository,
                courierToDeliveryRepository);
    }

    @Test
    public void setStatusToAcceptedAsNonVendor(){
        assertNull(deliveryService.updateStatus(
                UUID.randomUUID(),UUID.randomUUID(), StatusEnum.ACCEPTED));
    }

    @Test
    public void setStatusToInTransitAsNonCourier(){
        assertNull(deliveryService.updateStatus(
                UUID.randomUUID(), UUID.randomUUID(), StatusEnum.IN_TRANSIT));
    }

    @Test
    public void setStatusToDeliveredAsNonCourier(){
        assertNull(deliveryService.updateStatus(
                UUID.randomUUID(), UUID.randomUUID(), StatusEnum.DELIVERED));
    }

    @Test
    public void setStatusToInTransitAsCourier(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new Delivery(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        CourierToDelivery courierToDelivery = new CourierToDelivery(userId, id);
        Optional<CourierToDelivery> optionalCourierToDelivery = Optional.of(courierToDelivery);
        CourierToDelivery.CourierToDeliveryPrimaryKey courierDeliveryPair =
                new CourierToDelivery.CourierToDeliveryPrimaryKey(userId, id);
        when(courierToDeliveryRepository.findById(courierDeliveryPair))
                .thenReturn(optionalCourierToDelivery);

        Delivery actual =  deliveryService.updateStatus(id, userId, StatusEnum.IN_TRANSIT);
        assertEquals(expected, actual);
    }

    @Test
    public void setStatusToDeliveredAsCourier(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new Delivery(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        CourierToDelivery courierToDelivery = new CourierToDelivery(userId, id);
        Optional<CourierToDelivery> optionalCourierToDelivery = Optional.of(courierToDelivery);
        CourierToDelivery.CourierToDeliveryPrimaryKey courierDeliveryPair =
                new CourierToDelivery.CourierToDeliveryPrimaryKey(userId, id);
        when(courierToDeliveryRepository.findById(courierDeliveryPair))
                .thenReturn(optionalCourierToDelivery);

        Delivery actual =  deliveryService.updateStatus(id, userId, StatusEnum.DELIVERED);
        assertEquals(expected, actual);
    }

}