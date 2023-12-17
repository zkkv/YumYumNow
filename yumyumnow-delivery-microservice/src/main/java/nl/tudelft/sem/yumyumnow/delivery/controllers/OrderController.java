package nl.tudelft.sem.yumyumnow.delivery.controllers;

import nl.tudelft.sem.yumyumnow.delivery.application.services.OrderService;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for communicating with the order microservice
 */

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }
}
