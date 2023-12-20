package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.VendorCustomizer;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.VendorCustomizerRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
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

        deliveryService = new DeliveryService(
                deliveryRepository,
                vendorCustomizerRepository);
    }

    @Test
    public void setStatusToAcceptedAsNonVendor(){
        assertNull(deliveryService.updateStatus(
                UUID.randomUUID(),UUID.randomUUID(), DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED));
    }

    @Test
    public void setStatusToInTransitAsNonCourier(){
        assertNull(deliveryService.updateStatus(
                UUID.randomUUID(), UUID.randomUUID(), DeliveryIdStatusPutRequest.StatusEnum.IN_TRANSIT));
    }

    @Test
    public void setStatusToDeliveredAsNonCourier(){
        assertNull(deliveryService.updateStatus(
                UUID.randomUUID(), UUID.randomUUID(), DeliveryIdStatusPutRequest.StatusEnum.DELIVERED));
    }

    @Test
    public void setStatusToInTransitAsCourier(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new Delivery();
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);


        Delivery actual =  deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.IN_TRANSIT);
        assertEquals(expected, actual);
    }

    @Test
    public void setStatusToDeliveredAsCourier(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new Delivery();
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);


        Delivery actual =  deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.DELIVERED);
        assertEquals(expected, actual);
    }

    // TO-DO: Redo this test
//    @Test
//    public void setStatusAsStrangerCourier(){
//        UUID id = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        UUID actualCourierId = UUID.randomUUID();
//
//        Delivery delivery = new Delivery();
//        delivery.setId(id);
//        Optional<Delivery> optionalDelivery = Optional.of(delivery);
//        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
//
//
//        Delivery actual =  deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.DELIVERED);
//        assertNull(actual);
//    }

    @Test
    public void changePrepTimeAsNonVendor(){
        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);
        assertNull(deliveryService.changePrepTime(UUID.randomUUID(), UUID.randomUUID(), offsetDateTime));
    }

    @Test
    public void changePrepTimeOnNonExistingOrder(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(vendorCustomizerRepository.findById(userId)).thenReturn(Optional.of(new VendorCustomizer()));

        when(deliveryRepository.findById(id)).thenReturn(Optional.empty());

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        assertNull(deliveryService.changePrepTime(id,userId,offsetDateTime));
    }

    @Test
    public void changePrepTimeAsVendorNonAccepted(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(vendorCustomizerRepository.findById(userId)).thenReturn(Optional.of(new VendorCustomizer()));

        Delivery delivery = new Delivery();
        delivery.setId(id);;

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        delivery.setEstimatedPreparationFinishTime(offsetDateTime);
        assertNull(deliveryService.changePrepTime(id,userId,offsetDateTime));
    }

    @Test
    public void changePrepTimeAsVendor(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(vendorCustomizerRepository.findById(userId)).thenReturn(Optional.of(new VendorCustomizer()));

        Delivery delivery = new Delivery();
        delivery.setId(id);;
        delivery.setStatus(Delivery.StatusEnum.ACCEPTED);
        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        delivery.setEstimatedPreparationFinishTime(offsetDateTime);
        assertEquals(delivery ,deliveryService.changePrepTime(id,userId,offsetDateTime));
        assertEquals(delivery.getEstimatedPreparationFinishTime() ,offsetDateTime);
    }

}