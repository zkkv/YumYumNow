package nl.tudelft.sem.yumyumnow.delivery.application.services;


import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    private EmailService emailService;

    @BeforeEach
    void setUp(){
        emailService = new EmailService();
    }





    @Test
    void send() {
        String address = "max.verstappen1@gmail.com";
        String email = "The status of your order has been changed to Delivered";
        assertEquals("Email has been successfully sent", emailService.send(email,address));
    }
}