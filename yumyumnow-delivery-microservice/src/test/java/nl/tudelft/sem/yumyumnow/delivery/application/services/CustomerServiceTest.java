package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CustomerBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomerServiceTest {
    private CustomerService customerService;
    private RestTemplate restTemplate;
    private final String testWebsite = "test://website";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        customerService = new CustomerService(
                restTemplate,
                testWebsite
        );
    }

    @Test
    public void testGetCustomerSuccess() {
        UUID customerId = UUID.randomUUID();

        Location address = new Location();
        OffsetDateTime timestamp = OffsetDateTime.now();
        address.setTimestamp(timestamp);
        address.setLatitude(BigDecimal.valueOf(0));
        address.setLongitude(BigDecimal.valueOf(0));

        Customer expectedCustomer = new CustomerBuilder()
                .setId(customerId)
                .setName("test")
                .setPhoneNumber("123456789")
                .setAddress(address)
                .create();

        expectedCustomer.setDeliveryAddress(address);

        when(restTemplate.getForObject(
                testWebsite + "/customer/" + customerId,
                Map.class
        )).thenReturn(Map.of(
                "userID", customerId.toString(),
                "name", "test",
                "contactInfo", Map.of(
                        "phoneNumber", "123456789"
                ),
                "location", Map.of(
                        "latitude", 0,
                        "longitude", 0
                )
        ));

        Customer gotCustomer = customerService.getCustomer(customerId.toString());

        assertEquals(gotCustomer.getId(),expectedCustomer.getId());
        assertEquals(gotCustomer.getName(), expectedCustomer.getName());
        assertEquals(gotCustomer.getPhone(), expectedCustomer.getPhone());
        assertEquals(gotCustomer.getDeliveryAddress().getLatitude(),expectedCustomer.getDeliveryAddress().getLatitude());
        assertEquals(gotCustomer.getDeliveryAddress().getLongitude(), expectedCustomer.getDeliveryAddress().getLongitude());
        assertTrue(ChronoUnit.SECONDS.between(timestamp, gotCustomer.getDeliveryAddress().getTimestamp()) < 10);
    }

    @Test
    public void testGetCustomerAddressSuccess() {
        UUID customerId = UUID.randomUUID();

        Location expectedAddress = new Location();
        expectedAddress.setTimestamp(null);
        expectedAddress.setLatitude(BigDecimal.valueOf(0));
        expectedAddress.setLongitude(BigDecimal.valueOf(0));

        ResponseEntity<Location> response = new ResponseEntity<>(expectedAddress, HttpStatus.ACCEPTED);

        when(restTemplate.getForEntity(
                testWebsite + "/customer/address/" + customerId,
                Location.class
        )).thenReturn(response);

        Location gotAddress = customerService.getCustomerAddress(customerId);

        assertEquals(gotAddress.getLatitude(),expectedAddress.getLatitude());
        assertEquals(gotAddress.getLongitude(), expectedAddress.getLongitude());
    }

    @Test
    public void testGetCustomerEmptyResponse() {
        String uuid = UUID.randomUUID().toString();

        when(restTemplate.getForObject(
                testWebsite + "/customer/" + uuid, Map.class
        )).thenReturn(null);

        assertNull(customerService.getCustomer(uuid));
    }
}
