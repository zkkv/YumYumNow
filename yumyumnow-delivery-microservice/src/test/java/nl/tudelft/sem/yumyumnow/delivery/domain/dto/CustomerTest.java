package nl.tudelft.sem.yumyumnow.delivery.domain.dto;

import net.jqwik.api.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CourierBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CustomerBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.LocationBuilder;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CustomerTest {

    @Property
    public void testEquals(@ForAll("customerProvider") Customer customer) {
        Customer customer2 = new CustomerBuilder()
                .setId(customer.getId())
                .setName(customer.getName())
                .setPhoneNumber(customer.getPhone())
                .create();

        Customer customer3 = new CustomerBuilder()
                .setId(customer.getId())
                .create();

        assertThat(customer).isEqualTo(customer);
        assertThat(customer).isEqualTo(customer2);
        assertThat(customer).isEqualTo(customer3);
    }

    @Test
    public void testNotEqualsWithNull() {
        Customer customer = new CustomerBuilder()
                .setId(UUID.randomUUID())
                .create();

        Object o = null;

        assertNotEquals(customer, o);
    }

    @Test
    public void testNotEqualsWithOtherClass() {
        Customer customer = new CustomerBuilder()
                .setId(UUID.randomUUID())
                .create();

        Object o = new Object();

        assertNotEquals(customer, o);
    }

    @Property
    public void testNotEquals(@ForAll("customerProvider") Customer customer) {
        Customer customer2 = new CustomerBuilder()
                .setId(UUID.randomUUID())
                .setName(customer.getName())
                .setPhoneNumber(customer.getPhone())
                .create();

        assertThat(customer).isNotEqualTo(customer2);
        assertThat(customer).isNotEqualTo(null);
    }

    @Property
    public void testHashCode(
            @ForAll("customerProvider") Customer customer) {
        Customer customer2 = new CustomerBuilder()
                .setId(customer.getId())
                .setName(customer.getName())
                .setPhoneNumber(customer.getPhone())
                .create();

        Customer customer3 = new CustomerBuilder()
                .setId(customer.getId())
                .create();

        assertThat(customer.hashCode()).isEqualTo(customer2.hashCode());
        assertThat(customer.hashCode()).isEqualTo(customer3.hashCode());
    }

    @Provide
    public Arbitrary<Customer> customerProvider() {
        Arbitrary<UUID> id = Arbitraries
                .longs()
                .tuple2()
                .map(tuple -> new UUID(tuple.get1(), tuple.get2()));

        Arbitrary<String> name = Arbitraries.strings();

        Arbitrary<String> phoneNumber = Arbitraries.strings();

        Arbitrary<Location> address = Arbitraries.randomValue(
                (random) -> new LocationBuilder()
                        .setLatitude(BigDecimal.valueOf(random.nextDouble()))
                        .setLongitude(BigDecimal.valueOf(random.nextDouble()))
                        .create()
        );

        return Combinators.combine(id, name, address, phoneNumber)
                .as(Customer::new);
    }

    @Test
    void customerToStringTest() {
        UUID id = UUID.randomUUID();
        Customer customer = new CustomerBuilder()
                .setId(id)
                .create();

        String idString = id.toString();

        String unformatted = """
                class Customer {
                    id: %s
                    name: null
                    deliveryAddress: null
                    phone: null
                }""";
        String expected = String.format(unformatted, idString);
        assertEquals(expected, customer.toString());
    }
}
