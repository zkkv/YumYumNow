package nl.tudelft.sem.yumyumnow.delivery.controllers;

import nl.tudelft.sem.yumyumnow.delivery.application.services.AdminService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.DeliveryService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.UserService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryAdminMaxZoneGet200Response;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryVendorIdMaxZonePutRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


class DeliveryControllerTest {

    private DeliveryService deliveryService;
    private DeliveryController deliveryController;
    private UserService userService;
    private VendorService vendorService;
    private AdminService adminService;


    @BeforeEach
    void setUp(){
        this.deliveryService = mock(DeliveryService.class);
        this.userService = mock(UserService.class);
        this.vendorService = mock(VendorService.class);
        this.adminService = mock(AdminService.class);
        this.deliveryController = new DeliveryController(deliveryService, userService, vendorService, adminService);
    }

    @Test
    void updateStatusFail() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.PENDING;
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();
        deliveryIdStatusPutRequest.setUserId(userId);
        deliveryIdStatusPutRequest.setStatus(status);

        when(deliveryService.updateStatus(id,deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus())).thenReturn(null);

        ResponseEntity<Delivery> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ResponseEntity<Delivery> actual = deliveryController.deliveryIdStatusPut(id, deliveryIdStatusPutRequest);

        assertEquals(expected, actual);
    }

    @Test
    void updateStatusSuccess() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeliveryIdStatusPutRequest.StatusEnum status = DeliveryIdStatusPutRequest.StatusEnum.PREPARING;
        DeliveryIdStatusPutRequest deliveryIdStatusPutRequest = new DeliveryIdStatusPutRequest();
        deliveryIdStatusPutRequest.setUserId(userId);
        deliveryIdStatusPutRequest.setStatus(status);

        Delivery delivery = new Delivery();
        delivery.setStatus(Delivery.StatusEnum.PREPARING);
        delivery.setId(id);

        when(deliveryService.updateStatus(id,deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus())).thenReturn(delivery);

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
    void adminMaxZoneGetSuccessTest(){
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
    void adminMaxZoneGetFailTest(){
        UUID adminId = UUID.randomUUID();

        when(deliveryService.adminGetMaxZone(adminId,adminService)).thenReturn(null);
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = deliveryController.deliveryAdminMaxZoneGet(adminId);
        assertEquals(ResponseEntity.badRequest().body(null), response);
    }

    @Test
    void adminMaxZoneSetSuccessTest(){
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(deliveryService.adminSetMaxZone(adminId, defaultMaxZone)).thenReturn(deliveryAdminMaxZoneGet200Response);
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = deliveryController.deliveryAdminMaxZonePut(deliveryAdminMaxZoneGet200Response);
        assertEquals(ResponseEntity.ok(deliveryAdminMaxZoneGet200Response), response);
    }

    @Test
    void adminMaxZoneSetFailTest(){
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);
        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(deliveryService.adminSetMaxZone(adminId,defaultMaxZone)).thenReturn(null);
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = deliveryController.deliveryAdminMaxZonePut(deliveryAdminMaxZoneGet200Response);
        assertEquals(ResponseEntity.badRequest().body(deliveryAdminMaxZoneGet200Response), response);
    }
}