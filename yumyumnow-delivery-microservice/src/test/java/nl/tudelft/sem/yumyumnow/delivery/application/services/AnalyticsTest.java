package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.DeliveryBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.ServiceUnavailableException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnalyticsTest {
    private DeliveryRepository deliveryRepository;
    private DeliveryService deliveryService;
    private VendorService vendorService;
    private AdminService adminService;
    private CourierService courierService;
    private EmailService emailService;
    private OrderService orderService;

    @BeforeEach
    void setUp(){
        this.deliveryRepository = mock(DeliveryRepository.class);
        this.vendorService = mock(VendorService.class);
        this.adminService = mock(AdminService.class);
        this.courierService = mock(CourierService.class);
        this.orderService = mock(OrderService.class);
        this.emailService = mock(EmailService.class);

        deliveryService = new DeliveryService(
                deliveryRepository, vendorService, courierService, adminService, orderService,emailService);
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
            deliveryService.getPreparationTimeAnalytic(adminId, startDate, endDate);
        });
    }

    @Test
    void getDeliveryTimeWrongTimesExceptionTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 2, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID id = UUID.randomUUID();
        when(adminService.validate(id)).thenReturn(true);
        assertThrows(BadArgumentException.class, () -> {
            deliveryService.getDeliveryTimeAnalytic(id, startDate, endDate);
        });
    }
    @Test
    void getDeliveryTimeAccessForbiddenExceptionTest() throws ServiceUnavailableException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 2, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID id = UUID.randomUUID();
        when(adminService.validate(id)).thenReturn(false);
        assertThrows(AccessForbiddenException.class, () -> {
            deliveryService.getDeliveryTimeAnalytic(id, startDate, endDate);
        });
    }

    @Test
    void getDeliveryTimeSuccessfulAnalyticsTest() throws ServiceUnavailableException, BadArgumentException, AccessForbiddenException {
        OffsetDateTime startDate = OffsetDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = OffsetDateTime.of(2023, 1, 5, 12, 0, 0, 0, ZoneOffset.UTC);
        UUID id = UUID.randomUUID();
        when(adminService.validate(id)).thenReturn(true);

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
        assertThat(deliveryService.getDeliveryTimeAnalytic(id, startDate, endDate)).isEqualTo(45);
    }
}
