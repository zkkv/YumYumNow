package nl.tudelft.sem.yumyumnow.delivery.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class AdminService{

    private final RestTemplate restTemplate;
    private final String adminServiceUrl;

    /**
     * Constructor for admin service.
     *
     * @param restTemplate restTemplate to interact with other api
     * @param userServiceUrl url for user microservice
     */
    @Autowired
    public AdminService(RestTemplate restTemplate, @Value("${user.microservice.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.adminServiceUrl = userServiceUrl + "/admin/";
    }

}
