package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
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
public class VendorService {
    private final RestTemplate restTemplate;
    private final String vendorServiceUrl;

    /**
     * Creates a new VendorService object.
     *
     * @param restTemplate the RestTemplate object used for making HTTP requests to the Order microservice.
     * @param userServiceUrl the url of the User Microservice.
     */
    @Autowired
    public VendorService(RestTemplate restTemplate, @Value("${user.microservice.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.vendorServiceUrl = userServiceUrl + "/vendor/";
    }

    private Map<String, Object> getVendorRaw(String vendorId) {
        String url = vendorServiceUrl + vendorId;
        return restTemplate.getForObject(url, Map.class);
    }

    /**
     * Get a vendor by its user id.
     *
     * @param vendorId The id of the vendor.
     * @return the vendor as a map of response JSON
     */
    public Vendor getVendor(String vendorId) {
        Map<String, Object> response = getVendorRaw(vendorId);

        if (response == null) {
            return null;
        }

        Location address = new Location();
        address.setTimestamp(OffsetDateTime.now());
        address.setLatitude(new BigDecimal(String.valueOf(((Map<String, Object>) response.get("location")).get("latitude"))));
        address.setLongitude(new BigDecimal(String.valueOf(((Map<String, Object>) response.get("location")).get("longitude"))));


        Vendor vendor = new VendorBuilder()
                .setId(UUID.fromString((String) response.get("userID")))
                .setAddress(address)
                .setPhoneNumber((String) ((Map<String, Object>) response.get("contactInfo")).get("phoneNumber"))
                .setAllowsOnlyOwnCouriers((Boolean) response.get("allowsOnlyOwnCouriers"))
                .setMaxDeliveryZoneKm((BigDecimal) response.get("maxDeliveryZone"))
                .createVendor();

        return vendor;
    }

    /**
     * Update a vendor.
     *
     * @param vendor The updated vendor
     */
    public boolean putVendor(Vendor vendor) {
        Map<String, Object> vendorMap = getVendorRaw(vendor.getId().toString());

        vendorMap.put("location", Map.of(
                "latitude", vendor.getAddress().getLatitude(),
                "longitude", vendor.getAddress().getLongitude()
        ));
        vendorMap.put("contactInfo", Map.of(
                "phoneNumber", vendor.getPhone()
        ));
        vendorMap.put("allowsOnlyOwnCouriers", vendor.getAllowsOnlyOwnCouriers());
        vendorMap.put("maxDeliveryZone", vendor.getMaxDeliveryZoneKm());

        String url = vendorServiceUrl + vendor.getId().toString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(vendorMap, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        return response.getStatusCode().is2xxSuccessful();
    }
}
