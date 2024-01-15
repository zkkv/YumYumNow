package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.GlobalConfig;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.GlobalConfigRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryAdminMaxZoneGet200Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class AdminServiceTest {

    private DeliveryRepository deliveryRepository;
    private GlobalConfigRepository globalConfigRepository;
    private AdminService adminService;
    private OrderService orderService;
    @Value("${globalConfigId}$")
    private UUID globalConfigId;
    private AdminValidatorService adminValidatorService;

    @BeforeEach
    void setUp(){
        this.deliveryRepository = mock(DeliveryRepository.class);
        this.globalConfigRepository = mock(GlobalConfigRepository.class);
        this.orderService = mock(OrderService.class);
        this.adminValidatorService = mock(AdminValidatorService.class);
        this.adminService = new AdminService(orderService,deliveryRepository,adminValidatorService,globalConfigRepository);
    }

    @Test
    public void adminGetMaxZoneTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setGlobalConfigId(globalConfigId);
        globalConfig.setDefaultMaxZone(defaultMaxZone);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.of(globalConfig);
        when(adminValidatorService.validate(adminId)).thenReturn(true);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        DeliveryAdminMaxZoneGet200Response response = adminService.adminGetMaxZone(adminId, adminService);
        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminGetMaxZoneNotFoundTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        Optional<GlobalConfig> optionalGlobalConfig = Optional.empty();
        when(adminValidatorService.validate(adminId)).thenReturn(true);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response response = adminService.adminGetMaxZone(adminId, adminService);
        assertNull(response);
    }

    @Test
    public void adminGetMaxZoneNullDefaultTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setGlobalConfigId(globalConfigId);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.of(globalConfig);
        when(adminValidatorService.validate(adminId)).thenReturn(true);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);

        DeliveryAdminMaxZoneGet200Response response = adminService.adminGetMaxZone(adminId, adminService);

        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminGetMaxZoneExceptionTest() throws ServiceUnavailableException {
        UUID adminId = UUID.randomUUID();

        Optional<GlobalConfig> optionalGlobalConfig = Optional.empty();
        when(adminValidatorService.validate(adminId)).thenReturn(false);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        assertThrows(AccessForbiddenException.class, () -> {
            adminService.adminGetMaxZone(adminId, adminService);
        });
    }

    @Test
    public void adminSetMaxZoneTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal newMaxZone = BigDecimal.valueOf(20);
        BigDecimal originalMaxZone = BigDecimal.valueOf(10);

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setGlobalConfigId(globalConfigId);
        globalConfig.setDefaultMaxZone(originalMaxZone);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.of(globalConfig);
        when(adminValidatorService.validate(adminId)).thenReturn(true);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(newMaxZone);

        DeliveryAdminMaxZoneGet200Response response = adminService.adminSetMaxZone(adminId, newMaxZone, adminService);

        assertEquals(BigDecimal.valueOf(20), globalConfig.getDefaultMaxZone());
        verify(globalConfigRepository).save(globalConfig);
        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminSetMaxZoneNotFoundTest() throws ServiceUnavailableException, AccessForbiddenException {
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.empty();
        when(adminValidatorService.validate(adminId)).thenReturn(true);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response response = adminService.adminSetMaxZone(adminId, defaultMaxZone, adminService);
        assertNull(response);
    }

    @Test
    public void adminSetMaxZoneExceptionTest() throws ServiceUnavailableException{
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.empty();
        when(adminValidatorService.validate(adminId)).thenReturn(false);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        assertThrows(AccessForbiddenException.class, () -> {
            adminService.adminSetMaxZone(adminId, defaultMaxZone, adminService);
        });
    }
}
