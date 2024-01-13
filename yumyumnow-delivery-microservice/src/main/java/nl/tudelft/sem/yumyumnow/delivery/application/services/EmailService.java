package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;

import java.util.UUID;

public class EmailService {



    /**
     * Create a new email service
     *
     */
    public EmailService() {

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
