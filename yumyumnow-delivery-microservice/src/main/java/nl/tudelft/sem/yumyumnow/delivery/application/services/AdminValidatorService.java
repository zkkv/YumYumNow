package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AdminValidatorService {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    /**
     * Constructor for AdminValidator object
     * @param restTemplate      the RestTemplate object used for making HTTP requests to the Order microservice.
     * @param userServiceUrl    the url for the user microservice
     */
    @Autowired
    public AdminValidatorService(RestTemplate restTemplate, @Value("${user.microservice.url}") String userServiceUrl){
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    /**
     * Validate the admin.
     *
     * @param adminId The admin to validate
     * @return The validation result.
     * @throws ServiceUnavailableException Exception if the service of other microservice is unavailable.
     */
    public boolean validate(UUID adminId) throws ServiceUnavailableException {
        try {
            Map<String, Object> responseUser = restTemplate.getForObject(userServiceUrl
                    + "/" + adminId.toString(), Map.class);
            String type = (String) responseUser.get("userType");
            if (Objects.equals(type, "Admin")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new ServiceUnavailableException(e.getMessage());
        }
    }
}
