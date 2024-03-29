package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CustomerBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.DeliveryBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.OrderBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.NoDeliveryFoundException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryCurrentLocation;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeliveryTimeTest {
    private DeliveryRepository deliveryRepository;
    private DeliveryService deliveryService;
    private OrderService orderService;
    private CustomerService userService;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        deliveryRepository = mock(DeliveryRepository.class);
        VendorService vendorService = mock(VendorService.class);
        CourierService courierService = mock(CourierService.class);
        orderService = mock(OrderService.class);
        emailService = mock(EmailService.class);
        deliveryService = new DeliveryService(deliveryRepository, vendorService, courierService, orderService, emailService);
        userService = mock(CustomerService.class);
    }

    @Test
    void successfulTest() throws Exception{
        // set up the delivery with the preparation time fixed
        UUID deliveryId = UUID.randomUUID();
        LocalDate localDate = LocalDate.of(2023, 12, 10);
        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        Delivery delivery = new DeliveryBuilder()
                .setId(deliveryId)
                .setEstimatedPreparationFinishTime(offsetDateTime)
                .create();

        // create an order
        UUID orderId = UUID.randomUUID();
        Order order = new OrderBuilder()
                .setOrderId(orderId)
                .create();
        delivery.setOrderId(orderId);

        // mock a location for a customer
        UUID customerId = UUID.randomUUID();
        Location customerLocation = new Location();
        customerLocation.setLatitude(BigDecimal.valueOf(0));
        customerLocation.setLongitude(BigDecimal.valueOf(0));
        // create a customer
        Customer customer = new CustomerBuilder()
                .setId(customerId)
                .setAddress(customerLocation)
                .create();

        order.setCustomer(customer);

        // set the location of the vendor
        DeliveryCurrentLocation vendorLocation = new DeliveryCurrentLocation();
        vendorLocation.setLatitude(BigDecimal.valueOf(1));
        vendorLocation.setLongitude(BigDecimal.valueOf(1));
        delivery.setCurrentLocation(vendorLocation);

        Optional<Delivery> optionalDelivery = Optional.of(delivery);

        when(userService.getCustomerAddress(customerId)).thenReturn(customerLocation);
        when(orderService.findOrderById(orderId)).thenReturn(order);
        when(deliveryRepository.findById(deliveryId)).thenReturn(optionalDelivery);

        Delivery newDelivery = deliveryService.addDeliveryTime(deliveryId, orderService, userService);
        OffsetDateTime expected = OffsetDateTime.parse("2023-12-10T15:08:54Z");

        assertThat(newDelivery).isNotNull();
        assertThat(newDelivery.getEstimatedDeliveryTime()).isEqualTo(expected);
    }

    @Test
    void unsuccessfulTest() throws Exception{
        // we will have some fields set to null, resulting in a bad request

        // set up the delivery with the preparation time fixed
        UUID deliveryId = UUID.randomUUID();
        LocalDate localDate = LocalDate.of(2023, 12, 10);
        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        Delivery delivery = new DeliveryBuilder()
                .setId(deliveryId)
                .setEstimatedPreparationFinishTime(offsetDateTime)
                .create();

        Optional<Delivery> optionalDelivery = Optional.of(delivery);
        when(deliveryRepository.findById(deliveryId)).thenReturn(optionalDelivery);

        assertThatThrownBy(() -> {
            deliveryService.addDeliveryTime(deliveryId, orderService, userService);
        }).isInstanceOf(BadArgumentException.class).hasMessageContaining("The order is non-existent.");
    }

    @Test
    void getDeliveryTimeHelperTest() {
        Location location1 = new Location();
        location1.setLatitude(BigDecimal.valueOf(0));
        location1.setLongitude(BigDecimal.valueOf(0));

        DeliveryCurrentLocation location2 = new DeliveryCurrentLocation();
        location2.setLatitude(BigDecimal.valueOf(1));
        location2.setLongitude(BigDecimal.valueOf(1));

        Duration actual = deliveryService.getDeliveryTimeHelper(location1, location2);
        assertThat(actual.getSeconds()).isEqualTo(11334);

        DeliveryCurrentLocation location3 = new DeliveryCurrentLocation();
        location3.setLatitude(BigDecimal.valueOf(0));
        location3.setLongitude(BigDecimal.valueOf(0));

        actual = deliveryService.getDeliveryTimeHelper(location1, location3);
        assertThat(actual.getSeconds()).isEqualTo(0);
    }

    @Test
    void deliveryDoesNotExistTest() {
        UUID deliveryId = UUID.randomUUID();
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());
        assertThrows(NoDeliveryFoundException.class, () -> deliveryService.addDeliveryTime(
                deliveryId,
                orderService,
                userService));
    }

    @Test
    void orderIsNullTest() {
        UUID deliveryId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(deliveryId)
                .create();

        UUID orderId = UUID.randomUUID();

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(orderService.findOrderById(orderId)).thenReturn(null);

        assertThrows(BadArgumentException.class, () -> deliveryService.addDeliveryTime(
                deliveryId,
                orderService,
                userService));
    }

    @Test
    void customerIsNullTest() {
        UUID deliveryId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(deliveryId)
                .create();

        UUID orderId = UUID.randomUUID();
        Order order = new OrderBuilder()
                .setOrderId(orderId)
                .create();
        delivery.setOrderId(orderId);

        order.setCustomer(null);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(orderService.findOrderById(orderId)).thenReturn(order);

        assertThrows(BadArgumentException.class, () -> deliveryService.addDeliveryTime(
                deliveryId,
                orderService,
                userService));
    }

    @Test
    void customerLocationIsNullTest() {
        UUID deliveryId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(deliveryId)
                .create();

        UUID orderId = UUID.randomUUID();
        Order order = new OrderBuilder()
                .setOrderId(orderId)
                .create();
        delivery.setOrderId(orderId);

        UUID customerId = UUID.randomUUID();
        Customer customer = new CustomerBuilder()
                .setId(customerId)
                .setAddress(null)
                .create();

        order.setCustomer(customer);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(orderService.findOrderById(orderId)).thenReturn(order);
        when(userService.getCustomerAddress(customerId)).thenReturn(null);

        assertThrows(BadArgumentException.class, () -> deliveryService.addDeliveryTime(
                deliveryId,
                orderService,
                userService));
    }

    @Test
    void vendorLocationIsNullTest() {
        UUID deliveryId = UUID.randomUUID();

        Delivery delivery = new DeliveryBuilder()
                .setId(deliveryId)
                .setCurrentLocation(null)
                .create();

        UUID orderId = UUID.randomUUID();
        Order order = new OrderBuilder()
                .setOrderId(orderId)
                .create();
        delivery.setOrderId(orderId);

        Location customerLocation = new Location();
        customerLocation.setLatitude(BigDecimal.valueOf(0));
        customerLocation.setLongitude(BigDecimal.valueOf(0));

        UUID customerId = UUID.randomUUID();
        Customer customer = new CustomerBuilder()
                .setId(customerId)
                .setAddress(customerLocation)
                .create();

        order.setCustomer(customer);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(orderService.findOrderById(orderId)).thenReturn(order);
        when(userService.getCustomerAddress(customerId)).thenReturn(customerLocation);

        assertThrows(BadArgumentException.class, () -> deliveryService.addDeliveryTime(
                deliveryId,
                orderService,
                userService));
    }
}
