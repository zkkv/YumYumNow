package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class AdminService {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    /**
     * Constructor for admin service with RestTemplateBuilder.
     *
     * @param restTemplate restTemplate to interact with other api
     * @param userServiceUrl url for user microservice
     */
    @Autowired
    public AdminService(RestTemplateBuilder restTemplate, @Value("${user.microservice.url}") String userServiceUrl) {
        this.restTemplate = restTemplate.build();
        this.userServiceUrl = userServiceUrl;
    }

    /**
     * Constructor for admin service with RestTemplate.
     *
     * @param restTemplate restTemplate to interact with other api
     * @param userServiceUrl url for user microservice
     */
    public AdminService(RestTemplate restTemplate, String userServiceUrl) {
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
