package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.DeliveryBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.GlobalConfig;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.GlobalConfigRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CourierBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.NoDeliveryFoundException;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;

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
    private GlobalConfigRepository globalConfigRepository;

    private DeliveryService deliveryService;
    private VendorService vendorService;
    private AdminService adminService;
    private CourierService courierService;
    private OrderService orderService;
    @Value("${globalConfigId}$")
    private UUID globalConfigId;

    @BeforeEach
    void setUp(){
        this.deliveryRepository = mock(DeliveryRepository.class);
        this.globalConfigRepository = mock(GlobalConfigRepository.class);
        this.vendorService = mock(VendorService.class);
        this.adminService = mock(AdminService.class);
        this.courierService = mock(CourierService.class);
        this.orderService = mock(OrderService.class);

        deliveryService = new DeliveryService(
                deliveryRepository, globalConfigRepository,vendorService, courierService, orderService);
    }

    @Test
    public void createDeliverySuccess() throws BadArgumentException {
        UUID orderId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();

        when(vendorService.getVendor(vendorId.toString())).thenReturn(new VendorBuilder().create());

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
;
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
        //I'm not sure if that choice was on purpose so I chose to preserve it
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
    public void changePrepTimeAsNonVendor() {
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

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .create();

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
        assertNull(deliveryService.changePrepTime(id, vendor.getId(), offsetDateTime));

    }

    @Test
    public void changePrepTimeAsVendor() {
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
    public void vendorMaxZoneTest() {
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
    public void adminGetMaxZoneTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setGlobalConfigId(globalConfigId);
        globalConfig.setDefaultMaxZone(defaultMaxZone);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.of(globalConfig);
        when(adminService.validate(adminId)).thenReturn(true);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        DeliveryAdminMaxZoneGet200Response response = deliveryService.adminGetMaxZone(adminId, adminService);
        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminGetMaxZoneNotFoundTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        Optional<GlobalConfig> optionalGlobalConfig = Optional.empty();
        when(adminService.validate(adminId)).thenReturn(true);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response response = deliveryService.adminGetMaxZone(adminId, adminService);
        assertNull(response);
    }

    @Test
    public void adminGetMaxZoneNullDefaultTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setGlobalConfigId(globalConfigId);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.of(globalConfig);
        when(adminService.validate(adminId)).thenReturn(true);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);

        DeliveryAdminMaxZoneGet200Response response = deliveryService.adminGetMaxZone(adminId, adminService);

        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminGetMaxZoneExceptionTest() throws ServiceUnavailableException {
        UUID adminId = UUID.randomUUID();

        Optional<GlobalConfig> optionalGlobalConfig = Optional.empty();
        when(adminService.validate(adminId)).thenReturn(false);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        assertThrows(AccessForbiddenException.class, () -> {
            deliveryService.adminGetMaxZone(adminId, adminService);
        });
    }

    @Test
    public void adminSetMaxZoneTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal newMaxZone = BigDecimal.valueOf(20);
        BigDecimal originalMaxZone = BigDecimal.valueOf(10);

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setGlobalConfigId(globalConfigId);
        globalConfig.setDefaultMaxZone(originalMaxZone);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.of(globalConfig);
        when(adminService.validate(adminId)).thenReturn(true);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(newMaxZone);

        DeliveryAdminMaxZoneGet200Response response = deliveryService.adminSetMaxZone(adminId, newMaxZone, adminService);

        assertEquals(BigDecimal.valueOf(20), globalConfig.getDefaultMaxZone());
        verify(globalConfigRepository).save(globalConfig);
        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminSetMaxZoneNotFoundTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.empty();
        when(adminService.validate(adminId)).thenReturn(true);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response response = deliveryService.adminSetMaxZone(adminId, defaultMaxZone, adminService);
        assertNull(response);
    }

    @Test
    public void adminSetMaxZoneExceptionTest() throws ServiceUnavailableException{
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.empty();
        when(adminService.validate(adminId)).thenReturn(false);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        assertThrows(AccessForbiddenException.class, () -> {
            deliveryService.adminSetMaxZone(adminId, defaultMaxZone, adminService);
        });
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
                ()->{
                    deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED);
                });
    }

}