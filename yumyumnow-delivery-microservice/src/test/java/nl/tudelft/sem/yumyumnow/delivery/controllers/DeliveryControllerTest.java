package nl.tudelft.sem.yumyumnow.delivery.controllers;

import nl.tudelft.sem.yumyumnow.delivery.application.services.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.DeliveryBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.OrderBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.NoDeliveryFoundException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import nl.tudelft.sem.yumyumnow.delivery.model.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.math.BigDecimal;
import java.util.List;
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

    private EmailService emailService;

    @BeforeEach
    void setUp(){
        this.deliveryService = mock(DeliveryService.class);
        this.userService = mock(CustomerService.class);
        this.vendorService = mock(VendorService.class);
        this.adminService = mock(AdminService.class);
        this.orderService = mock(OrderService.class);
        this.emailService = mock(EmailService.class);
        this.deliveryController = new DeliveryController(deliveryService, userService, vendorService, adminService, orderService, emailService);
    }

    @Test
    void deliveryPostSuccess() throws BadArgumentException {
        UUID id = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();

        Order order = new OrderBuilder()
                .setOrderId(orderId)
                .setOrderVendor(vendor)
                .create();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .create();

        when(deliveryService.createDelivery(orderId, vendorId)).thenReturn(delivery);

        DeliveryPostRequest request = new DeliveryPostRequest();
        request.setOrderId(orderId);
        request.setVendorId(vendorId);

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);

        ResponseEntity<Delivery> actual = deliveryController.deliveryPost(request);

        assertEquals(expected, actual);
    }

    @Test
    void deliveryPostBadRequestTest() throws BadArgumentException {
        UUID vendorId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();

        Order order = new OrderBuilder()
                .setOrderId(orderId)
                .setOrderVendor(vendor)
                .create();

        when(deliveryService.createDelivery(orderId, vendorId))
                .thenThrow(BadArgumentException.class);


        DeliveryPostRequest request = new DeliveryPostRequest();
        request.setOrderId(orderId);
        request.setVendorId(vendorId);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryPost(request));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void deliveryPostServiceUnavailableTest() throws BadArgumentException {
        UUID vendorId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();

        Order order = new OrderBuilder()
                .setOrderId(orderId)
                .setOrderVendor(vendor)
                .create();

        when(deliveryService.createDelivery(orderId, vendorId))
                .thenThrow(RestClientException.class);


        DeliveryPostRequest request = new DeliveryPostRequest();
        request.setOrderId(orderId);
        request.setVendorId(vendorId);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryPost(request));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void deliveryPostGenericExceptionTest() throws BadArgumentException {
        UUID vendorId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();

        Order order = new OrderBuilder()
                .setOrderId(orderId)
                .setOrderVendor(vendor)
                .create();

        when(deliveryService.createDelivery(orderId, vendorId))
                .thenAnswer(t -> {throw new Exception();});


        DeliveryPostRequest request = new DeliveryPostRequest();
        request.setOrderId(orderId);
        request.setVendorId(vendorId);

        Exception exception = assertThrows(Exception.class,
                () -> deliveryController.deliveryPost(request));
    }

    @Test
    void getDeliverySuccess() throws NoDeliveryFoundException {
        UUID id = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .create();

        when(deliveryService.getDelivery(id)).thenReturn(delivery);

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);
        ResponseEntity<Delivery> actual = deliveryController.deliveryIdGet(id);

        assertEquals(expected, actual);
    }

    @Test
    void getDeliveryBadRequestTest() throws NoDeliveryFoundException {
        UUID id = UUID.randomUUID();

        when(deliveryService.getDelivery(id)).thenThrow(NoDeliveryFoundException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdGet(id));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getDeliveryServiceUnavailableTest() throws NoDeliveryFoundException {
        UUID id = UUID.randomUUID();

        when(deliveryService.getDelivery(id)).thenThrow(RestClientException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdGet(id));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void getDeliveryGenericExceptionTest() throws NoDeliveryFoundException {
        UUID id = UUID.randomUUID();

        when(deliveryService.getDelivery(id)).thenAnswer(t -> {throw new Exception();});

        Exception exception = assertThrows(Exception.class,
                () -> deliveryController.deliveryIdGet(id));
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

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdPrepTimePost(id, deliveryIdDeliveryTimePostRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void updateStatusBadRequestTest() throws BadArgumentException, NoDeliveryFoundException, AccessForbiddenException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.PENDING;
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();
        deliveryIdStatusPutRequest.setUserId(userId);
        deliveryIdStatusPutRequest.setStatus(status);

        when(deliveryService.updateStatus(
                id,deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus()))
                .thenThrow(BadArgumentException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdStatusPut(id, deliveryIdStatusPutRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void updateStatusServiceUnavailableTest() throws BadArgumentException, NoDeliveryFoundException, AccessForbiddenException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.PENDING;
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();
        deliveryIdStatusPutRequest.setUserId(userId);
        deliveryIdStatusPutRequest.setStatus(status);

        when(deliveryService.updateStatus(
                id,deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus()))
                .thenThrow(RestClientException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdStatusPut(id, deliveryIdStatusPutRequest));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    public void updateStatusGenericExceptionTest() throws BadArgumentException, NoDeliveryFoundException, AccessForbiddenException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.PENDING;
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();
        deliveryIdStatusPutRequest.setUserId(userId);
        deliveryIdStatusPutRequest.setStatus(status);

        when(deliveryService.updateStatus(
                id,deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus()))
                .thenAnswer(t -> {throw new Exception();});

        assertThrows(Exception.class,
                () -> deliveryController.deliveryIdStatusPut(id, deliveryIdStatusPutRequest));
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

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdPrepTimePut(id, deliveryIdDeliveryTimePostRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
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

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setStatus(Delivery.StatusEnum.ACCEPTED)
                .setEstimatedPreparationFinishTime(offsetDateTime)
                .create();

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

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setStatus(Delivery.StatusEnum.ACCEPTED)
                .setEstimatedPreparationFinishTime(offsetDateTime)
                .create();

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

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setStatus(Delivery.StatusEnum.PREPARING)
                .create();

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

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryVendorIdMaxZonePut(vendorId, deliveryVendorIdMaxZonePutRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void totalDeliveryTimeSuccessfulTest() throws Exception {
        UUID deliveryId = UUID.randomUUID();
        Delivery delivery = new DeliveryBuilder()
                .setId(deliveryId)
                .create();

        when(deliveryService.addDeliveryTime(deliveryId, orderService, userService)).thenReturn(delivery);

        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest1 = new DeliveryIdDeliveryTimePostRequest1();

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);
        ResponseEntity<Delivery> actual = deliveryController.deliveryIdDeliveryTimePost(deliveryId, deliveryIdDeliveryTimePostRequest1);

        assertEquals(expected, actual);
    }

    @Test
    void totalDeliveryTimeUnsuccessfulTest() throws Exception{
        UUID deliveryId = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);

        when(deliveryService.addDeliveryTime(deliveryId, orderService, userService)).thenThrow(NoDeliveryFoundException.class);

        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest1 = new DeliveryIdDeliveryTimePostRequest1();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdDeliveryTimePost(deliveryId, deliveryIdDeliveryTimePostRequest1));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void totalDeliveryTimeServiceUnavailableTest() throws Exception{
        UUID deliveryId = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);

        when(deliveryService.addDeliveryTime(deliveryId, orderService, userService))
                .thenThrow(RestClientException.class);

        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest1 = new DeliveryIdDeliveryTimePostRequest1();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdDeliveryTimePost(deliveryId, deliveryIdDeliveryTimePostRequest1));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void totalDeliveryTimeGenericExceptionTest() throws Exception{
        UUID deliveryId = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);

        when(deliveryService.addDeliveryTime(deliveryId, orderService, userService))
                .thenAnswer(t -> {throw new Exception();});

        DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest1 = new DeliveryIdDeliveryTimePostRequest1();

        Exception exception = assertThrows(Exception.class,
                () -> deliveryController.deliveryIdDeliveryTimePost(deliveryId, deliveryIdDeliveryTimePostRequest1));
    }

    @Test
    void assignCourierSuccess()
            throws NoDeliveryFoundException, AccessForbiddenException, BadArgumentException {
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .create();

        assertNotEquals(delivery.getCourierId(), courierId);
        delivery.setCourierId(courierId);

        when(deliveryService.assignCourier(id, courierId)).thenReturn(delivery);

        DeliveryIdAssignPutRequest request = new DeliveryIdAssignPutRequest();
        request.setCourierId(courierId);

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);
        ResponseEntity<Delivery> actual = deliveryController.deliveryIdAssignPut(id, request);

        assertEquals(expected, actual);
    }

    @Test
    void assignCourierBadRequest()
            throws NoDeliveryFoundException, AccessForbiddenException, BadArgumentException {
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .create();

        assertNotEquals(delivery.getCourierId(), courierId);
        delivery.setCourierId(courierId);

        when(deliveryService.assignCourier(id, courierId))
                .thenThrow(NoDeliveryFoundException.class);

        DeliveryIdAssignPutRequest request = new DeliveryIdAssignPutRequest();
        request.setCourierId(courierId);


        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdAssignPut(id, request));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void assignCourierForbidden()
            throws NoDeliveryFoundException, AccessForbiddenException, BadArgumentException {
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .create();

        assertNotEquals(delivery.getCourierId(), courierId);
        delivery.setCourierId(courierId);

        when(deliveryService.assignCourier(id, courierId))
                .thenThrow(AccessForbiddenException.class);

        DeliveryIdAssignPutRequest request = new DeliveryIdAssignPutRequest();
        request.setCourierId(courierId);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdAssignPut(id, request));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void assignCourierServiceUnavailable()
            throws NoDeliveryFoundException, AccessForbiddenException, BadArgumentException {
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .create();

        assertNotEquals(delivery.getCourierId(), courierId);
        delivery.setCourierId(courierId);

        when(deliveryService.assignCourier(id, courierId))
                .thenThrow(RestClientException.class);

        DeliveryIdAssignPutRequest request = new DeliveryIdAssignPutRequest();
        request.setCourierId(courierId);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdAssignPut(id, request));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void assignCourierGenericException()
            throws NoDeliveryFoundException, AccessForbiddenException, BadArgumentException {
        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .create();

        assertNotEquals(delivery.getCourierId(), courierId);
        delivery.setCourierId(courierId);

        when(deliveryService.assignCourier(id, courierId))
                .thenAnswer(t -> {throw new Exception();});

        DeliveryIdAssignPutRequest request = new DeliveryIdAssignPutRequest();
        request.setCourierId(courierId);

        Exception exception = assertThrows(Exception.class,
                () -> deliveryController.deliveryIdAssignPut(id, request));
    }

    @Test
    void updateTotalDeliveryTimeSuccessfulTest() throws Exception {
        // The PUT request for updating the delivery time
        UUID deliveryId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(deliveryId)
                .create();

        when(deliveryService.addDeliveryTime(deliveryId, orderService, userService)).thenReturn(delivery);

        DeliveryIdDeliveryTimePostRequest deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest();

        ResponseEntity<Delivery> expected = ResponseEntity.ok(delivery);
        ResponseEntity<Delivery> actual = deliveryController.deliveryIdDeliveryTimePut(deliveryId, deliveryIdDeliveryTimePostRequest);
        assertEquals(expected, actual);
    }



    @Test
    void updateStatusSendEmailBadRequestTest()
            throws BadArgumentException, NoDeliveryFoundException, AccessForbiddenException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.PREPARING;
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();
        deliveryIdStatusPutRequest.setUserId(userId);
        deliveryIdStatusPutRequest.setStatus(status);

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setStatus(Delivery.StatusEnum.PREPARING)
                .create();

        when(deliveryService.updateStatus(
                id,deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus()))
                .thenReturn(delivery);

        when(deliveryService.sendEmail(deliveryIdStatusPutRequest.getStatus(), id)).thenThrow(BadArgumentException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdStatusPut(id, deliveryIdStatusPutRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void updateStatusSendEmailServiceUnavailableTest()
            throws BadArgumentException, NoDeliveryFoundException, AccessForbiddenException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.PREPARING;
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();
        deliveryIdStatusPutRequest.setUserId(userId);
        deliveryIdStatusPutRequest.setStatus(status);

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setStatus(Delivery.StatusEnum.PREPARING)
                .create();

        when(deliveryService.updateStatus(
                id,deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus()))
                .thenReturn(delivery);

        when(deliveryService.sendEmail(deliveryIdStatusPutRequest.getStatus(), id))
                .thenThrow(RestClientException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdStatusPut(id, deliveryIdStatusPutRequest));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void updateStatusSendEmailGenericExceptionTest()
            throws BadArgumentException, NoDeliveryFoundException, AccessForbiddenException {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.PREPARING;
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();
        deliveryIdStatusPutRequest.setUserId(userId);
        deliveryIdStatusPutRequest.setStatus(status);

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setStatus(Delivery.StatusEnum.PREPARING)
                .create();

        when(deliveryService.updateStatus(
                id,deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus()))
                .thenReturn(delivery);

        when(deliveryService.sendEmail(deliveryIdStatusPutRequest.getStatus(), id))
                .thenAnswer(t -> {throw new Exception();});

        Exception exception = assertThrows(Exception.class,
                () -> deliveryController.deliveryIdStatusPut(id, deliveryIdStatusPutRequest));
    }

    @Test
    void getDeliveriesInRadiusUnauthorized() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        BigDecimal radius = BigDecimal.ONE;
        Location location = new Location();
        UUID courierId = UUID.randomUUID();

        when(deliveryService.getAvailableDeliveries(radius,location,courierId)).thenThrow(AccessForbiddenException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryAvailableGet(radius,location,courierId));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void getDeliveriesInRadiusBadArgument() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        BigDecimal radius = BigDecimal.valueOf(-1);
        Location location = new Location();
        UUID courierId = UUID.randomUUID();

        when(deliveryService.getAvailableDeliveries(radius,location,courierId)).thenThrow(BadArgumentException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryAvailableGet(radius, location, courierId));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getDeliveriesInRadiusServiceUnavailable() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        BigDecimal radius = BigDecimal.valueOf(-1);
        Location location = new Location();
        UUID courierId = UUID.randomUUID();

        when(deliveryService.getAvailableDeliveries(radius,location,courierId)).thenThrow(ServiceUnavailableException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryAvailableGet(radius,location,courierId));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void getDeliveriesInRadiusGenericException() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        BigDecimal radius = BigDecimal.valueOf(-1);
        Location location = new Location();
        UUID courierId = UUID.randomUUID();

        when(deliveryService.getAvailableDeliveries(radius,location,courierId))
                .thenAnswer(t -> {throw new Exception();});

        assertThrows(Exception.class,
                () -> deliveryController.deliveryAvailableGet(radius,location,courierId));
    }


    @Test
    void getDeliveriesInRadiusSuccess() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        BigDecimal radius = BigDecimal.valueOf(10);
        Location location = new Location();
        UUID courierId = UUID.randomUUID();


        Delivery delivery1 = new Delivery();
        Delivery delivery2 = new Delivery();
        delivery1.setId(UUID.randomUUID());
        delivery2.setId(UUID.randomUUID());
        when(deliveryService.getAvailableDeliveries(radius,location,courierId)).thenReturn(List.of(delivery1,delivery2));

        ResponseEntity<List<Delivery>> expected = ResponseEntity.ok(List.of(delivery1,delivery2));

        assertEquals(expected, deliveryController.deliveryAvailableGet(radius,location,courierId));
    }

    @Test
    void updateTotalDeliveryTimeBadRequestTest() throws BadArgumentException, NoDeliveryFoundException {
        UUID deliveryId = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);

        when(deliveryService.addDeliveryTime(deliveryId, orderService, userService))
                .thenThrow(NoDeliveryFoundException.class);

        DeliveryIdDeliveryTimePostRequest deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdDeliveryTimePut(deliveryId, deliveryIdDeliveryTimePostRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void updateTotalDeliveryTimeServiceUnavailableTest() throws BadArgumentException, NoDeliveryFoundException {
        UUID deliveryId = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);

        when(deliveryService.addDeliveryTime(deliveryId, orderService, userService))
                .thenThrow(RestClientException.class);

        DeliveryIdDeliveryTimePostRequest deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> deliveryController.deliveryIdDeliveryTimePut(deliveryId, deliveryIdDeliveryTimePostRequest));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void updateTotalDeliveryTimeGenericExceptionTest() throws BadArgumentException, NoDeliveryFoundException {
        UUID deliveryId = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);

        when(deliveryService.addDeliveryTime(deliveryId, orderService, userService))
                .thenAnswer(t -> {throw new Exception();});

        DeliveryIdDeliveryTimePostRequest deliveryIdDeliveryTimePostRequest = new DeliveryIdDeliveryTimePostRequest();

        Exception exception = assertThrows(Exception.class,
                () -> deliveryController.deliveryIdDeliveryTimePut(deliveryId, deliveryIdDeliveryTimePostRequest));
    }

    @Test
    void handleArgumentMismatchTest() {
        HttpServletRequest request= mock(HttpServletRequest.class);
        String expectedMessage = "Received parameters have incorrect format or are incomplete.";
        when(request.getRequestURL()).thenReturn(new StringBuffer(expectedMessage));

        ResponseEntity<Error> expected = ResponseEntity.badRequest().body(new Error()
                .timestamp(OffsetDateTime.MIN)
                .timestamp(OffsetDateTime.MIN)
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(expectedMessage)
                .path(request.getRequestURI()));

        ResponseEntity<Error> actual = deliveryController.handleArgumentTypeMismatch(request);
        assertEquals(expected.getBody().getStatus(), actual.getBody().getStatus());
        assertEquals(expected.getBody().getError(), actual.getBody().getError());
        assertEquals(expected.getBody().getMessage(), actual.getBody().getMessage());
        assertEquals(expected.getBody().getPath(), actual.getBody().getPath());
    }

}