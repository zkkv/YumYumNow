package nl.tudelft.sem.yumyumnow.delivery.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import nl.tudelft.sem.yumyumnow.delivery.api.DeliveryApi;
import nl.tudelft.sem.yumyumnow.delivery.application.services.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.NoDeliveryFoundException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
public class DeliveryController implements DeliveryApi {
    private final DeliveryService deliveryService;
    private final CustomerService userService;
    private final VendorService vendorService;
    private final AdminService adminService;

    private final EmailService emailService;
    private final OrderService orderService;

    /**
     * Constructor for delivery controller.
     *
     * @param deliveryService delivery service for the logic
     * @param userService customer service from User microservice
     * @param vendorService vendor service from User microservice
     * @param adminService admin service from User microservice
     * @param orderService order service
     * @param emailService email service
     */
    public DeliveryController(DeliveryService deliveryService,
                              CustomerService userService,
                              VendorService vendorService,
                              AdminService adminService,
                              OrderService orderService,
                              EmailService emailService) {
        this.deliveryService = deliveryService;
        this.userService = userService;
        this.vendorService = vendorService;
        this.adminService = adminService;
        this.orderService = orderService;
        this.emailService = emailService;
    }

    /**
     * Create a delivery based on order data.
     *
     * @param order The order object containing the order ID to which the
     *              delivery corresponds (UUID) and the vendor ID to which the
     *              delivery corresponds (UUID).
     * @return The created delivery.
     */
    @Override
    public ResponseEntity<Delivery> deliveryPost(
            @Parameter(name = "Order", description = "")
            @Valid @RequestBody DeliveryPostRequest order) {

        Delivery delivery = null;
        try {
            delivery = deliveryService.createDelivery(order.getOrderId(),
                    order.getVendorId());
        } catch (BadArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(delivery);
    }

    /**
     * Returns a delivery based on its {@code id} or BAD REQUEST if no delivery was found.
     *
     * @param id UUID of the delivery (required)
     * @return the delivery object and OK, or BAD REQUEST if no delivery was found.
     * @author Kirill Zhankov
     */
    @Override
    public ResponseEntity<Delivery> deliveryIdGet(
            @Parameter(name = "id", description = "UUID of the delivery", required = true)
            @PathVariable("id") UUID id
    ) {
        Delivery delivery = null;
        try {
            delivery = deliveryService.getDelivery(id);
        } catch (NoDeliveryFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(delivery);
    }


    /**
     * Add the estimated time to a delivery.
     *
     * @param id UUID of the delivery (required)
     * @param deliveryIdDeliveryTimePostRequest  (optional)
     * @return the updated delivery
     */
    @Override
    public ResponseEntity<Delivery> deliveryIdPrepTimePost(
            @Parameter(name = "id", description = "UUID of the delivery", required = true) @PathVariable("id") UUID id,
            @Parameter(name = "DeliveryIdDeliveryTimePostRequest1", description = "")
            @Valid @RequestBody(required = false) DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest
    ) {
        Delivery delivery = deliveryService.changePrepTime(
                id, deliveryIdDeliveryTimePostRequest.getUserId(),
                deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime()
        );

        if (delivery == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(delivery);
    }


    /**
     * Change the status of the delivery.
     *
     * @param id UUID of the delivery (required)
     * @param deliveryIdStatusPutRequest  (optional)
     * @return the updated delivery
     */
    public ResponseEntity<Delivery> deliveryIdStatusPut(
            @Parameter(name = "id", description = "UUID of the delivery", required = true) @PathVariable("id") UUID id,
            @Parameter(name = "DeliveryIdStatusPutRequest", description = "")
            @Valid @RequestBody(required = false) DeliveryIdStatusPutRequest deliveryIdStatusPutRequest
    ) {
        Delivery delivery = null;
        try {
            delivery = deliveryService.updateStatus(id, deliveryIdStatusPutRequest.getUserId(),
                    deliveryIdStatusPutRequest.getStatus());
        } catch (NoDeliveryFoundException | BadArgumentException | AccessForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            deliveryService.sendEmail(deliveryIdStatusPutRequest.getStatus(), id);
        } catch (BadArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(delivery);
    }

    /**
     * Update the estimated time of a delivery.
     *
     * @param id UUID of the delivery (required)
     * @param deliveryIdDeliveryTimePostRequest1  (optional)
     * @return the updated delivery
     */
    @Override
    public ResponseEntity<Delivery> deliveryIdPrepTimePut(
            @Parameter(name = "id", description = "UUID of the delivery", required = true) @PathVariable("id") UUID id,
            @Parameter(name = "DeliveryIdDeliveryTimePostRequest1", description = "")
            @Valid @RequestBody(required = false) DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest1
    ) {
        Delivery delivery = deliveryService.changePrepTime(id, deliveryIdDeliveryTimePostRequest1.getUserId(),
                deliveryIdDeliveryTimePostRequest1.getEstimatedNewDeliveryTime());
        if (delivery == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(delivery);
    }


    /**
     * Update the maximum delivery zone of a vendor.
     *
     * @param id UUID of the vendor
     * @param deliveryVendorIdMaxZonePutRequest (contains the vendor to update and a new maximum delivery zone)
     * @return the vendorID with its updated maximum delivery zone
     */
    @Override
    public ResponseEntity<DeliveryVendorIdMaxZonePutRequest> deliveryVendorIdMaxZonePut(
            @Parameter(name = "id", description = "UUID of the vendor", required = true, in = ParameterIn.PATH)
            @PathVariable("id") UUID id,
            @Parameter(name = "DeliveryVendorIdMaxZonePutRequest", description = "")
            @Valid @RequestBody(required = false) DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest
    ) {
        DeliveryVendorIdMaxZonePutRequest response = deliveryService.vendorMaxZone(id,
                deliveryVendorIdMaxZonePutRequest, vendorService);

        if (response == null) {
            return ResponseEntity.badRequest().body(deliveryVendorIdMaxZonePutRequest);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Update the total delivery time of an order for a POST request.
     *
     * @param id UUID of the delivery (required).
     * @param deliveryIdDeliveryTimePostRequest1  (optional)/
     * @return a Delivery ResponseEntity representing the updated delivery.
     */
    @Override
    public ResponseEntity<Delivery> deliveryIdDeliveryTimePost(
            @Parameter(name = "id", description = "UUID of the delivery", required = true)
            @PathVariable("id") UUID id,
            @Parameter(name = "DeliveryIdDeliveryTimePostRequest1", description = "")
            @Valid @RequestBody(required = false) DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest1
    ) {
        Delivery delivery;
        try {
            delivery = deliveryService.addDeliveryTime(id, orderService, userService);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(delivery);
    }

    /**
     * Assigns courier with id provided as a query parameter to the delivery
     * with the given {@code id}.
     *
     * @param id UUID of the delivery (required)
     * @param deliveryIdAssignPutRequest request containing the courier id query parameter
     * @return a Delivery ResponseEntity representing the updated delivery
     * @author Kirill Zhankov
     */
    @Override
    public ResponseEntity<Delivery> deliveryIdAssignPut(
            @Parameter(name = "id", description = "UUID of the delivery", required = true)
            @PathVariable("id") UUID id,
            @Parameter(name = "DeliveryIdAssignPutRequest", description = "") @Valid
            @RequestBody(required = false) DeliveryIdAssignPutRequest deliveryIdAssignPutRequest
    ) {
        UUID courierId = deliveryIdAssignPutRequest.getCourierId();
        Delivery delivery = null;
        try {
            delivery = deliveryService.assignCourier(id, courierId);
        } catch (NoDeliveryFoundException | BadArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (AccessForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(delivery);
    }

    /**
     * Update the total delivery time of an order for PUT request.
     *
     * @param id UUID of the delivery (required).
     * @param deliveryIdDeliveryTimePostRequest  (optional).
     * @return a Delivery ResponseEntity representing the updated delivery.
     */
    @Override
    public ResponseEntity<Delivery> deliveryIdDeliveryTimePut(
            @Parameter(name = "id", description = "UUID of the delivery", required = true)
            @PathVariable("id") UUID id,
            @Parameter(name = "DeliveryIdDeliveryTimePostRequest", description = "")
            @Valid @RequestBody(required = false) DeliveryIdDeliveryTimePostRequest deliveryIdDeliveryTimePostRequest
    ) {
        Delivery delivery;
        try {
            delivery = deliveryService.addDeliveryTime(id, orderService, userService);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(delivery);
    }

    /**
     * Get all deliveries available for a courier to accept.
     *
     * @param radius The maximum distance in kilometers (required)
     * @param location The location for which the distances are calculated (required)
     * @param courierId The courier ID (required)
     * @return the list of said deliveries ordered by distance from the courier (the shortest first)
     */
    @Override
    public ResponseEntity<List<Delivery>> deliveryAvailableGet(
            @NotNull @Parameter(name = "radius", description = "The maximum distance in kilometers", required = true)
            @Valid @RequestParam(value = "radius", required = true) BigDecimal radius,
            @NotNull @Parameter(name = "location", description = "The location for which the distances are calculated",
                    required = true) @Valid Location location,
            @NotNull @Parameter(name = "courierId", description = "The courier ID", required = true)
            @Valid @RequestParam(value = "courierId", required = true) UUID courierId
    ) {
        List<Delivery> deliveries;
        try {
            deliveries = deliveryService.getAvailableDeliveries(radius, location, courierId);
        } catch (BadArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (AccessForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ServiceUnavailableException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        return ResponseEntity.ok(deliveries);
    }
}
