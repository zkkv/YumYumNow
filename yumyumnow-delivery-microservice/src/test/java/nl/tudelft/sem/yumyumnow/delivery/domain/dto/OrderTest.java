package nl.tudelft.sem.yumyumnow.delivery.domain.dto;

import net.jqwik.api.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CourierBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CustomerBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.OrderBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class OrderTest {
    @Property
    public void testEquals(@ForAll("orderProvider") Order order) {
        Order order2 = new OrderBuilder()
                .setOrderId(order.getId())
                .setOrderCustomer(order.getCustomer())
                .setOrderVendor(order.getVendor())
                .create();

        Order order3 = new OrderBuilder()
                .setOrderId(order.getId())
                .create();

        assertThat(order).isEqualTo(order);
        assertThat(order).isEqualTo(order2);
        assertThat(order).isEqualTo(order3);
    }

    @Test
    public void testNotEqualsWithNull() {
        Order order = new OrderBuilder()
                .setOrderId(UUID.randomUUID())
                .create();

        Object o = null;

        assertNotEquals(order, o);
    }

    @Test
    public void testNotEqualsWithOtherClass() {
        Order order = new OrderBuilder()
                .setOrderId(UUID.randomUUID())
                .create();

        Object o = new Object();

        assertNotEquals(order, o);
    }

    @Property
    public void testNotEquals(@ForAll("orderProvider") Order order) {
        Order order2 = new OrderBuilder()
                .setOrderId(UUID.randomUUID())
                .setOrderCustomer(order.getCustomer())
                .setOrderVendor(order.getVendor())
                .create();

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
                .create();

        Order order3 = new OrderBuilder()
                .setOrderId(order.getId())
                .create();

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
                        .create()
        );

        Arbitrary<Customer> customer = Arbitraries.randomValue(
                (random) -> new CustomerBuilder()
                        .setId(UUID.randomUUID())
                        .create()
        );



        return Combinators.combine(id, vendor, customer).as(Order::new);
    }

    @Test
    void orderToStringTest() {
        UUID id = UUID.randomUUID();
        Order order = new OrderBuilder()
                .setOrderId(id)
                .create();

        String idString = id.toString();

        String unformatted = """
                class Order {
                    id: %s
                    vendor: null
                }""";
        String expected = String.format(unformatted, idString);
        assertEquals(expected, order.toString());
    }

}
