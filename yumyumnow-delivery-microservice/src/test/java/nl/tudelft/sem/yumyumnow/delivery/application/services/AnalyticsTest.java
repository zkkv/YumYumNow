package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.GlobalConfigRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnalyticsTest {
    private DeliveryRepository deliveryRepository;
    private GlobalConfigRepository globalConfigRepository;

    private DeliveryService deliveryService;
    private VendorService vendorService;
    private AdminService adminService;
    private CourierService courierService;
    private OrderService orderService;
    @Value("${globalConfigId}$")
    private UUID globalConfigId;

    @BeforeEach
    void setUp(){
        this.deliveryRepository = mock(DeliveryRepository.class);
        this.globalConfigRepository = mock(GlobalConfigRepository.class);
        this.vendorService = mock(VendorService.class);
        this.adminService = mock(AdminService.class);
        this.courierService = mock(CourierService.class);
        this.orderService = mock(OrderService.class);

        deliveryService = new DeliveryService(
                deliveryRepository, globalConfigRepository,vendorService, courierService, adminService, orderService);
    }

    @Test
    void getTotalDeliveriesSuccessfulTest() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        // setting up the deliveries
        UUID id = UUID.randomUUID();
        Delivery delivery1 = new Delivery();
        delivery1.setId(id);
        delivery1.setEstimatedDeliveryTime(OffsetDateTime.of(2022,1,1,12,0,0,0,ZoneOffset.UTC));
        id = UUID.randomUUID();
        Delivery delivery2 = new Delivery();
        delivery2.setId(id);
        delivery2.setEstimatedDeliveryTime(OffsetDateTime.of(2020,1,1,12,0,0,0,ZoneOffset.UTC));

        List<Delivery> deliveries = new ArrayList<>();
        deliveries.add(delivery1);
        deliveries.add(delivery2);

        UUID adminId = UUID.randomUUID();
        when(adminService.validate(adminId)).thenReturn(true);
        when(deliveryRepository.findAll()).thenReturn(deliveries);

        assertThat(deliveryService.getTotalDeliveriesAnalytic(adminId, startDate, endDate)).isEqualTo(1);
    }

    @Test
    void getTotalDeliveriesExceptionTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(adminService.validate(adminId)).thenReturn(true);

        assertThrows(BadArgumentException.class, () -> {
            deliveryService.getTotalDeliveriesAnalytic(adminId, startDate, endDate);
        });
    }

    @Test
    void getTotalDeliveriesUnauthorizedTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(adminService.validate(adminId)).thenReturn(false);

        assertThrows(AccessForbiddenException.class, () -> {
            deliveryService.getTotalDeliveriesAnalytic(adminId, startDate, endDate);
        });
    }

    @Test
    void getSuccessfulDeliveriesSuccessfulTest() throws BadArgumentException, ServiceUnavailableException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        // setting up the deliveries
        UUID id = UUID.randomUUID();
        Delivery delivery1 = new Delivery();
        delivery1.setId(id);
        delivery1.setEstimatedDeliveryTime(OffsetDateTime.of(2022,1,1,12,0,0,0,ZoneOffset.UTC));
        delivery1.setStatus(Delivery.StatusEnum.DELIVERED);
        id = UUID.randomUUID();
        Delivery delivery2 = new Delivery();
        delivery2.setId(id);
        delivery2.setEstimatedDeliveryTime(OffsetDateTime.of(2020,1,1,12,0,0,0,ZoneOffset.UTC));
        delivery2.setStatus(Delivery.StatusEnum.PENDING);
        id = UUID.randomUUID();
        Delivery delivery3 = new Delivery();
        delivery3.setId(id);
        delivery3.setEstimatedDeliveryTime(OffsetDateTime.of(2022,1,1,12,0,0,0,ZoneOffset.UTC));
        delivery3.setStatus(Delivery.StatusEnum.PENDING);

        List<Delivery> deliveries = new ArrayList<>();
        deliveries.add(delivery1);
        deliveries.add(delivery2);
        deliveries.add(delivery3);

        UUID adminId = UUID.randomUUID();
        when(adminService.validate(adminId)).thenReturn(true);
        when(deliveryRepository.findAll()).thenReturn(deliveries);

        assertThat(deliveryService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate)).isEqualTo(1);
    }

    @Test
    void getSuccessfulDeliveriesExceptionTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(adminService.validate(adminId)).thenReturn(true);

        assertThrows(BadArgumentException.class, () -> {
            deliveryService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate);
        });
    }

    @Test
    void getSuccessfulDeliveriesUnauthorizedTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(adminService.validate(adminId)).thenReturn(false);

        assertThrows(AccessForbiddenException.class, () -> {
            deliveryService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate);
        });
    }

    @Test
    void getPreparationTimeSuccessfulTest() throws ServiceUnavailableException, BadArgumentException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2022, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2024, 12, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(adminService.validate(adminId)).thenReturn(true);

        // setting up the deliveries
        UUID id1 = UUID.randomUUID();
        UUID orderId1 = UUID.randomUUID();
        Delivery delivery1 = new Delivery();
        delivery1.setId(id1);
        delivery1.setStatus(Delivery.StatusEnum.DELIVERED);
        delivery1.setEstimatedPreparationFinishTime(OffsetDateTime.of(2023, 12, 24, 16, 59, 07, 0, ZoneOffset.UTC));
        delivery1.setEstimatedDeliveryTime(OffsetDateTime.of(2023, 12, 24, 16, 59, 07, 0, ZoneOffset.UTC));
        delivery1.setOrderId(orderId1);

        UUID id2 = UUID.randomUUID();
        UUID orderId2 = UUID.randomUUID();
        Delivery delivery2 = new Delivery();
        delivery2.setId(id2);
        delivery2.setOrderId(orderId2);
        delivery2.setEstimatedDeliveryTime(OffsetDateTime.of(2020,1,1,12,0,0,0,ZoneOffset.UTC));
        delivery2.setStatus(Delivery.StatusEnum.DELIVERED);

        UUID id3 = UUID.randomUUID();
        UUID orderId3 = UUID.randomUUID();
        Delivery delivery3 = new Delivery();
        delivery3.setId(id3);
        delivery3.setOrderId(orderId3);
        delivery3.setStatus(Delivery.StatusEnum.DELIVERED);
        delivery3.setEstimatedPreparationFinishTime(OffsetDateTime.of(2024, 01, 10, 21, 59, 07, 0, ZoneOffset.UTC));
        delivery3.setEstimatedDeliveryTime(OffsetDateTime.of(2024, 01, 10, 21, 59, 07, 0, ZoneOffset.UTC));

        UUID id4 = UUID.randomUUID();
        UUID orderId4 = UUID.randomUUID();
        Delivery delivery4 = new Delivery();
        delivery4.setId(id4);
        delivery4.setOrderId(orderId4);
        delivery4.setStatus(Delivery.StatusEnum.DELIVERED);
        delivery4.setEstimatedPreparationFinishTime(OffsetDateTime.of(2024, 01, 10, 21, 59, 07, 0, ZoneOffset.UTC));
        delivery4.setEstimatedDeliveryTime(OffsetDateTime.of(2024, 01, 10, 21, 59, 07, 0, ZoneOffset.UTC));

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
        long actual = deliveryService.getPreparationTimeAnalytic(adminId, startDate, endDate);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getPreparationTimeUnauthorizedTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(adminService.validate(adminId)).thenReturn(false);

        assertThrows(AccessForbiddenException.class, () -> {
            deliveryService.getPreparationTimeAnalytic(adminId, startDate, endDate);
        });
    }

    @Test
    void getPreparationTimeExceptionTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID adminId = UUID.randomUUID();
        when(adminService.validate(adminId)).thenReturn(true);

        assertThrows(BadArgumentException.class, () -> {
            deliveryService.getSuccessfulDeliveriesAnalytic(adminId, startDate, endDate);
        });
    }
}
