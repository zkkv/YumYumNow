package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CustomerBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomerService {
    private final RestTemplate restTemplate;
    private final String customerServiceUrl;

    /**
     * Creates a new User Service.
     *
     * @param restTemplate the RestTemplate object used for making HTTP requests to the Order microservice.
     * @param userServiceUrl the url of the User Microservice.
     */
    @Autowired
    public CustomerService(RestTemplate restTemplate, @Value("${user.microservice.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.customerServiceUrl = userServiceUrl + "/customer/";
    }

    /**
     * Get a customer by userid.
     *
     * @param userId the id of customer
     * @return the customer as a json map
     */
    private Map<String, Object> getCustomerRaw(String userId) {
        String url = customerServiceUrl + userId;
        return restTemplate.getForObject(url, Map.class);
    }

    /**
     * Get a customer by userid.
     *
     * @param customerId the id of customer
     * @return the customer object
     */
    public Customer getCustomer(String customerId) {
        Map<String, Object> response = getCustomerRaw(customerId);

        if (response == null) {
            return null;
        }

        Location address = new Location();
        address.setTimestamp(OffsetDateTime.now());
        address.setLatitude(new BigDecimal(String.valueOf(((Map<String, Object>) response.get("location"))
                .get("latitude"))));
        address.setLongitude(new BigDecimal(String.valueOf(((Map<String, Object>) response.get("location"))
                .get("longitude"))));

        Customer customer = new CustomerBuilder()
            .setId(UUID.fromString((String) response.get("userID")))
            .setName((String) response.get("name"))
            .setPhoneNumber((String) ((Map<String, Object>) response.get("contactInfo")).get("phoneNumber"))
            .setAddress(address)
                .setEmail((String) ((Map<String, Object>) response.get("contactInfo")).get("email"))
            .create();

        return customer;
    }


    /**
     * Gets the customer's address.
     *
     * @param userId the id of the user.
     * @return a Location object.f
     */
    public Location getCustomerAddress(UUID userId) {
        String url = customerServiceUrl + "address/" + userId;
        ResponseEntity<Location> response = restTemplate.getForEntity(url, Location.class);
        return response.getBody();
    }
}
