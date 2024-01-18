package nl.tudelft.sem.yumyumnow.delivery.controllers;

import nl.tudelft.sem.yumyumnow.delivery.application.services.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import nl.tudelft.sem.yumyumnow.delivery.model.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
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

        when(adminService.adminGetMaxZone(adminId)).thenReturn(deliveryAdminMaxZoneGet200Response);
        ResponseEntity<AdminMaxZoneGet200Response> response = adminController.adminMaxZoneGet(adminId);
        assertEquals(ResponseEntity.ok(deliveryAdminMaxZoneGet200Response), response);
    }

    @Test
    void adminMaxZoneGetFailTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(adminService.adminGetMaxZone(adminId)).thenThrow(RestClientException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminMaxZoneGet(adminId));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void adminMaxZoneGetServiceUnavailableTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(adminService.adminGetMaxZone(adminId)).thenThrow(new ServiceUnavailableException(""));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminMaxZoneGet(adminId));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void adminMaxZoneGetGenericExceptionTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(adminService.adminGetMaxZone(adminId))
                .thenAnswer(t -> {throw new Exception();});
        assertThrows(ResponseStatusException.class, () -> adminController.adminMaxZoneGet(adminId));
    }

    @Test
    void adminMaxZoneGetAccessForbiddenTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        when(adminService.adminGetMaxZone(adminId)).thenThrow(new AccessForbiddenException(""));

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

        when(adminService.adminSetMaxZone(adminId, defaultMaxZone)).thenReturn(adminMaxZoneGet200Response);
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

        when(adminService.adminSetMaxZone(adminId,defaultMaxZone)).thenReturn(null);

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

        when(adminService.adminSetMaxZone(adminId,defaultMaxZone)).thenThrow(new ServiceUnavailableException(""));

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

        when(adminService.adminSetMaxZone(adminId,defaultMaxZone)).thenThrow(new AccessForbiddenException(""));

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
    void getTotalDeliveriesGenericExceptionTest()
            throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getTotalDeliveriesAnalytic(adminId, startDate, endDate))
                .thenAnswer(t -> {throw new Exception();});
        assertThrows(ResponseStatusException.class, () ->
                adminController.adminAnalyticsTotalDeliveriesGet(adminId, startDate, endDate));
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
    void getSuccessfulDeliveriesBadArgumentTest() throws Exception {
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
    void getSuccessfulDeliveriesGenericExceptionTest()
            throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate))
                .thenAnswer(t -> {throw new Exception();});
        assertThrows(ResponseStatusException.class, () ->
                adminController.adminAnalyticsSuccessfulDeliveriesGet(adminId, startDate, endDate));
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

    @Test
    void getPrepTimeBadRequestTest()
            throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getPreparationTimeAnalytic(adminId, startDate, endDate))
                .thenThrow(BadArgumentException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsPreparationTimeGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getPrepTimeForbiddenTest()
            throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getPreparationTimeAnalytic(adminId, startDate, endDate))
                .thenThrow(AccessForbiddenException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsPreparationTimeGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void getPrepTimeServiceUnavailableTest()
            throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getPreparationTimeAnalytic(adminId, startDate, endDate))
                .thenThrow(ServiceUnavailableException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsPreparationTimeGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void getPrepTimeGenericExceptionTest()
            throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getPreparationTimeAnalytic(adminId, startDate, endDate))
                .thenAnswer(t -> {throw new Exception();});

        assertThrows(Exception.class,
                () -> adminController.adminAnalyticsPreparationTimeGet(adminId, startDate, endDate));
    }

    @Test
    void getDeliveryTimeSuccessfulTest()
            throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getDeliveryTimeAnalytic(adminId, startDate, endDate)).thenReturn(1L);

        AdminAnalyticsDeliveryTimeGet200Response response = new AdminAnalyticsDeliveryTimeGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setDeliveryTime(BigDecimal.valueOf(1));

        ResponseEntity<AdminAnalyticsDeliveryTimeGet200Response> expected = ResponseEntity.ok(response);
        ResponseEntity<AdminAnalyticsDeliveryTimeGet200Response> actual = adminController.adminAnalyticsDeliveryTimeGet(
                adminId, startDate, endDate
        );

        assertEquals(expected, actual);
    }

    @Test
    void getDeliveryTimeBadRequestTest()
            throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getDeliveryTimeAnalytic(adminId, startDate, endDate))
                .thenThrow(BadArgumentException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsDeliveryTimeGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getDeliveryTimeForbiddenTest()
            throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getDeliveryTimeAnalytic(adminId, startDate, endDate))
                .thenThrow(AccessForbiddenException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsDeliveryTimeGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void getDeliveryTimeServiceUnavailableTest()
            throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getDeliveryTimeAnalytic(adminId, startDate, endDate))
                .thenThrow(ServiceUnavailableException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsDeliveryTimeGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void getDeliveryTimeGenericExceptionTest()
            throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getDeliveryTimeAnalytic(adminId, startDate, endDate))
                .thenAnswer(t -> {throw new Exception();});

        assertThrows(Exception.class,
                () -> adminController.adminAnalyticsDeliveryTimeGet(adminId, startDate, endDate));
    }

    @Test
    void handleArgumentMismatchTest() {
        HttpServletRequest request= mock(HttpServletRequest.class);
        String expectedMessage = "Received parameters have incorrect format or are incomplete.";
        when(request.getRequestURL()).thenReturn(new StringBuffer(expectedMessage));

        ResponseEntity<Error> expected = ResponseEntity.badRequest().body(new Error()
                .timestamp(OffsetDateTime.MIN)
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(expectedMessage)
                .path(request.getRequestURI()));

        ResponseEntity<Error> actual = adminController.handleArgumentTypeMismatch(request);
        assertEquals(expected.getBody().getStatus(), actual.getBody().getStatus());
        assertEquals(expected.getBody().getError(), actual.getBody().getError());
        assertEquals(expected.getBody().getMessage(), actual.getBody().getMessage());
        assertEquals(expected.getBody().getPath(), actual.getBody().getPath());
    }

    @Test
    void encounteredIssuesGetSuccessTest() throws AccessForbiddenException, BadArgumentException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 2, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        List<String> encounteredIssues = List.of("Issue 1", "Issue 2");

        when(adminService.getEncounteredIssues(adminId, startDate, endDate)).thenReturn(encounteredIssues);

        AdminAnalyticsIssuesGet200Response response = new AdminAnalyticsIssuesGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setEncounteredIssues(encounteredIssues);

        ResponseEntity<AdminAnalyticsIssuesGet200Response> expected = ResponseEntity.ok(response);

        ResponseEntity<AdminAnalyticsIssuesGet200Response> actual = adminController.adminAnalyticsIssuesGet(adminId, startDate, endDate);

        assertEquals(expected, actual);
    }

    @Test
    void encounteredIssuesGetBadArgumentExceptionTest() throws AccessForbiddenException, BadArgumentException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 2, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getEncounteredIssues(adminId, startDate, endDate)).thenThrow(BadArgumentException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsIssuesGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void encounteredIssuesGetAccessForbiddenExceptionTest() throws AccessForbiddenException, BadArgumentException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 2, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getEncounteredIssues(adminId, startDate, endDate)).thenThrow(AccessForbiddenException.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> adminController.adminAnalyticsIssuesGet(adminId, startDate, endDate));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void encounteredIssuesGetGenericExceptionTest() throws AccessForbiddenException, BadArgumentException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();

        when(adminService.getEncounteredIssues(adminId, startDate, null)).thenAnswer(t -> {throw new Exception();});

        assertThrows(Exception.class,
                () -> adminController.adminAnalyticsIssuesGet(adminId, startDate, null));
    }

}
