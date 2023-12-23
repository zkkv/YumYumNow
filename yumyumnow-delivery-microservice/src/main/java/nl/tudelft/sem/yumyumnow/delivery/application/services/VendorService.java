package nl.tudelft.sem.yumyumnow.delivery.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class VendorService extends UserService {
    private final RestTemplate restTemplate;
    private final String vendorServiceUrl;

    /**
     * Constructor for vendor service.
     *
     * @param restTemplate restTemplate to interact with other api
     * @param userServiceUrl url for user microservice
     */
    @Autowired
    public VendorService(RestTemplate restTemplate, @Value("${user.microservice.url}") String userServiceUrl) {
        super(restTemplate, userServiceUrl);
        this.restTemplate = restTemplate;
        this.vendorServiceUrl = userServiceUrl + "/vendor/";
    }

    /**
     * Get a vendor by its user id.
     *
     * @param vendorId The id of the vendor.
     * @return the vendor as a map of response JSON
     */
    public Map<String, Object> getVendor(UUID vendorId) {
        String url = vendorServiceUrl + vendorId;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return response;
    }

    /**
     * Update a vendor.
     *
     * @param vendorId The id of updated vendor
     * @param vendorMap The updated vendor
     */
    public boolean putVendor(UUID vendorId, Map<String, Object> vendorMap) {
        String url = vendorServiceUrl + vendorId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(vendorMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        return response.getStatusCode().is2xxSuccessful();
    }
}
