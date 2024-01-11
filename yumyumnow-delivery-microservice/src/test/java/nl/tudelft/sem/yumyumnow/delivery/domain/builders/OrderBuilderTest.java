package nl.tudelft.sem.yumyumnow.delivery.domain.builders;


import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.UUID;

public class OrderBuilderTest {

    @Test
    void OrderBuilderConstructorTest() {
        OrderBuilder orderBuilder = new OrderBuilder();
    }

    @Test
    void setOrderIdTest() {
        UUID id = UUID.randomUUID();
        Order order = new OrderBuilder()
                .setOrderId(id)
                .createOrder();
        assertThat(order.getId()).isEqualTo(id);
    }

    @Test
    void setOrderCustomerTest() {
        Customer customer = new Customer();
        Order order = new OrderBuilder()
                .setOrderCustomer(customer)
                .createOrder();
        assertThat(order.getCustomer()).isEqualTo(customer);
    }

    @Test
    void setOrderVendorTest() {
        UUID id = UUID.randomUUID();
        Vendor vendor = new VendorBuilder()
                .setId(id)
                .createVendor();
        Order order = new OrderBuilder()
                .setOrderVendor(vendor)
                .createOrder();
        assertThat(order.getVendor()).isEqualTo(vendor);
    }

    @Test
    void createOrderTest() {
        UUID vendorId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .createVendor();
        Customer customer = new Customer();
        Order order = new OrderBuilder()
                .setOrderId(orderId)
                .setOrderCustomer(customer)
                .setOrderVendor(vendor)
                .createOrder();
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getCustomer()).isEqualTo(customer);
        assertThat(order.getVendor()).isEqualTo(vendor);
    }
}
