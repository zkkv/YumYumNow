package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.application.validators.UserIsAdminValidator;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.AdminMaxZoneGet200Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class AdminServiceTest {

    private DeliveryRepository deliveryRepository;
    private AdminService adminService;
    private OrderService orderService;
    private VendorService vendorService;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp(){
        this.deliveryRepository = mock(DeliveryRepository.class);
        this.orderService = mock(OrderService.class);
        this.vendorService = mock(VendorService.class);
        this.restTemplate = mock(RestTemplate.class);
        this.adminService = new AdminService(
                orderService, deliveryRepository,
                vendorService, restTemplate,
                "https://testsite.com");
    }

    @Test
    public void adminGetMaxZoneTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));
        when(vendorService.getDefaultMaxDeliveryZone()).thenReturn(BigDecimal.valueOf(20));

        AdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new  AdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        AdminMaxZoneGet200Response response = adminService.adminGetMaxZone(adminId);
        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminGetMaxZoneExceptionTest() throws ServiceUnavailableException {
        UUID adminId = UUID.randomUUID();

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Courier"));
        when(vendorService.getDefaultMaxDeliveryZone()).thenReturn(BigDecimal.valueOf(20));

        assertThrows(AccessForbiddenException.class, () -> {
            adminService.adminGetMaxZone(adminId);
        });
    }

    @Test
    public void adminSetMaxZoneTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal newMaxZone = BigDecimal.valueOf(20);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));

        AdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new  AdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(newMaxZone);

        AdminMaxZoneGet200Response response = adminService.adminSetMaxZone(adminId, newMaxZone);

        verify(vendorService).setDefaultMaxDeliveryZone(newMaxZone);
        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminSetMaxZoneExceptionTest() throws ServiceUnavailableException{
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Courier"));

        assertThrows(AccessForbiddenException.class, () -> {
            adminService.adminSetMaxZone(adminId, defaultMaxZone);
        });
    }

    @Test
    public void adminSetMaxZoneNotValidTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(-2);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));

        AdminMaxZoneGet200Response response = adminService.adminSetMaxZone(adminId, defaultMaxZone);

        assertEquals(null, response);
    }
}
