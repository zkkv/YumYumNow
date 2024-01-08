package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@Service
public class UserService {
    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    /**
     * Creates a new User Service.
     *
     * @param restTemplate the RestTemplate object used for making HTTP requests to the Order microservice.
     * @param userServiceUrl the url of the User Microservice.
     */
    @Autowired
    public UserService(RestTemplate restTemplate, @Value("${user.microservice.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    // TODO: adjust the data types
    /**
     * Gets the customer's address.
     *
     * @param userId the id of the user.
     * @return a Location object.
     */
    public Location getCustomerAddress(UUID userId) {
        String url = userServiceUrl + "/customer/address/" + userId;
        ResponseEntity<Location> response = restTemplate.getForEntity(url, Location.class);
        return response.getBody();
    }
}
