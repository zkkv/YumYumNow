package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.application.validators.UserIsAdminValidator;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.DeliveryBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AnalyticsTest {
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
    void getTotalDeliveriesSuccessfulTest() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        // setting up the deliveries
        UUID id = UUID.randomUUID();
        Delivery delivery1 = new DeliveryBuilder()
                .setId(id)
                .setEstimatedDeliveryTime(OffsetDateTime.of(2022,1,1,12,0,0,0,ZoneOffset.UTC))
                .create();

        id = UUID.randomUUID();
        Delivery delivery2 = new DeliveryBuilder()
                .setId(id)
                .setEstimatedDeliveryTime(OffsetDateTime.of(2020,1,1,12,0,0,0,ZoneOffset.UTC))
                .create();

        List<Delivery> deliveries = new ArrayList<>();
        deliveries.add(delivery1);
        deliveries.add(delivery2);


        UUID adminId = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));
        when(deliveryRepository.findAll()).thenReturn(deliveries);

        assertThat(adminService.getTotalDeliveriesAnalytic(adminId, startDate, endDate)).isEqualTo(1);
    }

    @Test
    void getTotalDeliveriesExceptionTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));

        assertThrows(BadArgumentException.class, () -> {
            adminService.getTotalDeliveriesAnalytic(adminId, startDate, endDate);
        });
    }

    @Test
    void getTotalDeliveriesUnauthorizedTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Courier"));

        assertThrows(AccessForbiddenException.class, () -> {
            adminService.getTotalDeliveriesAnalytic(adminId, startDate, endDate);
        });
    }

    @Test
    void getSuccessfulDeliveriesSuccessfulTest() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        // setting up the deliveries
        UUID id = UUID.randomUUID();
        Delivery delivery1 = new DeliveryBuilder()
                .setId(id)
                .setEstimatedDeliveryTime(OffsetDateTime.of(2022,1,1,12,0,0,0,ZoneOffset.UTC))
                .setStatus(Delivery.StatusEnum.DELIVERED)
                .create();

        id = UUID.randomUUID();
        Delivery delivery2 = new DeliveryBuilder()
                .setId(id)
                .setEstimatedDeliveryTime(OffsetDateTime.of(2020,1,1,12,0,0,0,ZoneOffset.UTC))
                .setStatus(Delivery.StatusEnum.PENDING)
                .create();

        id = UUID.randomUUID();
        Delivery delivery3 = new DeliveryBuilder()
                .setId(id)
                .setEstimatedDeliveryTime(OffsetDateTime.of(2022,1,1,12,0,0,0,ZoneOffset.UTC))
                .setStatus(Delivery.StatusEnum.PENDING)
                .create();

        List<Delivery> deliveries = new ArrayList<>();
        deliveries.add(delivery1);
        deliveries.add(delivery2);
        deliveries.add(delivery3);

        UUID adminId = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));
        when(deliveryRepository.findAll()).thenReturn(deliveries);

        assertThat(adminService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate)).isEqualTo(1);
    }

    @Test
    void getSuccessfulDeliveriesExceptionTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));

        assertThrows(BadArgumentException.class, () -> {
            adminService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate);
        });
    }

    @Test
    void getSuccessfulDeliveriesUnauthorizedTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Courier"));

        assertThrows(AccessForbiddenException.class, () -> {
            adminService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate);
        });
    }

    @Test
    void getPreparationTimeSuccessfulTest() throws ServiceUnavailableException, BadArgumentException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2022, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2024, 12, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));

        // setting up the deliveries
        UUID id1 = UUID.randomUUID();
        UUID orderId1 = UUID.randomUUID();
        Delivery delivery1 = new DeliveryBuilder()
                .setId(id1)
                .setStatus(Delivery.StatusEnum.DELIVERED)
                .setEstimatedPreparationFinishTime(OffsetDateTime.of(2023, 12, 24, 16, 59, 07, 0, ZoneOffset.UTC))
                .setEstimatedDeliveryTime(OffsetDateTime.of(2023, 12, 24, 16, 59, 07, 0, ZoneOffset.UTC))
                .setOrderId(orderId1)
                .create();

        UUID id2 = UUID.randomUUID();
        UUID orderId2 = UUID.randomUUID();
        Delivery delivery2 = new DeliveryBuilder()
                .setId(id2)
                .setOrderId(orderId2)
                .setEstimatedDeliveryTime(OffsetDateTime.of(2020,1,1,12,0,0,0,ZoneOffset.UTC))
                .setStatus(Delivery.StatusEnum.DELIVERED)
                .create();

        UUID id3 = UUID.randomUUID();
        UUID orderId3 = UUID.randomUUID();
        Delivery delivery3 = new DeliveryBuilder()
                .setId(id3)
                .setOrderId(orderId3)
                .setStatus(Delivery.StatusEnum.DELIVERED)
                .setEstimatedPreparationFinishTime(OffsetDateTime.of(2024, 01, 10, 21, 59, 07, 0, ZoneOffset.UTC))
                .setEstimatedDeliveryTime(OffsetDateTime.of(2024, 01, 10, 21, 59, 07, 0, ZoneOffset.UTC))
                .create();

        UUID id4 = UUID.randomUUID();
        UUID orderId4 = UUID.randomUUID();
        Delivery delivery4 = new DeliveryBuilder()
                .setId(id4)
                .setOrderId(orderId4)
                .setStatus(Delivery.StatusEnum.DELIVERED)
                .setEstimatedPreparationFinishTime(OffsetDateTime.of(2024, 01, 10, 21, 59, 07, 0, ZoneOffset.UTC))
                .setEstimatedDeliveryTime(OffsetDateTime.of(2024, 01, 10, 21, 59, 07, 0, ZoneOffset.UTC))
                .create();

        List<Delivery> deliveries = new ArrayList<>();
        deliveries.add(delivery1);
        deliveries.add(delivery2);
        deliveries.add(delivery3);
        deliveries.add(delivery4);

        when(deliveryRepository.findAll()).thenReturn(deliveries);
        when(orderService.getTimeOfPlacement(orderId1)).thenReturn(new BigDecimal("1703087443059"));
        when(orderService.getTimeOfPlacement(orderId3)).thenReturn(new BigDecimal("1703087443059"));
        when(orderService.getTimeOfPlacement(orderId4)).thenReturn(null);

        long expected = 18218L;
        long actual = adminService.getPreparationTimeAnalytic(adminId, startDate, endDate);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPreparationTimeUnauthorizedTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Courier"));

        assertThrows(AccessForbiddenException.class, () -> {
            adminService.getPreparationTimeAnalytic(adminId, startDate, endDate);
        });
    }

    @Test
    void getPreparationTimeExceptionTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));

        assertThrows(BadArgumentException.class, () -> {
            adminService.getPreparationTimeAnalytic(adminId, startDate, endDate);
        });
    }

    @Test
    void getDeliveryTimeWrongTimesExceptionTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID id = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));
        assertThrows(BadArgumentException.class, () -> {
            adminService.getDeliveryTimeAnalytic(id, startDate, endDate);
        });
    }
    @Test
    void getDeliveryTimeAccessForbiddenExceptionTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 2, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID id = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Courier"));
        assertThrows(AccessForbiddenException.class, () -> {
            adminService.getDeliveryTimeAnalytic(id, startDate, endDate);
        });
    }

    @Test
    void getDeliveryTimeSuccessfulAnalyticsTest() throws ServiceUnavailableException, BadArgumentException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 5, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID id = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));

        Delivery delivery1 = new DeliveryBuilder()
                .setId(UUID.randomUUID())
                .setStatus(Delivery.StatusEnum.DELIVERED)
                .setEstimatedPreparationFinishTime(OffsetDateTime.of(2023, 1, 2, 16, 59, 07, 0, ZoneOffset.UTC))
                .setEstimatedDeliveryTime(OffsetDateTime.of(2023, 1, 2, 17, 59, 07, 0, ZoneOffset.UTC))
                .create();

        Delivery delivery2 = new DeliveryBuilder()
                .setId(UUID.randomUUID())
                .setStatus(Delivery.StatusEnum.DELIVERED)
                .setEstimatedPreparationFinishTime(OffsetDateTime.of(2023, 1, 3, 15, 30, 07, 0, ZoneOffset.UTC))
                .setEstimatedDeliveryTime(OffsetDateTime.of(2023, 1, 3, 16, 15, 07, 0, ZoneOffset.UTC))
                .create();

        Delivery delivery3 = new DeliveryBuilder()
                .setId(UUID.randomUUID())
                .setStatus(Delivery.StatusEnum.DELIVERED)
                .setEstimatedPreparationFinishTime(OffsetDateTime.of(2023, 1, 4, 16, 59, 07, 0, ZoneOffset.UTC))
                .setEstimatedDeliveryTime(OffsetDateTime.of(2023, 1, 4, 17, 29, 07, 0, ZoneOffset.UTC))
                .create();

        Delivery delivery4 = new DeliveryBuilder()
                .setId(UUID.randomUUID())
                .setStatus(Delivery.StatusEnum.DELIVERED)
                .setEstimatedPreparationFinishTime(OffsetDateTime.of(2023, 1, 2, 16, 00, 07, 0, ZoneOffset.UTC))
                .setEstimatedDeliveryTime(OffsetDateTime.of(2023, 1, 2, 16, 45, 07, 0, ZoneOffset.UTC))
                .create();

        List<Delivery> deliveries = new ArrayList<>();
        deliveries.add(delivery1);
        deliveries.add(delivery2);
        deliveries.add(delivery3);
        deliveries.add(delivery4);

        when(deliveryRepository.findAll()).thenReturn(deliveries);
        assertThat(adminService.getDeliveryTimeAnalytic(id, startDate, endDate)).isEqualTo(45);
    }

    @Test
    void getDriverEfficiencySuccessfulTest()
            throws ServiceUnavailableException, BadArgumentException, AccessForbiddenException {
        UUID id = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 5, 12, 0, 0, 0, ZoneOffset.UTC);


        Delivery delivery1 = new DeliveryBuilder()
                .setId(UUID.randomUUID())
                .setStatus(Delivery.StatusEnum.IN_TRANSIT)
                .setEstimatedPreparationFinishTime(OffsetDateTime.of(2023, 1, 2, 16, 59, 07, 0, ZoneOffset.UTC))
                .setEstimatedDeliveryTime(OffsetDateTime.of(2023, 1, 2, 17, 59, 07, 0, ZoneOffset.UTC))
                .create();

        Delivery delivery2 = new DeliveryBuilder()
                .setId(UUID.randomUUID())
                .setStatus(Delivery.StatusEnum.IN_TRANSIT)
                .setEstimatedPreparationFinishTime(OffsetDateTime.of(2023, 1, 3, 15, 30, 07, 0, ZoneOffset.UTC))
                .setEstimatedDeliveryTime(OffsetDateTime.of(2023, 1, 3, 16, 15, 07, 0, ZoneOffset.UTC))
                .create();

        Delivery delivery3 = new DeliveryBuilder()
                .setId(UUID.randomUUID())
                .setStatus(Delivery.StatusEnum.DELIVERED)
                .setEstimatedPreparationFinishTime(OffsetDateTime.of(2023, 1, 4, 16, 59, 07, 0, ZoneOffset.UTC))
                .setEstimatedDeliveryTime(OffsetDateTime.of(2023, 1, 4, 17, 29, 07, 0, ZoneOffset.UTC))
                .create();

        Delivery delivery4 = new DeliveryBuilder()
                .setId(UUID.randomUUID())
                .setStatus(Delivery.StatusEnum.DELIVERED)
                .setEstimatedPreparationFinishTime(OffsetDateTime.of(2023, 1, 2, 16, 00, 07, 0, ZoneOffset.UTC))
                .setEstimatedDeliveryTime(OffsetDateTime.of(2023, 1, 2, 16, 45, 07, 0, ZoneOffset.UTC))
                .create();
        List<Delivery> deliveries = new ArrayList<>();
        deliveries.add(delivery1);
        deliveries.add(delivery2);
        deliveries.add(delivery3);
        deliveries.add(delivery4);

        when(deliveryRepository.findAll()).thenReturn(deliveries);
        assertThat(adminService.getDriverEfficiencyAnalytic(id, startDate, endDate)).isEqualTo(50);
    }

    @Test
    void getDriverEfficiencyServiceUnavailableExceptionTest() throws ServiceUnavailableException {
        UUID id = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 5, 12, 0, 0, 0, ZoneOffset.UTC);

        when(deliveryRepository.findAll()).thenThrow(ServiceUnavailableException.class);
        assertThrows(ServiceUnavailableException.class, () -> {
            adminService.getDriverEfficiencyAnalytic(id, startDate, endDate);
        });
    }

    @Test
    void getDriverEfficiencyBadArgumentExceptionTest() {
        UUID id = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Admin"));
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2022, 1, 5, 12, 0, 0, 0, ZoneOffset.UTC);

        List<Delivery> deliveries = new ArrayList<>();

        when(deliveryRepository.findAll()).thenReturn(deliveries);
        assertThrows(BadArgumentException.class, () -> {
            adminService.getDriverEfficiencyAnalytic(id, startDate, endDate);
        });

    }
    @Test
    void getDriverEfficiencyAccessForbiddenExceptionTest() {
        UUID id = UUID.randomUUID();
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("userType", "Courier"));
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 5, 12, 0, 0, 0, ZoneOffset.UTC);

        List<Delivery> deliveries = new ArrayList<>();

        when(deliveryRepository.findAll()).thenReturn(deliveries);
        assertThrows(AccessForbiddenException.class, () -> {
            adminService.getDriverEfficiencyAnalytic(id, startDate, endDate);
        });
    }
}
