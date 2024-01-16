package nl.tudelft.sem.yumyumnow.delivery.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import nl.tudelft.sem.yumyumnow.delivery.api.AdminApi;
import nl.tudelft.sem.yumyumnow.delivery.api.DeliveryApi;
import nl.tudelft.sem.yumyumnow.delivery.application.services.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
public class AdminController implements AdminApi {
    private final DeliveryService deliveryService;
    private final AdminService adminService;

    /**
     * Constructor for admin controller.
     *
     * @param deliveryService delivery service for the logic
     * @param adminService admin service from User microservice
     */
    public AdminController(DeliveryService deliveryService,
                              AdminService adminService) {
        this.deliveryService = deliveryService;
        this.adminService = adminService;
    }

    /**
     * Get default maximum delivery zone as an admin.
     *
     * @param adminId The admin ID (required)
     * @return The response that contains admin id and default maximum zone.
     */
    @Override
    public ResponseEntity<AdminMaxZoneGet200Response> adminMaxZoneGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true)
            @Valid @RequestParam(value = "adminId", required = true) UUID adminId
    ) {
        try {
            AdminMaxZoneGet200Response response = adminService.adminGetMaxZone(adminId, adminService);
            if (response == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(response);
        } catch (ServiceUnavailableException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (AccessForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     *  Set default maximum delivery zone as an admin.
     *
     * @param adminMaxZoneGet200Response  (optional)
     * @return The response that contains admin id and default maximum zone.
     */
    @Override
    public ResponseEntity<AdminMaxZoneGet200Response> adminMaxZonePut(
            @Parameter(name = "AdminMaxZoneGet200Response", description = "")
            @Valid @RequestBody(required = false) AdminMaxZoneGet200Response adminMaxZoneGet200Response
    ) {
        try {
            AdminMaxZoneGet200Response response =
                    adminService.adminSetMaxZone(adminMaxZoneGet200Response.getAdminId(),
                            adminMaxZoneGet200Response.getRadiusKm(), adminService);

            if (response == null) {
                return ResponseEntity.badRequest().body(adminMaxZoneGet200Response);
            }
            return ResponseEntity.ok(response);
        } catch (ServiceUnavailableException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (AccessForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get analytics regarding the total number of deliveries.
     *
     * @param adminId The admin ID (required)
     * @param startDate Start date of the analytic. (required)
     * @param endDate End date of the analytic. (required)
     * @return a DeliveryAdminAnalyticsTotalDeliveriesGet200Response representing the total number of deliveries.
     */
    @Override
    public ResponseEntity<AdminAnalyticsTotalDeliveriesGet200Response> adminAnalyticsTotalDeliveriesGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true)
            @Valid @RequestParam(value = "adminId", required = true) UUID adminId,
            @NotNull @Parameter(name = "startDate", description = "Start date of the analytic.", required = true)
            @Valid @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @NotNull @Parameter(name = "endDate", description = "End date of the analytic.", required = true)
            @Valid @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        AdminAnalyticsTotalDeliveriesGet200Response response =
                new AdminAnalyticsTotalDeliveriesGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        try {
            int totalDeliveries = adminService.getTotalDeliveriesAnalytic(adminId, startDate, endDate);
            response.setTotalDeliveries(BigDecimal.valueOf(totalDeliveries));
        } catch (BadArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (AccessForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ServiceUnavailableException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get analytics regarding the total number of successful deliveries.
     *
     * @param adminId The admin ID (required)
     * @param startDate Start date of the analytic. (required)
     * @param endDate End date of the analytic. (required)
     * @return a DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response representing the total number of deliveries.
     */
    @Override
    public ResponseEntity<AdminAnalyticsSuccessfulDeliveriesGet200Response> adminAnalyticsSuccessfulDeliveriesGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true)
            @Valid @RequestParam(value = "adminId", required = true) UUID adminId,
            @NotNull @Parameter(name = "startDate", description = "Start date of the analytic.", required = true)
            @Valid @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @NotNull @Parameter(name = "endDate", description = "End date of the analytic.", required = true)
            @Valid @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        AdminAnalyticsSuccessfulDeliveriesGet200Response response =
                new AdminAnalyticsSuccessfulDeliveriesGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        try {
            int totalDeliveries = adminService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate);
            response.setSuccessfulDeliveries(BigDecimal.valueOf(totalDeliveries));
        } catch (BadArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (AccessForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ServiceUnavailableException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get analytics regarding the average preparation time of an order.
     *
     * @param adminId The admin ID (required)
     * @param startDate Start date of the analytic. (required)
     * @param endDate End date of the analytic. (required)
     * @return a DeliveryAdminAnalyticsPreparationTimeGet200Response response representing the average time
     */
    @Override
    public ResponseEntity<AdminAnalyticsPreparationTimeGet200Response> adminAnalyticsPreparationTimeGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true)
            @Valid @RequestParam(value = "adminId", required = true) UUID adminId,
            @NotNull @Parameter(name = "startDate", description = "Start date of the analytic.", required = true)
            @Valid @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @NotNull @Parameter(name = "endDate", description = "End date of the analytic.", required = true)
            @Valid @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        AdminAnalyticsPreparationTimeGet200Response response =
                new AdminAnalyticsPreparationTimeGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);

        try {
            long averagePreparationTime = adminService.getPreparationTimeAnalytic(adminId, startDate, endDate);
            response.setPreparationTime(BigDecimal.valueOf(averagePreparationTime));
        } catch (BadArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (AccessForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ServiceUnavailableException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get analytics regarding the average delivery time of an order.
     *
     * @param adminId The admin ID.
     * @param startDate Start date of the analytic.
     * @param endDate End date of the analytic.
     * @return a DeliveryAdminAnalyticsDeliveryTimeGet200Response response representing the average duration of a delivery
     */
    @Override
    public ResponseEntity<AdminAnalyticsDeliveryTimeGet200Response> adminAnalyticsDeliveryTimeGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true)
            @Valid @RequestParam(value = "adminId", required = true) UUID adminId,
            @NotNull @Parameter(name = "startDate", description = "Start date of the analytic.", required = true)
            @Valid @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @NotNull @Parameter(name = "endDate", description = "End date of the analytic.", required = true)
            @Valid @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        AdminAnalyticsDeliveryTimeGet200Response response = new AdminAnalyticsDeliveryTimeGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);

        try {
            long averageDeliveryTime = adminService.getDeliveryTimeAnalytic(adminId, startDate, endDate);
            response.setDeliveryTime(BigDecimal.valueOf(averageDeliveryTime));
        } catch (BadArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (AccessForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ServiceUnavailableException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        return ResponseEntity.ok(response);
    }

}
