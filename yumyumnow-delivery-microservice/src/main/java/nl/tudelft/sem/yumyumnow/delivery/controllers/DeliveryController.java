package nl.tudelft.sem.yumyumnow.delivery.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import nl.tudelft.sem.yumyumnow.delivery.api.DeliveryApi;
import nl.tudelft.sem.yumyumnow.delivery.model.*;
import nl.tudelft.sem.yumyumnow.delivery.application.services.DeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

import javax.validation.Valid;

@RestController
public class DeliveryController implements DeliveryApi {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
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
     * @param id UUID of the delivery (required)
     * @param deliveryIdDeliveryTimePostRequest  (optional)
     * @return the updated delivery
     */
    @Override
    public ResponseEntity<Delivery> deliveryIdPrepTimePost(
            @Parameter(name = "id", description = "UUID of the delivery", required = true) @PathVariable("id") UUID id,
            @Parameter(name = "DeliveryIdDeliveryTimePostRequest1", description = "") @Valid @RequestBody(required = false) DeliveryIdDeliveryTimePostRequest1 deliveryIdDeliveryTimePostRequest
    ){
        Delivery delivery = deliveryService.addPrepTime(id, deliveryIdDeliveryTimePostRequest.getUserId(), deliveryIdDeliveryTimePostRequest.getEstimatedNewDeliveryTime());

        if (delivery == null){
            return (ResponseEntity<Delivery>) ResponseEntity.status(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(delivery);
    }


    /**
     * Change the status of the delivery
     * @param id UUID of the delivery (required)
     * @param deliveryIdStatusPutRequest  (optional)
     * @return the updated delivery
     */
    public ResponseEntity<Delivery> deliveryIdStatusPut(
            @Parameter(name = "id", description = "UUID of the delivery", required = true) @PathVariable("id") UUID id,
            @Parameter(name = "DeliveryIdStatusPutRequest", description = "") @Valid @RequestBody(required = false) DeliveryIdStatusPutRequest deliveryIdStatusPutRequest
    ) {
        Delivery delivery = deliveryService.updateStatus(id, deliveryIdStatusPutRequest.getUserId(), deliveryIdStatusPutRequest.getStatus());

        if (delivery == null) {
            return (ResponseEntity<Delivery>) ResponseEntity.status(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(delivery);
    }
    /**
     * Updated the estimated time of a delivery
     * @param id UUID of the delivery (required)
     * @param deliveryIdDeliveryTimePutRequest  (optional)
     * @return the updated delivery
     */
//    @Override
//    public ResponseEntity<Delivery> deliveryIdPrepTimePut(
//            @Parameter(name = "id", description = "UUID of the delivery", required = true) @PathVariable("id") UUID id,
//            @Parameter(name = "DeliveryIdDeliveryTimePostRequest1", description = "") @Valid @RequestBody(required = false) DeliveryIdDeliveryTimePostRequest deliveryIdDeliveryTimePutRequest
//    ){
//        Delivery delivery = deliveryService.addPrepTime(id, deliveryIdDeliveryTimePutRequest.getCourierId(), deliveryIdDeliveryTimePutRequest.getEstimatedNewDeliveryTime());
//        if (delivery == null){
//            return (ResponseEntity<Delivery>) ResponseEntity.status(HttpStatus.BAD_REQUEST);
//        }
//
//        return ResponseEntity.ok(delivery);
//    }


}
