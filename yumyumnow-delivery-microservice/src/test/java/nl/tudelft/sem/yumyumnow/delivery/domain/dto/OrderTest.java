package nl.tudelft.sem.yumyumnow.delivery.domain.dto;

import net.jqwik.api.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CustomerBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.OrderBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderTest {
    @Property
    public void testEquals(@ForAll("orderProvider") Order order) {
        Order order2 = new OrderBuilder()
                .setOrderId(order.getId())
                .setOrderCustomer(order.getCustomer())
                .setOrderVendor(order.getVendor())
                .createOrder();

        Order order3 = new OrderBuilder()
                .setOrderId(order.getId())
                .createOrder();

        assertThat(order).isEqualTo(order);
        assertThat(order).isEqualTo(order2);
        assertThat(order).isEqualTo(order3);
    }

    @Property
    public void testNotEquals(@ForAll("orderProvider") Order order) {
        Order order2 = new OrderBuilder()
                .setOrderId(UUID.randomUUID())
                .setOrderCustomer(order.getCustomer())
                .setOrderVendor(order.getVendor())
                .createOrder();

        assertThat(order).isNotEqualTo(order2);
        assertThat(order).isNotEqualTo(null);
    }

    @Property
    public void testHashCode(
            @ForAll("orderProvider") Order order) {
        Order order2 = new OrderBuilder()
                .setOrderId(order.getId())
                .setOrderCustomer(order.getCustomer())
                .setOrderVendor(order.getVendor())
                .createOrder();

        Order order3 = new OrderBuilder()
                .setOrderId(order.getId())
                .createOrder();

        assertThat(order.hashCode()).isEqualTo(order2.hashCode());
        assertThat(order.hashCode()).isEqualTo(order3.hashCode());
    }

    @Provide
    public Arbitrary<Order> orderProvider() {
        Arbitrary<UUID> id = Arbitraries.randomValue(
                (random) -> UUID.randomUUID()
        );

        Arbitrary<Vendor> vendor = Arbitraries.randomValue(
                (random) -> new VendorBuilder()
                        .setId(UUID.randomUUID())
                        .createVendor()
        );

        Arbitrary<Customer> customer = Arbitraries.randomValue(
                (random) -> new CustomerBuilder()
                        .setId(UUID.randomUUID())
                        .createCustomer()
        );



        return Combinators.combine(id, vendor, customer).as(Order::new);
    }

}
