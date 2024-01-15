package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CourierBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.UUID;

@Service
public class CourierService {
    private final RestTemplate restTemplate;
    private final String courierServiceUrl;
    private final VendorService vendorService;

    /**
     * Constructor for courier service.
     *
     * @param restTemplate restTemplate to interact with other api
     * @param userServiceUrl url for user microservice
     */
    @Autowired
    public CourierService(RestTemplateBuilder restTemplate, @Value("${user.microservice.url}") String userServiceUrl,
                          VendorService vendorService) {
        this.restTemplate = restTemplate.build();
        this.courierServiceUrl = userServiceUrl + "/courier/";
        this.vendorService = vendorService;
    }

    public CourierService(RestTemplate restTemplate, String userServiceUrl, VendorService vendorService) {
        this.restTemplate = restTemplate;
        this.courierServiceUrl = userServiceUrl + "/courier/";
        this.vendorService = vendorService;
    }

    private Map<String, Object> getCourierRaw(String courierId) {
        String url = courierServiceUrl + courierId;
        return restTemplate.getForObject(url, Map.class);
    }

    /**
     * Get a courier by its courier id.
     *
     * @param courierId The id of the courier.
     * @return the vendor as a map of response JSON
     */
    public Courier getCourier(String courierId) {
        Map<String, Object> response = getCourierRaw(courierId);

        if (response == null) {
            return null;
        }

        return new CourierBuilder()
                .setId(UUID.fromString((String) response.get("userID")))
                .setVendor(vendorService.getVendor((String) response.get("vendor")))
                .create();
    }

    /**
     * Update a vendor.
     *
     * @param courier The updated vendor
     */
    public boolean putCourier(Courier courier) {
        Map<String, Object> courierMap = getCourierRaw(courier.getId().toString());

        courierMap.put("userID", courier.getId().toString());
        courierMap.put("vendor", courier.getVendor().getId().toString());

        String url = courierServiceUrl + courier.getId().toString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(courierMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        return response.getStatusCode().is2xxSuccessful();
    }
}
