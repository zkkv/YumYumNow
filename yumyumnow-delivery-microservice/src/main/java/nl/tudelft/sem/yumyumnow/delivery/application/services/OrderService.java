package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.repos.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderService {
    OrderRepository orderRepository;

    /**
     * Create a new OrderService.
     *
     * @param orderRepository The repository to use.
     */
    @Autowired
    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }
}
