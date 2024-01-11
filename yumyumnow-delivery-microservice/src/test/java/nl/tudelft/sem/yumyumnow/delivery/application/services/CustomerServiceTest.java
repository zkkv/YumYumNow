package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

        Customer expectedCustomer = new Customer();
        expectedCustomer.setId(customerId);
        expectedCustomer.setName("test");
        expectedCustomer.setPhone("123456789");

        Location address = new Location();
        address.setTimestamp(null);
        address.setLatitude(BigDecimal.valueOf(0));
        address.setLongitude(BigDecimal.valueOf(0));

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
