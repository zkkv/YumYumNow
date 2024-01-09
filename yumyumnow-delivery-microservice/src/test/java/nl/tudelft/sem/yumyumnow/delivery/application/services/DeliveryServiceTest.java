package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.NoDeliveryFoundException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryVendorIdMaxZonePutRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


public class DeliveryServiceTest {
    private DeliveryRepository deliveryRepository;

    private DeliveryService deliveryService;
    private VendorService vendorService;
    private CourierService courierService;

    @BeforeEach
    void setUp() {
        deliveryRepository = mock(DeliveryRepository.class);
        vendorService = mock(VendorService.class);
        courierService = mock(CourierService.class);

        deliveryService = new DeliveryService(
                deliveryRepository, vendorService, courierService);
    }

    @Test
    public void getExistingDelivery() throws NoDeliveryFoundException {
        UUID id = UUID.randomUUID();

        Delivery expected = new Delivery();
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        Delivery actual = deliveryService.getDelivery(id);
        assertEquals(expected, actual);
    }

    @Test
    public void getNonExistingDelivery() {
        UUID id = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        Delivery expected = new Delivery();
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        assertThrows(NoDeliveryFoundException.class, () -> deliveryService.getDelivery(otherId));
    }

    @Test
    public void updateStatusOfNonExistingDelivery(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        assertThrows(NoDeliveryFoundException.class, () -> deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED));
    }

    @Test
    public void setStatusToAcceptedAsNonVendor() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Courier courier = new Courier();
        courier.setId(userId);

        Delivery expected = new Delivery();
        expected.setCourierId(courier.getId());
        expected.setVendorId(UUID.randomUUID());
        expected.setId(id);

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        assertThrows(AccessForbiddenException.class, () -> deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED));
    }

    @Test
    public void setStatusToRejectedAsNonVendor() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Courier courier = new Courier();
        courier.setId(userId);

        Delivery expected = new Delivery();
        expected.setCourierId(courier.getId());
        expected.setVendorId(UUID.randomUUID());
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(courierService.getCourier(userId.toString())).thenReturn(courier);

        assertThrows(AccessForbiddenException.class, () -> deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.REJECTED));
    }

    @Test
    public void setStatusToGivenToCourierAsNonVendor() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Courier courier = new Courier();
        courier.setId(userId);

        Delivery expected = new Delivery();
        expected.setCourierId(courier.getId());
        expected.setVendorId(UUID.randomUUID());

        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(courierService.getCourier(userId.toString())).thenReturn(courier);

        assertThrows(AccessForbiddenException.class, () -> deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.GIVEN_TO_COURIER));
    }

    @Test
    public void setStatusToPreparingAsNonVendor() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Courier courier = new Courier();
        courier.setId(userId);

        Delivery expected = new Delivery();
        expected.setCourierId(courier.getId());
        expected.setVendorId(UUID.randomUUID());
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(courierService.getCourier(userId.toString())).thenReturn(courier);

        assertThrows(AccessForbiddenException.class, () -> deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.PREPARING));
    }

    @Test
    public void setStatusToPendingNotAllowed() {
        UUID id = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(userId1)
                .createVendor();

        Courier courier = new Courier();
        courier.setId(userId2);

        Delivery expected = new Delivery();
        expected.setCourierId(courier.getId());
        expected.setVendorId(vendor.getId());
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);

        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(vendorService.getVendor(userId1.toString())).thenReturn(vendor);
        when(courierService.getCourier(userId2.toString())).thenReturn(courier);

        assertThrows(BadArgumentException.class, () -> deliveryService.updateStatus(
                id, userId1, DeliveryIdStatusPutRequest.StatusEnum.PENDING));

        assertThrows(BadArgumentException.class, () -> deliveryService.updateStatus(
                id, userId2, DeliveryIdStatusPutRequest.StatusEnum.PENDING));
    }

    @Test
    public void setStatusToDeliveredAsVendor() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(userId)
                .createVendor();

        Delivery expected = new Delivery();
        expected.setVendorId(UUID.randomUUID());
        expected.setCourierId(UUID.randomUUID());

        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(vendorService.getVendor(userId.toString())).thenReturn(vendor);

        assertThrows(AccessForbiddenException.class, () -> deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.DELIVERED));
    }

    @Test
    public void setStatusToAcceptedAsVendor()
            throws BadArgumentException, NoDeliveryFoundException, AccessForbiddenException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(userId)
                .createVendor();


        Delivery expected = new Delivery();
        expected.setVendorId(vendor.getId());
        expected.setId(id);
        expected.setStatus(Delivery.StatusEnum.PENDING);

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(vendorService.getVendor(vendor.getId().toString())).thenReturn(vendor);

        Delivery actual = deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED);
        assertEquals(expected, actual);
        assertEquals(actual.getStatus().getValue(), "ACCEPTED");
    }

    @Test
    public void setStatusToInTransitAsNonCourier() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(userId)
                .createVendor();

        Delivery expected = new Delivery();
        expected.setVendorId(userId);

        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(vendorService.getVendor(userId.toString())).thenReturn(vendor);

        assertThrows(AccessForbiddenException.class, () -> deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.IN_TRANSIT));
    }

    @Test
    public void setStatusToDeliveredAsNonCourier() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(userId)
                .createVendor();


        Delivery expected = new Delivery();
        expected.setVendorId(userId);
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(vendorService.getVendor(userId.toString())).thenReturn(vendor);

        assertThrows(AccessForbiddenException.class, () -> deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.DELIVERED));
    }

    @Test
    public void setStatusToInTransitAsCourier()
            throws BadArgumentException, NoDeliveryFoundException, AccessForbiddenException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Courier courier = new Courier();
        courier.setId(userId);

        Delivery expected = new Delivery();
        expected.setVendorId(UUID.randomUUID());
        expected.setCourierId(courier.getId());
        expected.setId(id);
        expected.setCourierId(userId);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(courierService.getCourier(userId.toString())).thenReturn(courier);

        Delivery actual = deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.IN_TRANSIT);
        assertEquals(expected, actual);
        assertEquals(actual.getStatus().getValue(), "IN_TRANSIT");
    }

    @Test
    public void setStatusToDeliveredAsCourier()
            throws BadArgumentException, NoDeliveryFoundException, AccessForbiddenException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Courier courier = new Courier();
        courier.setId(userId);

        Delivery expected = new Delivery();
        expected.setId(id);
        expected.setVendorId(UUID.randomUUID());
        expected.setCourierId(userId);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(courierService.getCourier(userId.toString())).thenReturn(courier);


        Delivery actual = deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.DELIVERED);
        assertEquals(expected, actual);
        assertEquals(actual.getStatus().getValue(), "DELIVERED");
    }

    @Test
    public void changePrepTimeAsNonVendor() {
        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new Delivery();
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        assertNull(deliveryService.changePrepTime(id, userId, offsetDateTime));
    }

    @Test
    public void changePrepTimeOnNonExistingOrder() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(deliveryRepository.findById(id)).thenReturn(Optional.empty());

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        assertNull(deliveryService.changePrepTime(id, userId, offsetDateTime));
    }

    @Test
    public void changePrepTimeAsVendorNonAccepted() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery delivery = new Delivery();
        delivery.setId(id);

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        delivery.setEstimatedPreparationFinishTime(offsetDateTime);
        assertNull(deliveryService.changePrepTime(id, userId, offsetDateTime));
    }

    @Test
    public void changePrepTimeUnauthorizedVendor() {
        UUID id = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(UUID.randomUUID())
                .createVendor();

        Delivery delivery = new Delivery();
        delivery.setId(id);
        delivery.setVendorId(UUID.randomUUID());


        delivery.setStatus(Delivery.StatusEnum.ACCEPTED);
        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));
        when(vendorService.getVendor(vendor.getId().toString())).thenReturn(vendor);

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        delivery.setEstimatedPreparationFinishTime(offsetDateTime);
        assertNull(deliveryService.changePrepTime(id, vendor.getId(), offsetDateTime));

    }

    @Test
    public void changePrepTimeAsVendor() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery delivery = new Delivery();
        delivery.setId(id);
        delivery.setVendorId(userId);

        Vendor vendor = new VendorBuilder()
                .setId(userId)
                .createVendor();

        delivery.setStatus(Delivery.StatusEnum.ACCEPTED);
        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));
        when(vendorService.getVendor(userId.toString())).thenReturn(vendor);

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        delivery.setEstimatedPreparationFinishTime(offsetDateTime);
        assertEquals(delivery, deliveryService.changePrepTime(id, userId, offsetDateTime));
        assertEquals(delivery.getEstimatedPreparationFinishTime(), offsetDateTime);
    }

    @Test
    public void vendorMaxZoneTest() {
        UUID vendorId = UUID.randomUUID();
        DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest = new DeliveryVendorIdMaxZonePutRequest();

        deliveryVendorIdMaxZonePutRequest.setVendorId(vendorId);
        deliveryVendorIdMaxZonePutRequest.setRadiusKm(BigDecimal.valueOf(5));

        Vendor vendor = new VendorBuilder()
                .setAllowsOnlyOwnCouriers(true)
                .setMaxDeliveryZoneKm(BigDecimal.valueOf(2))
                .createVendor();

        when(vendorService.getVendor(vendorId.toString())).thenReturn(vendor);
        when(vendorService.putVendor(vendor)).thenReturn(true);

        DeliveryVendorIdMaxZonePutRequest response = deliveryService.vendorMaxZone(vendorId, deliveryVendorIdMaxZonePutRequest, vendorService);

        assertEquals(response, deliveryVendorIdMaxZonePutRequest);
    }

}