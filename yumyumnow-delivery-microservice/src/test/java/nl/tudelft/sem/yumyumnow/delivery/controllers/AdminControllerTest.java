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
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        AdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new AdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(adminService.adminGetMaxZone(adminId,adminService)).thenReturn(deliveryAdminMaxZoneGet200Response);
        ResponseEntity<AdminMaxZoneGet200Response> response = adminController.adminMaxZoneGet(adminId);
        assertEquals(ResponseEntity.ok(deliveryAdminMaxZoneGet200Response), response);
    }

    @Test
    void adminMaxZoneGetFailTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(adminService.adminGetMaxZone(adminId,adminService)).thenThrow(RestClientException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminMaxZoneGet(adminId));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void adminMaxZoneGetServiceUnavailableTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(adminService.adminGetMaxZone(adminId,adminService)).thenThrow(new ServiceUnavailableException(""));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminMaxZoneGet(adminId));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void adminMaxZoneGetAccessForbiddenTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(adminService.adminGetMaxZone(adminId,adminService)).thenThrow(new AccessForbiddenException(""));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminMaxZoneGet(adminId));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void adminMaxZoneSetSuccessTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        AdminMaxZoneGet200Response adminMaxZoneGet200Response = new AdminMaxZoneGet200Response();
        adminMaxZoneGet200Response.setAdminId(adminId);
        adminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(adminService.adminSetMaxZone(adminId, defaultMaxZone, adminService)).thenReturn(adminMaxZoneGet200Response);
        ResponseEntity<AdminMaxZoneGet200Response> response = adminController.adminMaxZonePut(adminMaxZoneGet200Response);
        assertEquals(ResponseEntity.ok(adminMaxZoneGet200Response), response);
    }

    @Test
    void adminMaxZoneSetFailTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);
        AdminMaxZoneGet200Response adminMaxZoneGet200Response = new AdminMaxZoneGet200Response();
        adminMaxZoneGet200Response.setAdminId(adminId);
        adminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(adminService.adminSetMaxZone(adminId,defaultMaxZone,adminService)).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminMaxZonePut(adminMaxZoneGet200Response));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    void adminMaxZoneSetServiceUnavailableTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);
        AdminMaxZoneGet200Response adminMaxZoneGet200Response = new AdminMaxZoneGet200Response();
        adminMaxZoneGet200Response.setAdminId(adminId);
        adminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(adminService.adminSetMaxZone(adminId,defaultMaxZone,adminService)).thenThrow(new ServiceUnavailableException(""));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminMaxZonePut(adminMaxZoneGet200Response));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void adminMaxZoneSetAccessForbiddenTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);
        AdminMaxZoneGet200Response adminMaxZoneGet200Response = new AdminMaxZoneGet200Response();
        adminMaxZoneGet200Response.setAdminId(adminId);
        adminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        when(adminService.adminSetMaxZone(adminId,defaultMaxZone,adminService)).thenThrow(new AccessForbiddenException(""));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminMaxZonePut(adminMaxZoneGet200Response));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    // Analytics tests
    @Test
    void getTotalDeliveriesSuccessfulTest() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getTotalDeliveriesAnalytic(adminId, startDate, endDate)).thenReturn(1);
        AdminAnalyticsTotalDeliveriesGet200Response response = new AdminAnalyticsTotalDeliveriesGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setTotalDeliveries(BigDecimal.valueOf(1));

        ResponseEntity<AdminAnalyticsTotalDeliveriesGet200Response> expected = ResponseEntity.ok(response);
        ResponseEntity<AdminAnalyticsTotalDeliveriesGet200Response> actual = adminController.adminAnalyticsTotalDeliveriesGet(
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

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsTotalDeliveriesGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getTotalDeliveriesAccessExceptionTest() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getTotalDeliveriesAnalytic(adminId, startDate, endDate)).thenThrow(AccessForbiddenException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsTotalDeliveriesGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void getTotalDeliveriesServiceExceptionTest() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getTotalDeliveriesAnalytic(adminId, startDate, endDate)).thenThrow(ServiceUnavailableException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsTotalDeliveriesGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void getSuccessfulDeliveriesSuccessfulTest() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate)).thenReturn(1);
        AdminAnalyticsSuccessfulDeliveriesGet200Response response = new AdminAnalyticsSuccessfulDeliveriesGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setSuccessfulDeliveries(BigDecimal.valueOf(1));

        ResponseEntity<AdminAnalyticsSuccessfulDeliveriesGet200Response> expected = ResponseEntity.ok(response);
        ResponseEntity<AdminAnalyticsSuccessfulDeliveriesGet200Response> actual = adminController.adminAnalyticsSuccessfulDeliveriesGet(
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

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsSuccessfulDeliveriesGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getSuccessfulDeliveriesAccessExceptionTest() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate)).thenThrow(AccessForbiddenException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsSuccessfulDeliveriesGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void getSuccessfulDeliveriesServiceExceptionTest() throws Exception {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate)).thenThrow(ServiceUnavailableException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsSuccessfulDeliveriesGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void getPrepTimeSuccessfulTest() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getPreparationTimeAnalytic(adminId, startDate, endDate)).thenReturn(1L);

        AdminAnalyticsPreparationTimeGet200Response response = new AdminAnalyticsPreparationTimeGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setPreparationTime(BigDecimal.valueOf(1));

        ResponseEntity<AdminAnalyticsPreparationTimeGet200Response> expected = ResponseEntity.ok(response);
        ResponseEntity<AdminAnalyticsPreparationTimeGet200Response> actual = adminController.adminAnalyticsPreparationTimeGet(
                adminId, startDate, endDate
        );

        assertEquals(expected, actual);
    }
}
