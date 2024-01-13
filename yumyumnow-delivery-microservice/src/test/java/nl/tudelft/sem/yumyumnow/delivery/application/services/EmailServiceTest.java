package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private OrderService orderService;

    private CustomerService customerService;

    private DeliveryRepository deliveryRepository;

    private EmailService emailService;

    @BeforeEach
    void setUp(){
        orderService = mock(OrderService.class);
        customerService = mock(CustomerService.class);
        deliveryRepository = mock(DeliveryRepository.class);
        emailService = new EmailService(orderService,customerService,deliveryRepository);
    }





    @Test
    void send() {
        String address = "max.verstappen1@gmail.com";
        String email = "The status of your order has been changed to Delivered";
        assertEquals("Email has been successfully sent", emailService.send(email,address));
    }
}