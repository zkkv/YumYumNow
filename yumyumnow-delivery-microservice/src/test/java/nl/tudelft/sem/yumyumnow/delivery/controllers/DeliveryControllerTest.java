package nl.tudelft.sem.yumyumnow.delivery.controllers;

import nl.tudelft.sem.yumyumnow.delivery.application.services.AdminService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.DeliveryService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.OrderService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.CustomerService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.NoDeliveryFoundException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DeliveryControllerTest {

    private DeliveryService deliveryService;
    private DeliveryController deliveryController;
    private CustomerService userService;
    private VendorService vendorService;
    private AdminService adminService;
    private OrderService orderService;

    @BeforeEach
    void setUp(){
        this.deliveryService = mock(DeliveryService.class);
        this.userService = mock(CustomerService.class);
        this.vendorService = mock(VendorService.class);
        this.adminService = mock(AdminService.class);
        this.orderService = mock(OrderService.class);
        this.deliveryController = new DeliveryController(deliveryService, userService, vendorService, adminService, orderService);
    }

    @Test
    void deliveryPostSuccess() throws BadArgumentException {
        UUID id = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        Order order = new Order();
        order.setId(orderId);

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .createVendor();

        order.setVendor(vendor);

        Delivery delivery = new Delivery();
        delivery.setId(id);

        when(deliveryService.createDelivery(orderId, vendorId)).thenReturn(delivery);

        DeliveryPostRequest request = new DeliveryPostRequest();
        request.setOrderId(orderId);
        request.setVendorId(vendorId);

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);

        ResponseEntity<Delivery> actual = deliveryController.deliveryPost(request);

        assertEquals(expected, actual);
    }

    @Test
    void deliveryPostFail() throws BadArgumentException {
        UUID id = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        Order order = new Order();
        order.setId(orderId);

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .createVendor();

        order.setVendor(vendor);

        Delivery delivery = new Delivery();
        delivery.setId(id);

        when(deliveryService.createDelivery(orderId, vendorId))
                .thenThrow(BadArgumentException.class);


        DeliveryPostRequest request = new DeliveryPostRequest();
        request.setOrderId(orderId);
        request.setVendorId(vendorId);

        ResponseEntity<Delivery> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ResponseEntity<Delivery> actual = deliveryController.deliveryPost(request);

        assertEquals(expected, actual);
    }

    @Test
    void getDeliverySuccess() throws NoDeliveryFoundException {
        UUID id = UUID.randomUUID();

        Delivery delivery = new Delivery();
        delivery.setId(id);

        when(deliveryService.getDelivery(id)).thenReturn(delivery);

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);
        ResponseEntity<Delivery> actual = deliveryController.deliveryIdGet(id);

        assertEquals(expected, actual);
    }

    @Test
    void getDeliveryFail() throws NoDeliveryFoundException {
        UUID id = UUID.randomUUID();

        when(deliveryService.getDelivery(id)).thenThrow(NoDeliveryFoundException.class);

        ResponseEntity<Delivery> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ResponseEntity<Delivery> actual = deliveryController.deliveryIdGet(id);

        assertEquals(expected, actual);
    }

    @Test
    void deliveryIdPrepTimePostFail() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest1();

        deliveryIdDeliveryTimePostRequest.setUserId(userId);

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        deliveryIdDeliveryTimePostRequest.setEstimatedNewDeliveryTime(offsetDateTime);


        when(deliveryService.changePrepTime(id, deliveryIdDeliveryTimePostRequest.getUserId(), deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime())).thenReturn(null);

        ResponseEntity<Delivery> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdPrepTimePost(id, deliveryIdDeliveryTimePostRequest);

        assertEquals(expected, actual);
    }

    @Test
    public void updateStatusFail() throws BadArgumentException, NoDeliveryFoundException, AccessForbiddenException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.PENDING;
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();
        deliveryIdStatusPutRequest.setUserId(userId);
        deliveryIdStatusPutRequest.setStatus(status);

        when(deliveryService.updateStatus(
                id,deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus()))
                .thenThrow(BadArgumentException.class);

        ResponseEntity<Delivery> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdStatusPut(id, deliveryIdStatusPutRequest);


        assertEquals(expected, actual);
    }

    @Test
    void deliveryIdPrepTimePutFail() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest1();

        deliveryIdDeliveryTimePostRequest.setUserId(userId);

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        deliveryIdDeliveryTimePostRequest.setEstimatedNewDeliveryTime(offsetDateTime);


        when(deliveryService.changePrepTime(id,deliveryIdDeliveryTimePostRequest.getUserId(), deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime())).thenReturn(null);

        ResponseEntity<Delivery> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdPrepTimePut(id, deliveryIdDeliveryTimePostRequest);

        assertEquals(expected, actual);
    }

    @Test
    void deliveryIdPrepTimePutSuccess() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest1();

        deliveryIdDeliveryTimePostRequest.setUserId(userId);

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        deliveryIdDeliveryTimePostRequest.setEstimatedNewDeliveryTime(offsetDateTime);


        Delivery delivery = new Delivery();
        delivery.setId(id);
        delivery.setStatus(Delivery.StatusEnum.ACCEPTED);
        delivery.setEstimatedPreparationFinishTime(offsetDateTime);

        when(deliveryService.changePrepTime(id,deliveryIdDeliveryTimePostRequest.getUserId(), deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime()))
                .thenReturn(delivery);

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdPrepTimePut(id, deliveryIdDeliveryTimePostRequest);

        assertEquals(expected, actual);
    }

    @Test
    void deliveryIdPrepTimePostSuccess() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest1();

        deliveryIdDeliveryTimePostRequest.setUserId(userId);

        LocalDate localDate = LocalDate.of(2023, 12, 10);

        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        deliveryIdDeliveryTimePostRequest.setEstimatedNewDeliveryTime(offsetDateTime);


        Delivery delivery = new Delivery();
        delivery.setId(id);
        delivery.setStatus(Delivery.StatusEnum.ACCEPTED);
        delivery.setEstimatedPreparationFinishTime(offsetDateTime);

        when(deliveryService.changePrepTime(id, deliveryIdDeliveryTimePostRequest.getUserId(), deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime()))
                .thenReturn(delivery);

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdPrepTimePost(id, deliveryIdDeliveryTimePostRequest);

        assertEquals(expected, actual);
    }

    @Test
    void updateStatusSuccess() throws BadArgumentException, NoDeliveryFoundException, AccessForbiddenException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.PREPARING;
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();
        deliveryIdStatusPutRequest.setUserId(userId);
        deliveryIdStatusPutRequest.setStatus(status);

        Delivery delivery = new Delivery();
        delivery.setStatus(Delivery.StatusEnum.PREPARING);
        delivery.setId(id);

        when(deliveryService.updateStatus(
                id,deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus()))
                .thenReturn(delivery);

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdStatusPut(id, deliveryIdStatusPutRequest);

        assertEquals(expected, actual);
    }

    @Test
    void vendorMaxZonePutSuccessTest(){
        UUID vendorId = UUID.randomUUID();
        DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest = new DeliveryVendorIdMaxZonePutRequest();

        deliveryVendorIdMaxZonePutRequest.setVendorId(vendorId);
        deliveryVendorIdMaxZonePutRequest.setRadiusKm(BigDecimal.valueOf(5));

        when(deliveryService.vendorMaxZone(vendorId,deliveryVendorIdMaxZonePutRequest,vendorService)).thenReturn(deliveryVendorIdMaxZonePutRequest);

        ResponseEntity<DeliveryVendorIdMaxZonePutRequest> response = deliveryController.deliveryVendorIdMaxZonePut(vendorId, deliveryVendorIdMaxZonePutRequest);
        assertEquals(ResponseEntity.ok(deliveryVendorIdMaxZonePutRequest), response);
    }

    @Test
    void vendorMaxZonePutFailedTest(){
        UUID vendorId = UUID.randomUUID();
        DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest = new DeliveryVendorIdMaxZonePutRequest();

        deliveryVendorIdMaxZonePutRequest.setVendorId(vendorId);
        deliveryVendorIdMaxZonePutRequest.setRadiusKm(BigDecimal.valueOf(5));

        when(deliveryService.vendorMaxZone(vendorId,deliveryVendorIdMaxZonePutRequest,vendorService)).thenReturn(null);

        ResponseEntity<DeliveryVendorIdMaxZonePutRequest> response = deliveryController.deliveryVendorIdMaxZonePut(vendorId, deliveryVendorIdMaxZonePutRequest);
        assertEquals(ResponseEntity.badRequest().body(deliveryVendorIdMaxZonePutRequest), response);
    }

    @Test
    void adminMaxZoneGetSuccessTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(deliveryService.adminGetMaxZone(adminId,adminService)).thenReturn(deliveryAdminMaxZoneGet200Response);
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = deliveryController.deliveryAdminMaxZoneGet(adminId);
        assertEquals(ResponseEntity.ok(deliveryAdminMaxZoneGet200Response), response);
    }

    @Test
    void adminMaxZoneGetFailTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(deliveryService.adminGetMaxZone(adminId,adminService)).thenReturn(null);
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = deliveryController.deliveryAdminMaxZoneGet(adminId);
        assertEquals(ResponseEntity.badRequest().body(null), response);
    }

    @Test
    void adminMaxZoneGetServiceUnavailableTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(deliveryService.adminGetMaxZone(adminId,adminService)).thenThrow(new ServiceUnavailableException(""));
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = deliveryController.deliveryAdminMaxZoneGet(adminId);
        assertEquals(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE), response);
    }

    @Test
    void adminMaxZoneGetAccessForbiddenTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(deliveryService.adminGetMaxZone(adminId,adminService)).thenThrow(new AccessForbiddenException(""));
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = deliveryController.deliveryAdminMaxZoneGet(adminId);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), response);
    }

    @Test
    void adminMaxZoneSetSuccessTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(deliveryService.adminSetMaxZone(adminId, defaultMaxZone, adminService)).thenReturn(deliveryAdminMaxZoneGet200Response);
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = deliveryController.deliveryAdminMaxZonePut(deliveryAdminMaxZoneGet200Response);
        assertEquals(ResponseEntity.ok(deliveryAdminMaxZoneGet200Response), response);
    }

    @Test
    void adminMaxZoneSetFailTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);
        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(deliveryService.adminSetMaxZone(adminId,defaultMaxZone,adminService)).thenReturn(null);
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = deliveryController.deliveryAdminMaxZonePut(deliveryAdminMaxZoneGet200Response);
        assertEquals(ResponseEntity.badRequest().body(deliveryAdminMaxZoneGet200Response), response);
    }

    @Test
    void adminMaxZoneSetServiceUnavailableTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);
        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(deliveryService.adminSetMaxZone(adminId,defaultMaxZone,adminService)).thenThrow(new ServiceUnavailableException(""));
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = deliveryController.deliveryAdminMaxZonePut(deliveryAdminMaxZoneGet200Response);
        assertEquals(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE), response);
    }

    @Test
    void adminMaxZoneSetAccessForbiddenTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);
        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(deliveryService.adminSetMaxZone(adminId,defaultMaxZone,adminService)).thenThrow(new AccessForbiddenException(""));
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = deliveryController.deliveryAdminMaxZonePut(deliveryAdminMaxZoneGet200Response);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), response);
    }

    @Test
    void totalDeliveryTimeSuccessfulTest() throws Exception {
        UUID deliveryId = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);

        when(deliveryService.addDeliveryTime(deliveryId, orderService, userService)).thenReturn(delivery);

        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest1 = new DeliveryIdDeliveryTimePostRequest1();

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);
        ResponseEntity<Delivery> actual = deliveryController.deliveryIdDeliveryTimePost(deliveryId, deliveryIdDeliveryTimePostRequest1);

        assertEquals(expected, actual);
    }

    @Test
    void totalDeliveryTimeUnsuccessfulTest() throws Exception{
        UUID deliveryId = UUID.randomUUID();

        when(deliveryService.addDeliveryTime(deliveryId, orderService, userService)).thenThrow(NoDeliveryFoundException.class);

        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest1 = new DeliveryIdDeliveryTimePostRequest1();

        ResponseEntity<Delivery> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ResponseEntity<Delivery> actual = deliveryController.deliveryIdDeliveryTimePost(deliveryId, deliveryIdDeliveryTimePostRequest1);

        assertEquals(expected, actual);
    }

    @Test
    void updateTotalDeliveryTimeSuccessfulTest() throws Exception {
        // The PUT request for updating the delivery time
        UUID deliveryId = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);

        when(deliveryService.addDeliveryTime(deliveryId, orderService, userService)).thenReturn(delivery);

        DeliveryIdDeliveryTimePostRequest deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest();

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);
        ResponseEntity<Delivery> actual = deliveryController.deliveryIdDeliveryTimePut(deliveryId, deliveryIdDeliveryTimePostRequest);

        assertEquals(expected, actual);
    }
}