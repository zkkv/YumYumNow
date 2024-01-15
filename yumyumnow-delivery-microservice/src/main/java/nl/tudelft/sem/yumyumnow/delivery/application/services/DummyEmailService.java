package nl.tudelft.sem.yumyumnow.delivery.application.services;

import org.springframework.stereotype.Service;

@Service
public class DummyEmailService extends EmailService {
    @Override
    public String send(String email, String address) {
        return "Email sent to " + address + " with content: " + email + ".";
    }
}
