package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

    private AdminService adminService;
    private RestTemplate restTemplate;
    private final String testWebsite = "test://website";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        adminService = new AdminService(
                restTemplate,
                testWebsite
        );
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
        assertTrue(adminService.validate(adminId));
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
        assertFalse(adminService.validate(adminId));
    }

    @Test
    void validateServiceUnavailable() {
        UUID adminId = UUID.randomUUID();

        when(restTemplate.getForObject(
                testWebsite + "/" + adminId,
                Map.class
        )).thenThrow(RestClientException.class);

        assertThrows(ServiceUnavailableException.class, () -> adminService.validate(adminId));
    }
}
