package nl.tudelft.sem.yumyumnow.delivery.domain.builders;


import net.jqwik.api.Property;
import net.jqwik.api.*;
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
        Order order = orderBuilder.create();
        assertThat(order).isNotNull();
        assertThat(order.getId()).isNull();
        assertThat(order.getVendor()).isNull();
        assertThat(order.getCustomer()).isNull();
    }

    @Property
    void setOrderIdTest(
            @ForAll("uuidProvider") UUID id
    ) {
        Order order = new OrderBuilder()
                .setOrderId(id)
                .create();

        assertThat(order.getId()).isEqualTo(id);
    }

    @Property
    void setOrderCustomerTest(
            @ForAll("uuidProvider") UUID id
    ) {
        Customer customer = new CustomerBuilder()
                .setId(id)
                .create();

        Order order = new OrderBuilder()
                .setOrderCustomer(customer)
                .create();

        assertThat(order.getCustomer()).isEqualTo(customer);
    }

    @Property
    void setOrderVendorTest(
            @ForAll("uuidProvider") UUID id
    ) {
        Vendor vendor = new VendorBuilder()
                .setId(id)
                .create();

        Order order = new OrderBuilder()
                .setOrderVendor(vendor)
                .create();

        assertThat(order.getVendor()).isEqualTo(vendor);
    }

    @Property
    void createOrderTest(
            @ForAll("uuidProvider") UUID id
    ) {
        Vendor vendor = new VendorBuilder()
                .setId(id)
                .create();

        Customer customer = new CustomerBuilder()
                .setId(id)
                .create();

        Order order = new OrderBuilder()
                .setOrderId(id)
                .setOrderCustomer(customer)
                .setOrderVendor(vendor)
                .create();

        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getCustomer()).isEqualTo(customer);
        assertThat(order.getVendor()).isEqualTo(vendor);
    }

    @Provide
    Arbitrary<UUID> uuidProvider() {
        return Arbitraries
                .longs()
                .tuple2()
                .map(longs -> new UUID(longs.get1(), longs.get2()));
    }
    @Test
    void resetTest() {
        UUID vendorId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Vendor vendor = new VendorBuilder()
                .setId(vendorId)
                .create();
        Customer customer = new CustomerBuilder()
                .setId(customerId)
                .create();
        OrderBuilder orderBuilder = new OrderBuilder()
                .setOrderId(orderId)
                .setOrderCustomer(customer)
                .setOrderVendor(vendor);
        orderBuilder.reset();
        Order order = orderBuilder.create();
        assertThat(order.getId()).isNull();
        assertThat(order.getCustomer()).isNull();
        assertThat(order.getVendor()).isNull();
    }
}
