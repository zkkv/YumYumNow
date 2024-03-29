package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.NoDeliveryFoundException;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


public class DeliveryServiceTest {
    private DeliveryRepository deliveryRepository;
    private DeliveryService deliveryService;
    private VendorService vendorService;
    private CourierService courierService;
    private OrderService orderService;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        this.deliveryRepository = mock(DeliveryRepository.class);
        this.vendorService = mock(VendorService.class);
        this.courierService = mock(CourierService.class);
        this.orderService = mock(OrderService.class);
        this.emailService = mock(EmailService.class);

        deliveryService = new DeliveryService(
                deliveryRepository, vendorService, courierService, orderService, emailService);
    }

    @Test
    public void createDeliverySuccess() throws BadArgumentException {
        UUID orderId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        when(vendorService.getVendor(vendorId.toString())).thenReturn(new VendorBuilder().create());
        when(orderService.findOrderById(orderId)).thenReturn(new OrderBuilder().create());

        Delivery actual = deliveryService.createDelivery(orderId, vendorId);

        assertEquals(vendorId, actual.getVendorId());
    }

    @Test
    public void createDeliveryFail() {
        UUID orderId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        when(vendorService.getVendor(vendorId.toString())).thenReturn(null);

        assertThrows(BadArgumentException.class, () ->
                deliveryService.createDelivery(orderId, vendorId));
    }

    @Test
    public void testDistance(){
        Location location1 = new Location();
        location1.setLongitude(BigDecimal.ONE);
        location1.setLatitude(BigDecimal.TEN);

        DeliveryCurrentLocation location2 = new DeliveryCurrentLocation();
        location2.setLongitude(BigDecimal.ZERO);
        location2.setLatitude(BigDecimal.ONE);

        assertEquals(1007.9498662033461, deliveryService.distanceBetween(location1,location2));
    }

    @Test
    public void getExistingDelivery() throws NoDeliveryFoundException {
        UUID id = UUID.randomUUID();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .create();
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        Delivery actual = deliveryService.getDelivery(id);
        assertEquals(expected, actual);
    }

    @Test
    public void getNonExistingDelivery() {
        UUID id = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .create();
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        assertThrows(NoDeliveryFoundException.class, () -> deliveryService.getDelivery(otherId));
    }

    @Test
    public void updateStatusOfNonExistingDelivery() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        assertThrows(NoDeliveryFoundException.class, () -> deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED));
    }

    @Test
    public void setStatusToAcceptedAsNonVendor() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Courier courier = new CourierBuilder()
                .setId(userId)
                .create();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setCourierId(courier.getId())
                .setVendorId(UUID.randomUUID())
                .create();

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        assertThrows(AccessForbiddenException.class, () -> deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED));
    }

    @Test
    public void setStatusToRejectedAsNonVendor() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Courier courier = new CourierBuilder()
                .setId(userId)
                .create();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setCourierId(courier.getId())
                .setVendorId(UUID.randomUUID())
                .create();

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

        Courier courier = new CourierBuilder()
                .setId(userId)
                .create();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setCourierId(courier.getId())
                .setVendorId(UUID.randomUUID())
                .create();
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

        Courier courier = new CourierBuilder()
                .setId(userId)
                .create();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setCourierId(courier.getId())
                .setVendorId(UUID.randomUUID())
                .create();

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
                .create();

        Courier courier = new CourierBuilder()
                .setId(userId2)
                .create();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setCourierId(courier.getId())
                .setVendorId(vendor.getId())
                .create();

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
                .create();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setCourierId(UUID.randomUUID())
                .setVendorId(UUID.randomUUID())
                .create();

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
                .create();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setOrderId(id)
                .setVendorId(vendor.getId())
                .setStatus(Delivery.StatusEnum.PENDING)
                .create();

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(vendorService.getVendor(vendor.getId().toString())).thenReturn(vendor);
        when(orderService.isPaid(id)).thenReturn(true);

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
                .create();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setVendorId(userId)
                .create();

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
                .create();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setVendorId(userId)
                .create();

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

        Courier courier = new CourierBuilder()
                .setId(userId)
                .create();

        //I chose to set courier id twice because that's what happens in the original code
        //I'm not sure if that choice was on purpose, so I chose to preserve it
        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setVendorId(UUID.randomUUID())
                .setCourierId(courier.getId())
                .setCourierId(userId)
                .create();

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

        Courier courier = new CourierBuilder()
                .setId(userId)
                .create();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setVendorId(UUID.randomUUID())
                .setCourierId(userId)
                .create();

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(courierService.getCourier(userId.toString())).thenReturn(courier);


        Delivery actual = deliveryService.updateStatus(
                id, userId, DeliveryIdStatusPutRequest.StatusEnum.DELIVERED);
        assertEquals(expected, actual);
        assertEquals(actual.getStatus().getValue(), "DELIVERED");
    }

    @Test
    public void changePrepTimeAsNonVendor() throws BadArgumentException, NoDeliveryFoundException {
        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .create();

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        assertThrows(BadArgumentException.class, () -> deliveryService.changePrepTime(id, userId, offsetDateTime));
    }

    @Test
    public void changePrepTimeOnNonExistingOrder() throws BadArgumentException, NoDeliveryFoundException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(deliveryRepository.findById(id)).thenReturn(Optional.empty());

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        assertThrows(NoDeliveryFoundException.class , () -> deliveryService.changePrepTime(id, userId, offsetDateTime));
    }

    @Test
    public void changePrepTimeAsVendorNonAccepted() throws BadArgumentException, NoDeliveryFoundException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .create();

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        delivery.setEstimatedPreparationFinishTime(offsetDateTime);
        assertThrows(BadArgumentException.class, () -> deliveryService.changePrepTime(id, userId, offsetDateTime));
    }

    @Test
    public void changePrepTimeUnauthorizedVendor() throws BadArgumentException, NoDeliveryFoundException {
        UUID id = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(UUID.randomUUID())
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setVendorId(UUID.randomUUID())
                .setStatus(Delivery.StatusEnum.ACCEPTED)
                .create();

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));
        when(vendorService.getVendor(vendor.getId().toString())).thenReturn(vendor);

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        delivery.setEstimatedPreparationFinishTime(offsetDateTime);
        assertThrows(BadArgumentException.class , () -> deliveryService.changePrepTime(id, vendor.getId(), offsetDateTime));
    }

    @Test
    public void changePrepTimeAsVendor() throws BadArgumentException, NoDeliveryFoundException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setVendorId(userId)
                .setStatus(Delivery.StatusEnum.ACCEPTED)
                .create();

        Vendor vendor = new VendorBuilder()
                .setId(userId)
                .create();

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
    public void vendorMaxZoneTest() throws BadArgumentException {
        UUID vendorId = UUID.randomUUID();
        DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest = new DeliveryVendorIdMaxZonePutRequest();

        deliveryVendorIdMaxZonePutRequest.setVendorId(vendorId);
        deliveryVendorIdMaxZonePutRequest.setRadiusKm(BigDecimal.valueOf(5));

        Vendor vendor = new VendorBuilder()
                .setAllowsOnlyOwnCouriers(true)
                .setMaxDeliveryZoneKm(BigDecimal.valueOf(2))
                .create();

        when(vendorService.getVendor(vendorId.toString())).thenReturn(vendor);
        when(vendorService.putVendor(vendor)).thenReturn(true);

        DeliveryVendorIdMaxZonePutRequest response = deliveryService.vendorMaxZone(vendorId, deliveryVendorIdMaxZonePutRequest, vendorService);

        assertEquals(response, deliveryVendorIdMaxZonePutRequest);
    }

    @Test
    public void assignCourierSuccess()
            throws NoDeliveryFoundException, AccessForbiddenException, BadArgumentException {
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setVendorId(vendorId)
                .create();

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        Vendor vendor = new Vendor(vendorId, new Location(), "", true, new BigDecimal(1000));
        when(vendorService.getVendor(vendorId.toString())).thenReturn(vendor);

        Courier courier = new Courier(courierId, vendor);
        when(courierService.getCourier(courierId.toString())).thenReturn(courier);

        Delivery actual = deliveryService.assignCourier(id, courierId);
        assertEquals(expected, actual);
    }

    @Test
    public void assignCourierNoSuchCourier() {
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .create();

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        assertThrows(BadArgumentException.class,
                () -> deliveryService.assignCourier(id, courierId));
    }

    @Test
    public void assignCourierAnotherAlreadyAssigned() {
        UUID id = UUID.randomUUID();
        UUID oldCourierId = UUID.randomUUID();
        UUID newCourierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setCourierId(oldCourierId)
                .setVendorId(vendorId)
                .create();

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        Courier courier = new Courier(oldCourierId, null);
        when(courierService.getCourier(newCourierId.toString())).thenReturn(courier);

        assertThrows(AccessForbiddenException.class,
                () -> deliveryService.assignCourier(id, newCourierId));
    }

    @Test
    public void assignCourierSameAlreadyAssigned() {
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setCourierId(courierId)
                .create();

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        Courier courier = new Courier(courierId, null);
        when(courierService.getCourier(courierId.toString())).thenReturn(courier);

        assertThrows(BadArgumentException.class,
                () -> deliveryService.assignCourier(id, courierId));
    }

    @Test
    public void assignCourierNotAssignedToVendor() {
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        Delivery expected = new DeliveryBuilder()
                .setId(id)
                .setVendorId(vendorId)
                .create();

        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);

        Vendor vendor = new Vendor(vendorId, new Location(), "", true, new BigDecimal(1000));
        Vendor otherVendor = new Vendor(UUID.randomUUID(), new Location(), "", true, new BigDecimal(1000));
        when(vendorService.getVendor(vendorId.toString())).thenReturn(vendor);

        Courier courier = new Courier(courierId, otherVendor);
        when(courierService.getCourier(courierId.toString())).thenReturn(courier);

        assertThrows(AccessForbiddenException.class,
                () -> deliveryService.assignCourier(id, courierId));
    }

    @Test
    public void unpaidStatusChangeTest() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery delivery = new Delivery();
        delivery.setId(id);
        delivery.setVendorId(UUID.randomUUID());


        delivery.setStatus(Delivery.StatusEnum.PENDING);
        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));
        when(orderService.isPaid(id)).thenReturn(false);
        assertThrows(AccessForbiddenException.class,
                () -> {
                    deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED);
                });
    }

    @Test
    void sendEmailNoOrder() {
        UUID deliverId = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliverId);
        when(deliveryRepository.findById(deliverId)).thenReturn(Optional.of(delivery));
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED;
        assertThrows(BadArgumentException.class, () -> deliveryService.sendEmail(status, deliverId));

    }

    @Test
    void sendEmailNoCustomer() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, null, null);

        UUID deliverId = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliverId);
        delivery.setOrderId(orderId);

        when(orderService.findOrderById(orderId)).thenReturn(order);

        when(deliveryRepository.findById(deliverId)).thenReturn(Optional.of(delivery));

        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED;


        assertThrows(BadArgumentException.class, () -> deliveryService.sendEmail(status, deliverId));

    }

    @Test
    void sendEmailNoEmail() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "Alex", new Location(), null, "");

        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, null, customer);

        UUID deliverId = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliverId);
        delivery.setOrderId(orderId);

        when(orderService.findOrderById(orderId)).thenReturn(order);

        when(deliveryRepository.findById(deliverId)).thenReturn(Optional.of(delivery));

        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED;


        assertThrows(BadArgumentException.class, () -> deliveryService.sendEmail(status, deliverId));

    }

    @Test
    void sendEmailSuccess() throws BadArgumentException {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "Alex", new Location(), "max.verstappen1@gmail.com", "");

        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, null, customer);

        UUID deliverId = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliverId);
        delivery.setOrderId(orderId);

        when(orderService.findOrderById(orderId)).thenReturn(order);

        when(deliveryRepository.findById(deliverId)).thenReturn(Optional.of(delivery));

        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED;

        when(emailService.send("The status of your order has been changed to ACCEPTED", "max.verstappen1@gmail.com")).thenReturn("Email has been successfully sent");


        assertEquals("Email has been successfully sent", deliveryService.sendEmail(status, deliverId));

        verify(emailService).send("The status of your order has been changed to ACCEPTED", "max.verstappen1@gmail.com");
    }
    @Test
    public void voidGetAvailableDeliveriesAsNonCourier(){

        UUID courierId = UUID.randomUUID();

        when(courierService.getCourier(courierId.toString())).thenReturn(null);

        assertThrows(AccessForbiddenException.class, () -> deliveryService.getAvailableDeliveries(BigDecimal.ONE, new Location(), courierId));
    }

    @Test
    public void getAvailableDeliveriesInvalidRadius() {
        UUID courierId = UUID.randomUUID();

        when(courierService.getCourier(courierId.toString())).thenReturn(new Courier(courierId, null));

        assertThrows(BadArgumentException.class, () -> deliveryService.getAvailableDeliveries(BigDecimal.valueOf(-1), new Location(), courierId));
    }

    @Test
    public void getAvailableDeliveriesUnassignedCourier() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        UUID courierId = UUID.randomUUID();

        when(courierService.getCourier(courierId.toString())).thenReturn(new CourierBuilder()
                .setId(courierId)
                .create());

        List<Delivery> allDeliveries = new ArrayList<>();


        UUID delivery2vendorId = UUID.randomUUID();
        UUID delivery3vendorId = UUID.randomUUID();

        DeliveryCurrentLocation delivery2location = new DeliveryCurrentLocation();
        delivery2location.setLatitude(BigDecimal.ONE);
        delivery2location.setLongitude(BigDecimal.ONE);

        DeliveryCurrentLocation delivery4location = new DeliveryCurrentLocation();
        delivery4location.setLatitude(BigDecimal.ZERO);
        delivery4location.setLongitude(BigDecimal.ZERO);

        DeliveryCurrentLocation delivery5location = new DeliveryCurrentLocation();
        delivery5location.setLatitude(BigDecimal.valueOf(100));
        delivery5location.setLongitude(BigDecimal.valueOf(100));

        Vendor delivery2vendor = new Vendor(delivery2vendorId, new Location(), "", false, BigDecimal.TEN);

        Vendor delivery3vendor = new Vendor(delivery3vendorId, new Location(), "", true, BigDecimal.TEN);


        Delivery delivery1 = new Delivery();
        delivery1.setCourierId(UUID.randomUUID());

        Delivery delivery2 = new Delivery();
        delivery2.setVendorId(delivery2vendorId);
        when(vendorService.getVendor(delivery2vendorId.toString())).thenReturn(delivery2vendor);
        delivery2.setCurrentLocation(delivery2location);

        Delivery delivery3 = new Delivery();
        delivery3.setVendorId(delivery3vendorId);
        when(vendorService.getVendor(delivery3vendorId.toString())).thenReturn(delivery3vendor);
        delivery3.setCurrentLocation(delivery2location);

        Delivery delivery4 = new Delivery();
        delivery4.setVendorId(delivery2vendorId);
        delivery4.setCurrentLocation(delivery4location);

        Delivery delivery5 = new Delivery();
        delivery5.setVendorId(delivery2vendorId);
        delivery5.setCurrentLocation(delivery5location);


        allDeliveries.add(delivery1);
        allDeliveries.add(delivery2);
        allDeliveries.add(delivery3);
        allDeliveries.add(delivery4);
        allDeliveries.add(delivery5);

        List<Delivery> expected = List.of(delivery4, delivery2);

        when(deliveryRepository.findAll()).thenReturn(allDeliveries);

        Location courierLocation = new Location();
        courierLocation.setLongitude(delivery4location.getLongitude());
        courierLocation.setLatitude(delivery4location.getLatitude());


        assertEquals(expected, deliveryService.getAvailableDeliveries(BigDecimal.valueOf(200), courierLocation, courierId));
    }


    @Test
    public void getAvailableDeliveriesAssignedCourier() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        UUID courierId = UUID.randomUUID();
        UUID delivery2vendorId = UUID.randomUUID();
        UUID delivery3vendorId = UUID.randomUUID();


        List<Delivery> allDeliveries = new ArrayList<>();

        DeliveryCurrentLocation delivery2location = new DeliveryCurrentLocation()
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE);

        DeliveryCurrentLocation delivery4location = new DeliveryCurrentLocation()
                .latitude(BigDecimal.ZERO)
                .longitude(BigDecimal.ZERO);

        DeliveryCurrentLocation delivery5location = new DeliveryCurrentLocation()
                .latitude(BigDecimal.valueOf(100))
                .longitude(BigDecimal.valueOf(100));

        Vendor delivery2vendor = new VendorBuilder()
                .setId(delivery2vendorId)
                .setAddress(new Location())
                .setPhoneNumber("")
                .setAllowsOnlyOwnCouriers(false)
                .setMaxDeliveryZoneKm(BigDecimal.TEN)
                .create();

        Vendor delivery3vendor = new VendorBuilder()
                .setId(delivery3vendorId)
                .setAddress(new Location())
                .setPhoneNumber("")
                .setAllowsOnlyOwnCouriers(false)
                .setMaxDeliveryZoneKm(BigDecimal.TEN)
                .create();

        when(courierService.getCourier(courierId.toString())).thenReturn(new Courier(courierId, delivery3vendor));
        when(vendorService.getVendor(delivery2vendorId.toString())).thenReturn(delivery2vendor);
        when(vendorService.getVendor(delivery3vendorId.toString())).thenReturn(delivery3vendor);


        Delivery delivery1 = new DeliveryBuilder()
                .setCourierId(UUID.randomUUID())
                .create();

        Delivery delivery2 = new DeliveryBuilder()
                .setVendorId(delivery2vendorId)
                .setCurrentLocation(delivery2location)
                .create();

        Delivery delivery3 = new DeliveryBuilder()
                .setVendorId(delivery3vendorId)
                .setCurrentLocation(delivery2location)
                .create();

        Delivery delivery4 = new DeliveryBuilder()
                .setVendorId(delivery2vendorId)
                .setCurrentLocation(delivery4location)
                .create();

        Delivery delivery5 = new DeliveryBuilder()
                .setVendorId(delivery2vendorId)
                .setCurrentLocation(delivery5location)
                .create();


        allDeliveries.add(delivery1);
        allDeliveries.add(delivery2);
        allDeliveries.add(delivery3);
        allDeliveries.add(delivery4);
        allDeliveries.add(delivery5);

        List<Delivery> expected = List.of(delivery2, delivery3, delivery4);

        when(deliveryRepository.findAll()).thenReturn(allDeliveries);

        Location courierLocation = new Location();
        courierLocation.setLongitude(delivery4location.getLongitude());
        courierLocation.setLatitude(delivery4location.getLatitude());


        assertThat(deliveryService.getAvailableDeliveries(BigDecimal.valueOf(200), courierLocation, courierId))
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void updateLocationThrowsExceptionTest() {
        UUID id = UUID.randomUUID();
        when(deliveryRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NoDeliveryFoundException.class, () -> deliveryService.updateLocation(id, new Location()));
    }

    @Test
    void updateLocationTest() throws NoDeliveryFoundException {
        UUID id = UUID.randomUUID();
        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .create();
        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));
        Location location = new LocationBuilder()
                .setLatitude(BigDecimal.ONE)
                .setLongitude(BigDecimal.ONE)
                .create();
        DeliveryCurrentLocation deliveryLocation = new DeliveryCurrentLocation()
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE);
        deliveryService.updateLocation(id, location);
        assertEquals(deliveryLocation, delivery.getCurrentLocation());
    }
}