package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.GlobalConfig;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.GlobalConfigRepository;
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
import java.util.Optional;
import java.util.UUID;

@Service
public class VendorService {
    private final RestTemplate restTemplate;
    private final String vendorServiceUrl;
    private final GlobalConfigRepository globalConfigRepository;
    @Value("${globalConfigId}$")
    private UUID globalConfigId;

    /**
     * Constructor for vendor service.
     *
     * @param restTemplate restTemplate to interact with other api
     * @param userServiceUrl url for user microservice
     * @param globalConfigRepository global configuration repository
     */
    @Autowired
    public VendorService(RestTemplate restTemplate, @Value("${user.microservice.url}") String userServiceUrl,
                         GlobalConfigRepository globalConfigRepository) {
        this.restTemplate = restTemplate;
        this.vendorServiceUrl = userServiceUrl + "/vendor/";
        this.globalConfigRepository = globalConfigRepository;
    }

    /**
     * Get a vendor as json map  by its user id.
     *
     * @param vendorId vendorId
     * @return the json map of that vendor
     */
    private Map<String, Object> getVendorRaw(String vendorId) {
        String url = vendorServiceUrl + vendorId;
        return restTemplate.getForObject(url, Map.class);
    }

    /**
     * Get a vendor with updated max delivery zone by its user id.
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
        address.setLatitude(new BigDecimal(String.valueOf(((Map<String, Object>) response.get("location"))
                .get("latitude"))));
        address.setLongitude(new BigDecimal(String.valueOf(((Map<String, Object>) response.get("location"))
                .get("longitude"))));

        BigDecimal maxZone = null;
        if (!(Boolean) response.get("allowsOnlyOwnCouriers") || response.get("maxDeliveryZone") == null) {
            // If a vendor does not have its own couriers or the maxzone is not set by the vendor,
            // then the default maxzone is used.
            Optional<GlobalConfig> optionalGlobalConfig = globalConfigRepository.findById(globalConfigId);
            if (!optionalGlobalConfig.isEmpty()) {
                GlobalConfig globalConfig = optionalGlobalConfig.get();
                maxZone = globalConfig.getDefaultMaxZone();
            }
        } else {
            // If a vendor have its own couriers and set its own maxzone, then the customized maxzone is used.
            maxZone = new BigDecimal(String.valueOf(response.get("maxDeliveryZone")));
        }

        return new VendorBuilder()
                .setId(UUID.fromString((String) response.get("userID")))
                .setAddress(address)
                .setPhoneNumber((String) ((Map<String, Object>) response.get("contactInfo")).get("phoneNumber"))
                .setAllowsOnlyOwnCouriers((Boolean) response.get("allowsOnlyOwnCouriers"))
                .setMaxDeliveryZoneKm(maxZone)
                .create();
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
