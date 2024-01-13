package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;

import java.util.UUID;

public class EmailService {

    private final OrderService orderService;

    private final CustomerService customerService;

    private final DeliveryRepository deliveryRepository;

    /**
     * Create a new email service
     *
     * @param orderService       order service
     * @param customerService    customer service
     * @param deliveryRepository delivery repository
     */
    public EmailService(OrderService orderService, CustomerService customerService, DeliveryRepository deliveryRepository) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Send an email
     * @param email the email to be sent
     * @param address the address of the email
     * @return confirmation that the email was sent
     */

    public String send(String email, String address){
        //Send mock email
        return "Email has been successfully sent";
    }
}
