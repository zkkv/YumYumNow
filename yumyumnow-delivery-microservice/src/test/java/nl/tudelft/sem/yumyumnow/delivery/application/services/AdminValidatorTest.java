package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminValidatorTest {

    private AdminValidatorService adminValidatorService;
    private RestTemplate restTemplate;
    private final String testWebsite = "test://website";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        adminValidatorService = new AdminValidatorService(restTemplate, testWebsite);
    }

    @Test
    void validateIsAdmin() throws ServiceUnavailableException {
        UUID adminId = UUID.randomUUID();

        when(restTemplate.getForObject(
                testWebsite + "/" + adminId,
                Map.class
        )).thenReturn(Map.of(
                "userID", adminId.toString(),
                "firstname", "John",
                "surname", "James",
                "email", "john@email.com",
                "avatar", "www.avatar.com/avatar.png",
                "password", "12345",
                "verified", false,
                "userType", "Admin"
        ));
        assertTrue(adminValidatorService.validate(adminId));
    }

    @Test
    void validateIsNotAdmin() throws ServiceUnavailableException {
        UUID adminId = UUID.randomUUID();

        when(restTemplate.getForObject(
                testWebsite + "/" + adminId,
                Map.class
        )).thenReturn(Map.of(
                "userID", adminId.toString(),
                "firstname", "John",
                "surname", "James",
                "email", "john@email.com",
                "avatar", "www.avatar.com/avatar.png",
                "password", "12345",
                "verified", false,
                "userType", "Customer"
        ));
        assertFalse(adminValidatorService.validate(adminId));
    }

    @Test
    void validateServiceUnavailable() {
        UUID adminId = UUID.randomUUID();

        when(restTemplate.getForObject(
                testWebsite + "/" + adminId,
                Map.class
        )).thenThrow(RestClientException.class);

        assertThrows(ServiceUnavailableException.class, () -> adminValidatorService.validate(adminId));
    }
}