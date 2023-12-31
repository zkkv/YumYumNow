package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class CourierService extends UserService {
    private final RestTemplate restTemplate;
    private final String courierServiceUrl;
    private final VendorService vendorService;

    @Autowired
    public CourierService(RestTemplate restTemplate, @Value("${user.microservice.url}") String userServiceUrl, VendorService vendorService) {
        super(restTemplate, userServiceUrl);
        this.restTemplate = restTemplate;
        this.courierServiceUrl = userServiceUrl + "/courier/";
        this.vendorService = vendorService;
    }

    private Map<String, Object> getCourierRaw(String courierId) {
        String url = courierServiceUrl + courierId;
        return restTemplate.getForObject(url, Map.class);
    }

    /**
     * Get a vendor by its user id.
     *
     * @param courierId The id of the vendor.
     * @return the vendor as a map of response JSON
     */
    public Courier getCourier(String courierId) {
        Map<String, Object> response = getCourierRaw(courierId);

        Courier courier = new Courier();

        courier.setId(UUID.fromString((String) response.get("userID")));
        courier.setVendor(vendorService.getVendor((String) response.get("vendor")));

        return courier;
    }

    /**
     * Update a vendor.
     *
     * @param courier The updated vendor
     */
    public boolean putCourier(Courier courier) {
        Map<String, Object> vendorMap = getCourierRaw(courier.getId().toString());

        vendorMap.put("userID", courier.getId().toString());
        vendorMap.put("vendor", courier.getVendor().getId().toString());

        String url = courierServiceUrl + courier.getId().toString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(vendorMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        return response.getStatusCode().is2xxSuccessful();
    }
}
