package nl.tudelft.sem.yumyumnow.delivery.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import nl.tudelft.sem.yumyumnow.delivery.api.AdminApi;
import nl.tudelft.sem.yumyumnow.delivery.application.services.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import nl.tudelft.sem.yumyumnow.delivery.model.Error;
import org.springframework.core.NestedRuntimeException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class AdminController implements AdminApi {
    private final DeliveryService deliveryService;
    private final AdminService adminService;

    /**
     * Constructor for admin controller.
     *
     * @param deliveryService delivery service for the logic
     * @param adminService    admin service from User microservice
     */
    public AdminController(DeliveryService deliveryService,
                           AdminService adminService) {
        this.deliveryService = deliveryService;
        this.adminService = adminService;
    }

    /**
     * Handler for Spring exception which is thrown REST request parameters have wrong format.
     *
     * @param request HTTP Request causing an exception
     * @return Response with error
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, NullPointerException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Error> handleArgumentTypeMismatch(HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new Error()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Received parameters have incorrect format or are incomplete.")
                .path(request.getRequestURI()));
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
        AdminMaxZoneGet200Response response = null;
        try {
            response = adminService.adminGetMaxZone(adminId);
        } catch (ServiceUnavailableException | RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Server could not respond.");
        } catch (AccessForbiddenException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User has no right to get default max zone.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error.");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Set default maximum delivery zone as an admin.
     *
     * @param adminMaxZoneGet200Response (optional)
     * @return The response that contains admin id and default maximum zone.
     */
    @Override
    public ResponseEntity<AdminMaxZoneGet200Response> adminMaxZonePut(
            @Parameter(name = "AdminMaxZoneGet200Response", description = "")
            @Valid @RequestBody(required = false) AdminMaxZoneGet200Response adminMaxZoneGet200Response
    ) {
        AdminMaxZoneGet200Response response = null;
        try {
            response = adminService.adminSetMaxZone(adminMaxZoneGet200Response.getAdminId(),
                    adminMaxZoneGet200Response.getRadiusKm());
            if (response == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Max zone has to be positive.");
            }

        } catch (ServiceUnavailableException | RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Server could not respond.");
        } catch (AccessForbiddenException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User has no right to get default max zone.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error.");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get analytics regarding the total number of deliveries.
     *
     * @param adminId   The admin ID (required)
     * @param startDate Start date of the analytic. (required)
     * @param endDate   End date of the analytic. (required)
     * @return a AdminAnalyticsTotalDeliveriesGet200Response representing the total number of deliveries.
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start date cannot be greater than end date.");
        } catch (AccessForbiddenException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User has no right to get analytics.");
        } catch (ServiceUnavailableException | RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Server could not respond.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error.");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get analytics regarding the total number of successful deliveries.
     *
     * @param adminId   The admin ID (required)
     * @param startDate Start date of the analytic. (required)
     * @param endDate   End date of the analytic. (required)
     * @return a AdminAnalyticsSuccessfulDeliveriesGet200Response representing the total number of deliveries.
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start date cannot be greater than end date.");
        } catch (AccessForbiddenException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User has no right to get analytics.");
        } catch (ServiceUnavailableException | RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Server could not respond.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error.");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get analytics regarding the average preparation time of an order.
     *
     * @param adminId   The admin ID (required)
     * @param startDate Start date of the analytic. (required)
     * @param endDate   End date of the analytic. (required)
     * @return a AdminAnalyticsPreparationTimeGet200Response response representing the average time
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start date cannot be greater than end date.");
        } catch (AccessForbiddenException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User has no right to get analytics.");
        } catch (ServiceUnavailableException | RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Server could not respond.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error.");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get analytics regarding the average delivery time of an order.
     *
     * @param adminId   The admin ID.
     * @param startDate Start date of the analytic.
     * @param endDate   End date of the analytic.
     * @return a AdminAnalyticsDeliveryTimeGet200Response response representing the average duration of a delivery
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start date cannot be greater than end date.");
        } catch (AccessForbiddenException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User has no right to get analytics.");
        } catch (ServiceUnavailableException | RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Server could not respond.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error.");
        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AdminAnalyticsIssuesGet200Response> adminAnalyticsIssuesGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true) @Valid @RequestParam(value = "adminId", required = true) UUID adminId,
            @NotNull @Parameter(name = "startDate", description = "Start date of the analytic.", required = true) @Valid @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @NotNull @Parameter(name = "endDate", description = "End date of the analytic.", required = true) @Valid @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        try {
            List<String> encounteredIssues = adminService.getEncounteredIssues(adminId, startDate, endDate);
            return ResponseEntity.ok(new AdminAnalyticsIssuesGet200Response()
                    .startDate(startDate)
                    .endDate(endDate)
                    .encounteredIssues(encounteredIssues));
        } catch (BadArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start date cannot be greater than end date.");
        } catch (AccessForbiddenException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User has no right to get analytics.");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Server could not respond.");
        } catch (NestedRuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error.");
        }
    }

    /**
     * Get analytics regarding the average driver efficiency.
     *
     * @param adminId The admin ID (required)
     * @param startDate Start date of the analytic. (required)
     * @param endDate End date of the analytic. (required)
     * @return a AdminAnalyticsDriverEfficiencyGet200Response response representing the average driver efficiency
     */
    @Override
    public ResponseEntity<AdminAnalyticsDriverEfficiencyGet200Response> adminAnalyticsDriverEfficiencyGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true)
            @Valid @RequestParam(value = "adminId", required = true) UUID adminId,
            @NotNull @Parameter(name = "startDate", description = "Start date of the analytic.", required = true)
            @Valid @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @NotNull @Parameter(name = "endDate", description = "End date of the analytic.", required = true)
            @Valid @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        AdminAnalyticsDriverEfficiencyGet200Response response = new AdminAnalyticsDriverEfficiencyGet200Response();
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        try {
            long averageDriverEfficiency = adminService.getDriverEfficiencyAnalytic(adminId, startDate, endDate);
            response.setDriverEfficiency(BigDecimal.valueOf(averageDriverEfficiency));
        } catch (BadArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start date cannot be greater than end date.");
        } catch (AccessForbiddenException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User has no right to get analytics.");
        }  catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error.");
        }
        return ResponseEntity.ok(response);
    }
}