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

    /** Send an email to the customer that the status of its order has been changed
     * @param status the new status
     * @param id the id of the order
     */
    public String sendEmail(DeliveryIdStatusPutRequest.StatusEnum status, UUID id) throws BadArgumentException {
        Delivery delivery = deliveryRepository.findById(id).get();

        Order order = orderService.findOrderById(delivery.getOrderId());

        if(order == null){
            throw new BadArgumentException("Delivery isn't linked to an order");
        }

        Customer customer = order.getCustomer();

        if(customer == null){
            throw new BadArgumentException("Order doesn't have a customer");
        }

        if(customer.getEmail() == null){
            throw new BadArgumentException("Customer doesn't have an email");
        }

        String email = "The order of your status has been changed to " + status.getValue();

        return send(email, customer.getEmail());
    }

    public String send(String email, String address){
        //Send mock email
        return "Email has been successfully sent";
    }
}
