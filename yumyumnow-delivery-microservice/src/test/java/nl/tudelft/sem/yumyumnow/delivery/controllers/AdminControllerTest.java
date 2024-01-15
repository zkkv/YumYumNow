package nl.tudelft.sem.yumyumnow.delivery.controllers;

import nl.tudelft.sem.yumyumnow.delivery.application.services.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdminControllerTest {
    private DeliveryService deliveryService;
    private AdminController adminController;
    private AdminService adminService;

    @BeforeEach
    void setUp(){
        this.deliveryService = mock(DeliveryService.class);
        this.adminService = mock(AdminService.class);
        this.adminController = new AdminController(deliveryService, adminService);
    }

    @Test
    void adminMaxZoneGetSuccessTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(adminService.adminGetMaxZone(adminId,adminService)).thenReturn(deliveryAdminMaxZoneGet200Response);
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = adminController.deliveryAdminMaxZoneGet(adminId);
        assertEquals(ResponseEntity.ok(deliveryAdminMaxZoneGet200Response), response);
    }

    @Test
    void adminMaxZoneGetFailTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(adminService.adminGetMaxZone(adminId,adminService)).thenReturn(null);
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = adminController.deliveryAdminMaxZoneGet(adminId);
        assertEquals(ResponseEntity.badRequest().body(null), response);
    }

    @Test
    void adminMaxZoneGetServiceUnavailableTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(adminService.adminGetMaxZone(adminId,adminService)).thenThrow(new ServiceUnavailableException(""));
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = adminController.deliveryAdminMaxZoneGet(adminId);
        assertEquals(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE), response);
    }

    @Test
    void adminMaxZoneGetAccessForbiddenTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(adminService.adminGetMaxZone(adminId,adminService)).thenThrow(new AccessForbiddenException(""));
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = adminController.deliveryAdminMaxZoneGet(adminId);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), response);
    }

    @Test
    void adminMaxZoneSetSuccessTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(adminService.adminSetMaxZone(adminId, defaultMaxZone, adminService)).thenReturn(deliveryAdminMaxZoneGet200Response);
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = adminController.deliveryAdminMaxZonePut(deliveryAdminMaxZoneGet200Response);
        assertEquals(ResponseEntity.ok(deliveryAdminMaxZoneGet200Response), response);
    }

    @Test
    void adminMaxZoneSetFailTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);
        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(adminService.adminSetMaxZone(adminId,defaultMaxZone,adminService)).thenReturn(null);
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = adminController.deliveryAdminMaxZonePut(deliveryAdminMaxZoneGet200Response);
        assertEquals(ResponseEntity.badRequest().body(deliveryAdminMaxZoneGet200Response), response);
    }

    @Test
    void adminMaxZoneSetServiceUnavailableTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);
        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(adminService.adminSetMaxZone(adminId,defaultMaxZone,adminService)).thenThrow(new ServiceUnavailableException(""));
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = adminController.deliveryAdminMaxZonePut(deliveryAdminMaxZoneGet200Response);
        assertEquals(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE), response);
    }

    @Test
    void adminMaxZoneSetAccessForbiddenTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);
        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(adminService.adminSetMaxZone(adminId,defaultMaxZone,adminService)).thenThrow(new AccessForbiddenException(""));
        ResponseEntity<DeliveryAdminMaxZoneGet200Response> response = adminController.deliveryAdminMaxZonePut(deliveryAdminMaxZoneGet200Response);
        assertEquals(new ResponseEntity<>(HttpStatus.FORBIDDEN), response);
    }

    // Analytics tests
    @Test
    void getTotalDeliveriesSuccessfulTest() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getTotalDeliveriesAnalytic(adminId, startDate, endDate)).thenReturn(1);
        DeliveryAdminAnalyticsTotalDeliveriesGet200Response response = new DeliveryAdminAnalyticsTotalDeliveriesGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setTotalDeliveries(BigDecimal.valueOf(1));

        ResponseEntity<DeliveryAdminAnalyticsTotalDeliveriesGet200Response> expected = ResponseEntity.ok(response);
        ResponseEntity<DeliveryAdminAnalyticsTotalDeliveriesGet200Response> actual = adminController.deliveryAdminAnalyticsTotalDeliveriesGet(
                adminId, startDate, endDate
        );

        assertEquals(expected, actual);
    }

    @Test
    void getTotalDeliveriesBadArgumentExceptionTest() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getTotalDeliveriesAnalytic(adminId, startDate, endDate)).thenThrow(BadArgumentException.class);

        ResponseEntity<DeliveryAdminAnalyticsTotalDeliveriesGet200Response> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ResponseEntity<DeliveryAdminAnalyticsTotalDeliveriesGet200Response> actual = adminController.deliveryAdminAnalyticsTotalDeliveriesGet(
                adminId, startDate, endDate
        );

        assertEquals(expected, actual);
    }

    @Test
    void getTotalDeliveriesAccessExceptionTest() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getTotalDeliveriesAnalytic(adminId, startDate, endDate)).thenThrow(AccessForbiddenException.class);

        ResponseEntity<DeliveryAdminAnalyticsTotalDeliveriesGet200Response> expected = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        ResponseEntity<DeliveryAdminAnalyticsTotalDeliveriesGet200Response> actual = adminController.deliveryAdminAnalyticsTotalDeliveriesGet(
                adminId, startDate, endDate
        );

        assertEquals(expected, actual);
    }

    @Test
    void getTotalDeliveriesServiceExceptionTest() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getTotalDeliveriesAnalytic(adminId, startDate, endDate)).thenThrow(ServiceUnavailableException.class);

        ResponseEntity<DeliveryAdminAnalyticsTotalDeliveriesGet200Response> expected = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        ResponseEntity<DeliveryAdminAnalyticsTotalDeliveriesGet200Response> actual = adminController.deliveryAdminAnalyticsTotalDeliveriesGet(
                adminId, startDate, endDate
        );

        assertEquals(expected, actual);
    }

    @Test
    void getSuccessfulDeliveriesSuccessfulTest() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate)).thenReturn(1);
        DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response response = new DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setSuccessfulDeliveries(BigDecimal.valueOf(1));

        ResponseEntity<DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response> expected = ResponseEntity.ok(response);
        ResponseEntity<DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response> actual = adminController.deliveryAdminAnalyticsSuccessfulDeliveriesGet(
                adminId, startDate, endDate
        );

        assertEquals(expected, actual);
    }

    @Test
    void getSuccessfulDeliveriesExceptionTest() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate)).thenThrow(BadArgumentException.class);

        ResponseEntity<DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response> expected = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ResponseEntity<DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response> actual = adminController.deliveryAdminAnalyticsSuccessfulDeliveriesGet(
                adminId, startDate, endDate
        );

        assertEquals(expected, actual);
    }

    @Test
    void getSuccessfulDeliveriesAccessExceptionTest() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate)).thenThrow(AccessForbiddenException.class);

        ResponseEntity<DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response> expected = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        ResponseEntity<DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response> actual = adminController.deliveryAdminAnalyticsSuccessfulDeliveriesGet(
                adminId, startDate, endDate
        );

        assertEquals(expected, actual);
    }

    @Test
    void getSuccessfulDeliveriesServiceExceptionTest() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate)).thenThrow(ServiceUnavailableException.class);

        ResponseEntity<DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response> expected = new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        ResponseEntity<DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response> actual = adminController.deliveryAdminAnalyticsSuccessfulDeliveriesGet(
                adminId, startDate, endDate
        );

        assertEquals(expected, actual);
    }

    @Test
    void getPrepTimeSuccessfulTest() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getPreparationTimeAnalytic(adminId, startDate, endDate)).thenReturn(1L);

        DeliveryAdminAnalyticsPreparationTimeGet200Response response = new DeliveryAdminAnalyticsPreparationTimeGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setPreparationTime(BigDecimal.valueOf(1));

        ResponseEntity<DeliveryAdminAnalyticsPreparationTimeGet200Response> expected = ResponseEntity.ok(response);
        ResponseEntity<DeliveryAdminAnalyticsPreparationTimeGet200Response> actual = adminController.deliveryAdminAnalyticsPreparationTimeGet(
                adminId, startDate, endDate
        );

        assertEquals(expected, actual);
    }
}
