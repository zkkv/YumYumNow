package nl.tudelft.sem.yumyumnow.delivery.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import nl.tudelft.sem.yumyum.delivery.api.DeliveryApi;
import nl.tudelft.sem.yumyum.delivery.model.Delivery;
import nl.tudelft.sem.yumyum.delivery.model.Error;
import nl.tudelft.sem.yumyum.delivery.model.Order;
import nl.tudelft.sem.yumyumnow.delivery.application.services.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class DeliveryController implements DeliveryApi {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Override
    @Operation(
            operationId = "deliveryPost",
            summary = "Create a delivery",
            description = "Create a delivery based on order data.",
            tags = { "Delivery" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful response", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Delivery.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    })
            }
    )
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/delivery",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public ResponseEntity<Delivery> deliveryPost(
            @Parameter(name = "Order", description = "")
            @Valid @RequestBody Order order) {
        Delivery delivery = deliveryService.createDelivery(order);

        return ResponseEntity.ok(delivery);
    }

}
