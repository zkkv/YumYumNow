package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.GlobalConfig;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.VendorCustomizer;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.GlobalConfigRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.VendorCustomizerRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryAdminMaxZoneGet200Response;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryVendorIdMaxZonePutRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


public class DeliveryServiceTest {
    private DeliveryRepository deliveryRepository;
    private VendorCustomizerRepository vendorCustomizerRepository;
    private GlobalConfigRepository globalConfigRepository;


    private DeliveryService deliveryService;
    private VendorService vendorService;
    private AdminService adminService;
    @Value("${globalConfigId}$")
    private UUID globalConfigId;

    @BeforeEach
    void setUp(){
        this.deliveryRepository = mock(DeliveryRepository.class);
        this.vendorCustomizerRepository = mock(VendorCustomizerRepository.class);
        this.globalConfigRepository = mock(GlobalConfigRepository.class);
        this.vendorService = mock(VendorService.class);
        this.adminService = mock(AdminService.class);

        deliveryService = new DeliveryService(
                deliveryRepository,
                vendorCustomizerRepository,
                globalConfigRepository);
    }

    @Test
    public void setStatusToAcceptedAsNonVendor(){
        assertNull(deliveryService.updateStatus(
                UUID.randomUUID(),UUID.randomUUID(), DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED));
    }
    @Test
    public void setStatusToDeliveredAsVendor(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new Delivery();
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(vendorCustomizerRepository.findById(userId)).thenReturn(Optional.of(new VendorCustomizer()));

        Delivery actual =  deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.DELIVERED);
        assertNull(actual);
    }

    @Test
    public void setStatusToAcceptedAsVendor(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new Delivery();
        expected.setId(id);
        expected.setStatus(Delivery.StatusEnum.PENDING);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
        when(vendorCustomizerRepository.findById(userId)).thenReturn(Optional.of(new VendorCustomizer()));

        Delivery actual =  deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED);
        assertEquals(expected, actual);
        assertEquals(expected.getStatus().getValue(), "ACCEPTED");
    }

    @Test
    public void setStatusToInTransitAsNonCourier(){
        assertNull(deliveryService.updateStatus(
                UUID.randomUUID(), UUID.randomUUID(), DeliveryIdStatusPutRequest.StatusEnum.IN_TRANSIT));
    }

    @Test
    public void setStatusToDeliveredAsNonCourier(){
        assertNull(deliveryService.updateStatus(
                UUID.randomUUID(), UUID.randomUUID(), DeliveryIdStatusPutRequest.StatusEnum.DELIVERED));
    }

    @Test
    public void setStatusToInTransitAsCourier(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new Delivery();
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);


        Delivery actual =  deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.IN_TRANSIT);
        assertEquals(expected, actual);
    }

    @Test
    public void setStatusToDeliveredAsCourier(){
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Delivery expected = new Delivery();
        expected.setId(id);
        Optional<Delivery> optionalDelivery = Optional.of(expected);
        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);


        Delivery actual =  deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.DELIVERED);
        assertEquals(expected, actual);
    }

    // TO-DO: Redo this test
//    @Test
//    public void setStatusAsStrangerCourier(){
//        UUID id = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        UUID actualCourierId = UUID.randomUUID();
//
//        Delivery delivery = new Delivery();
//        delivery.setId(id);
//        Optional<Delivery> optionalDelivery = Optional.of(delivery);
//        when(deliveryRepository.findById(id)).thenReturn(optionalDelivery);
//
//
//        Delivery actual =  deliveryService.updateStatus(id, userId, DeliveryIdStatusPutRequest.StatusEnum.DELIVERED);
//        assertNull(actual);
//    }

    @Test
    public void vendorMaxZoneTest(){
        UUID vendorId = UUID.randomUUID();
        DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest = new DeliveryVendorIdMaxZonePutRequest();

        deliveryVendorIdMaxZonePutRequest.setVendorId(vendorId);
        deliveryVendorIdMaxZonePutRequest.setRadiusKm(BigDecimal.valueOf(5));

        Map<String, Object> vendorMap = new HashMap<>();
        vendorMap.put("allowOnlyOwnCouriers", true);
        vendorMap.put("maxDeliveryZone",BigDecimal.valueOf(2));

        when(vendorService.getVendor(vendorId)).thenReturn(vendorMap);
        when(vendorService.putVendor(vendorId,vendorMap)).thenReturn(true);

        DeliveryVendorIdMaxZonePutRequest response = deliveryService.vendorMaxZone(vendorId,deliveryVendorIdMaxZonePutRequest,vendorService);

        assertEquals(deliveryVendorIdMaxZonePutRequest, response);
    }

    @Test
    public void adminGetMaxZoneTest(){
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setGlobalConfigId(globalConfigId);
        globalConfig.setDefaultMaxZone(defaultMaxZone);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.of(globalConfig);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        DeliveryAdminMaxZoneGet200Response response = deliveryService.adminGetMaxZone(adminId, adminService);
        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminGetMaxZoneNotFoundTest(){
        UUID adminId = UUID.randomUUID();

        Optional<GlobalConfig> optionalGlobalConfig = Optional.empty();
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response response = deliveryService.adminGetMaxZone(adminId, adminService);
        assertNull(response);
    }

    @Test
    public void adminGetMaxZoneNullDefaultTest(){
        UUID adminId = UUID.randomUUID();

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setGlobalConfigId(globalConfigId);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.of(globalConfig);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        BigDecimal defaultMaxZone = BigDecimal.valueOf(0);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(defaultMaxZone);

        DeliveryAdminMaxZoneGet200Response response = deliveryService.adminGetMaxZone(adminId, adminService);

        assertEquals(BigDecimal.valueOf(0), globalConfig.getDefaultMaxZone());
        verify(globalConfigRepository).save(globalConfig);
        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminSetMaxZoneTest(){
        UUID adminId = UUID.randomUUID();
        BigDecimal newMaxZone = BigDecimal.valueOf(20);
        BigDecimal originalMaxZone = BigDecimal.valueOf(10);

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setGlobalConfigId(globalConfigId);
        globalConfig.setDefaultMaxZone(originalMaxZone);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.of(globalConfig);
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response deliveryAdminMaxZoneGet200Response = new DeliveryAdminMaxZoneGet200Response();
        deliveryAdminMaxZoneGet200Response.setAdminId(adminId);
        deliveryAdminMaxZoneGet200Response.setRadiusKm(newMaxZone);

        DeliveryAdminMaxZoneGet200Response response = deliveryService.adminSetMaxZone(adminId, newMaxZone);

        assertEquals(BigDecimal.valueOf(20), globalConfig.getDefaultMaxZone());
        verify(globalConfigRepository).save(globalConfig);
        assertEquals(deliveryAdminMaxZoneGet200Response, response);
    }

    @Test
    public void adminSetMaxZoneNotFoundTest(){
        UUID adminId = UUID.randomUUID();
        BigDecimal defaultMaxZone = BigDecimal.valueOf(20);

        Optional<GlobalConfig> optionalGlobalConfig = Optional.empty();
        when(globalConfigRepository.findById(globalConfigId)).thenReturn(optionalGlobalConfig);

        DeliveryAdminMaxZoneGet200Response response = deliveryService.adminSetMaxZone(adminId, defaultMaxZone);
        assertNull(response);
    }

}