package nl.tudelft.sem.yumyumnow.delivery.controllers;

import io.swagger.v3.oas.annotations.Parameter;
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
public class AdminController implements DeliveryApi {
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
    public ResponseEntity<DeliveryAdminMaxZoneGet200Response> deliveryAdminMaxZoneGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true)
            @Valid @RequestParam(value = "adminId", required = true) UUID adminId
    ) {
        try {
            DeliveryAdminMaxZoneGet200Response response = adminService.adminGetMaxZone(adminId, adminService);
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
     * @param deliveryAdminMaxZoneGet200Response  (optional)
     * @return The response that contains admin id and default maximum zone.
     */
    @Override
    public ResponseEntity<DeliveryAdminMaxZoneGet200Response> deliveryAdminMaxZonePut(
            @Parameter(name = "DeliveryAdminMaxZoneGet200Response", description = "")
            @Valid @RequestBody(required = false) DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response
    ) {
        try {
            DeliveryAdminMaxZoneGet200Response response =
                    adminService.adminSetMaxZone(deliveryAdminMaxZoneGet200Response.getAdminId(),
                            deliveryAdminMaxZoneGet200Response.getRadiusKm(), adminService);

            if (response == null) {
                return ResponseEntity.badRequest().body(deliveryAdminMaxZoneGet200Response);
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
    public ResponseEntity<DeliveryAdminAnalyticsTotalDeliveriesGet200Response> deliveryAdminAnalyticsTotalDeliveriesGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true)
            @Valid @RequestParam(value = "adminId", required = true) UUID adminId,
            @NotNull @Parameter(name = "startDate", description = "Start date of the analytic.", required = true)
            @Valid @RequestParam(value = "startDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @NotNull @Parameter(name = "endDate", description = "End date of the analytic.", required = true)
            @Valid @RequestParam(value = "endDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        DeliveryAdminAnalyticsTotalDeliveriesGet200Response response =
                new DeliveryAdminAnalyticsTotalDeliveriesGet200Response();
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
    public ResponseEntity<DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response> deliveryAdminAnalyticsSuccessfulDeliveriesGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true)
            @Valid @RequestParam(value = "adminId", required = true) UUID adminId,
            @NotNull @Parameter(name = "startDate", description = "Start date of the analytic.", required = true)
            @Valid @RequestParam(value = "startDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @NotNull @Parameter(name = "endDate", description = "End date of the analytic.", required = true)
            @Valid @RequestParam(value = "endDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response response =
                new DeliveryAdminAnalyticsSuccessfulDeliveriesGet200Response();
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
    public ResponseEntity<DeliveryAdminAnalyticsPreparationTimeGet200Response> deliveryAdminAnalyticsPreparationTimeGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true)
            @Valid @RequestParam(value = "adminId", required = true) UUID adminId,
            @NotNull @Parameter(name = "startDate", description = "Start date of the analytic.", required = true)
            @Valid @RequestParam(value = "startDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @NotNull @Parameter(name = "endDate", description = "End date of the analytic.", required = true)
            @Valid @RequestParam(value = "endDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        DeliveryAdminAnalyticsPreparationTimeGet200Response response =
                new DeliveryAdminAnalyticsPreparationTimeGet200Response();
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
    public ResponseEntity<DeliveryAdminAnalyticsDeliveryTimeGet200Response> deliveryAdminAnalyticsDeliveryTimeGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true)
            @Valid @RequestParam(value = "adminId", required = true) UUID adminId,
            @NotNull @Parameter(name = "startDate", description = "Start date of the analytic.", required = true)
            @Valid @RequestParam(value = "startDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @NotNull @Parameter(name = "endDate", description = "End date of the analytic.", required = true)
            @Valid @RequestParam(value = "endDate", required = true)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        DeliveryAdminAnalyticsDeliveryTimeGet200Response response = new DeliveryAdminAnalyticsDeliveryTimeGet200Response();
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
