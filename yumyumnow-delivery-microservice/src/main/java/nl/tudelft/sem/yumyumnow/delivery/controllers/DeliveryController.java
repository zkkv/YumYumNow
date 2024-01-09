package nl.tudelft.sem.yumyumnow.delivery.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import nl.tudelft.sem.yumyumnow.delivery.api.DeliveryApi;
import nl.tudelft.sem.yumyumnow.delivery.application.services.AdminService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.OrderService;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.NoDeliveryFoundException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import nl.tudelft.sem.yumyumnow.delivery.application.services.CustomerService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.DeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
public class DeliveryController implements DeliveryApi {
    private final DeliveryService deliveryService;
    private final CustomerService userService;
    private final VendorService vendorService;
    private final AdminService adminService;
    private final OrderService orderService;

    /**
     * Constructor for delivery controller.
     *
     * @param deliveryService delivery service for the logic
     * @param userService customer service from User microservice
     * @param vendorService vendor service from User microservice
     * @param adminService admin service from User microservice
     */
    public DeliveryController(DeliveryService deliveryService, CustomerService userService,
                              VendorService vendorService, AdminService adminService,
                              OrderService orderService) {
        this.deliveryService = deliveryService;
        this.userService = userService;
        this.vendorService = vendorService;
        this.adminService = adminService;
        this.orderService = orderService;
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

        Delivery delivery = deliveryService.createDelivery(order.getOrderId(), order.getVendorId());

        return ResponseEntity.ok(delivery);
    }


    /**
     * Add the estimated time to a delivery
     *
     * @param id UUID of the delivery (required)
     * @param deliveryIdDeliveryTimePostRequest  (optional)
     * @return the updated delivery
     */
    @Override
    public ResponseEntity<Delivery> deliveryIdPrepTimePost(
            @Parameter(name = "id", description = "UUID of the delivery", required = true) @PathVariable("id") UUID id,
            @Parameter(name = "DeliveryIdDeliveryTimePostRequest1", description = "") @Valid @RequestBody(required = false) DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest
    ){
        Delivery delivery = deliveryService.changePrepTime(
                id, deliveryIdDeliveryTimePostRequest.getUserId(),
                deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime()
        );

        if (delivery == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(delivery);
    }


    /**
     * Change the status of the delivery
     *
     * @param id UUID of the delivery (required)
     * @param deliveryIdStatusPutRequest  (optional)
     * @return the updated delivery
     */
    public ResponseEntity<Delivery> deliveryIdStatusPut(
            @Parameter(name = "id", description = "UUID of the delivery", required = true) @PathVariable("id") UUID id,
            @Parameter(name = "DeliveryIdStatusPutRequest", description = "") @Valid @RequestBody(required = false) DeliveryIdStatusPutRequest deliveryIdStatusPutRequest
    ) {
        Delivery delivery = null;
        try {
            delivery = deliveryService.updateStatus(id, deliveryIdStatusPutRequest.getUserId(),
                    deliveryIdStatusPutRequest.getStatus());
        } catch (NoDeliveryFoundException | BadArgumentException | AccessForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(delivery);
    }

    /**
     * Update the estimated time of a delivery
     *
     * @param id UUID of the delivery (required)
     * @param deliveryIdDeliveryTimePostRequest1  (optional)
     * @return the updated delivery
     */
    @Override
    public ResponseEntity<Delivery> deliveryIdPrepTimePut(
            @Parameter(name = "id", description = "UUID of the delivery", required = true) @PathVariable("id") UUID id,
            @Parameter(name = "DeliveryIdDeliveryTimePostRequest1", description = "") @Valid @RequestBody(required = false) DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest1
    ){
        Delivery delivery = deliveryService.changePrepTime(id, deliveryIdDeliveryTimePostRequest1.getUserId(), deliveryIdDeliveryTimePostRequest1.getEstimatedNewDeliveryTime());
        if (delivery == null){
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
     * Get default maximum delivery zone as an admin.
     *
     * @param adminId The admin ID (required)
     * @return The response that contains admin id and default maximum zone.
     */
    @Override
    public ResponseEntity<DeliveryAdminMaxZoneGet200Response> deliveryAdminMaxZoneGet(
            @NotNull @Parameter(name = "adminId", description = "The admin ID", required = true)
            @Valid @RequestParam(value = "adminId", required = true) UUID adminId
    ){
        try{
            DeliveryAdminMaxZoneGet200Response response = deliveryService.adminGetMaxZone(adminId, adminService);
            if (response == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(response);
        }
        catch (ServiceUnavailableException e){
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        catch (AccessForbiddenException e){
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
        try{
            DeliveryAdminMaxZoneGet200Response response =
                    deliveryService.adminSetMaxZone(deliveryAdminMaxZoneGet200Response.getAdminId(),
                            deliveryAdminMaxZoneGet200Response.getRadiusKm(), adminService);

            if (response == null) {
                return ResponseEntity.badRequest().body(deliveryAdminMaxZoneGet200Response);
            }
            return ResponseEntity.ok(response);
        }
        catch (ServiceUnavailableException e){
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        catch (AccessForbiddenException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Update the total delivery time of an order.
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
        Delivery delivery = deliveryService.addDeliveryTime(id, orderService, userService);
        if (delivery == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(delivery);
    }

}
