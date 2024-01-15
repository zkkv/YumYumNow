package nl.tudelft.sem.yumyumnow.delivery.application.services;


import org.springframework.stereotype.Service;

public abstract class EmailService {



    /**
     * Create a new email service.
     *
     */
    public EmailService() {

    }

    /** Send an email.
     *
     * @param email the email to be sent
     * @param address address where the email should be sent
     * @return confirmation that the email was sent
     */
    public abstract String send(String email, String address);
}
