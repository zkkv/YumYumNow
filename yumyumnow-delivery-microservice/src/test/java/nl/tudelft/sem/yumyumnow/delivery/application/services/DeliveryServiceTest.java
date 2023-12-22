package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.VendorCustomizer;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.VendorCustomizerRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryVendorIdMaxZonePutRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


public class DeliveryServiceTest {
    private DeliveryRepository deliveryRepository;

    private VendorCustomizerRepository vendorCustomizerRepository;


    private DeliveryService deliveryService;
    private VendorService vendorService;

    @BeforeEach
    void setUp(){
        this.deliveryRepository = mock(DeliveryRepository.class);
        this.vendorCustomizerRepository = mock(VendorCustomizerRepository.class);
        this.vendorService = mock(VendorService.class);

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
    public void setStatusToRejectedAsNonVendor(){
        assertNull(deliveryService.updateStatus(
                UUID.randomUUID(),UUID.randomUUID(), DeliveryIdStatusPutRequest.StatusEnum.REJECTED));
    }

    @Test
    public void setStatusToGivenToCourierAsNonVendor(){
        assertNull(deliveryService.updateStatus(
                UUID.randomUUID(),UUID.randomUUID(), DeliveryIdStatusPutRequest.StatusEnum.GIVEN_TO_COURIER));
    }

    @Test
    public void setStatusToPreparingAsNonVendor(){
        assertNull(deliveryService.updateStatus(
                UUID.randomUUID(),UUID.randomUUID(), DeliveryIdStatusPutRequest.StatusEnum.PREPARING));
    }

    @Test
    public void setStatusToDeliveredAsVendor(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new Delivery();
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        Delivery actual =  deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.DELIVERED);
        assertNull(actual);
    }

    @Test
    public void setStatusToAcceptedAsVendor(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new Delivery();
        expected.setId(id);
        expected.setVendorId(userId);
        expected.setStatus(Delivery.StatusEnum.PENDING);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        Delivery actual =  deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED);
        assertEquals(expected, actual);
        assertEquals(actual.getStatus().getValue(), "ACCEPTED");
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
        expected.setCourierId(userId);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        Delivery actual =  deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.IN_TRANSIT);
        assertEquals(expected, actual);
        assertEquals(actual.getStatus().getValue(), "IN_TRANSIT");
    }

    @Test
    public void setStatusToDeliveredAsCourier(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new Delivery();
        expected.setId(id);
        expected.setCourierId(userId);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);


        Delivery actual =  deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.DELIVERED);
        assertEquals(expected, actual);
        assertEquals(actual.getStatus().getValue(), "DELIVERED");
    }

    @Test
    public void vendorMaxZoneTest(){
        UUID vendorId = UUID.randomUUID();
        DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest = new DeliveryVendorIdMaxZonePutRequest();

        deliveryVendorIdMaxZonePutRequest.setVendorId(vendorId);
        deliveryVendorIdMaxZonePutRequest.setRadiusKm(BigDecimal.valueOf(5));

        Map<String, Object> vendorMap = new HashMap<>();
        vendorMap.put("allowOnlyOwnCouriers", true);
        vendorMap.put("maxDeliveryZone",BigDecimal.valueOf(2));

        when(vendorService.getVendor(vendorId)).thenReturn(vendorMap);
        when(vendorService.putVendor(vendorId,vendorMap)).thenReturn(true);

        DeliveryVendorIdMaxZonePutRequest response = deliveryService.vendorMaxZone(vendorId,deliveryVendorIdMaxZonePutRequest,vendorService);

        assertEquals(response, deliveryVendorIdMaxZonePutRequest);

    }

}