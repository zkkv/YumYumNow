package nl.tudelft.sem.yumyumnow.delivery.application.services;


public class EmailService {



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
    public String send(String email, String address) {
        //Send mock email
        return "Email has been successfully sent";
    }
}
