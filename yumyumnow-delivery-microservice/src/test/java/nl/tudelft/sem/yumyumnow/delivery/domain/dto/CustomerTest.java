package nl.tudelft.sem.yumyumnow.delivery.domain.dto;

import net.jqwik.api.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CustomerBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.LocationBuilder;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;

import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

public class CustomerTest {

    @Property
    public void testEquals(@ForAll("customerProvider") Customer customer) {
        Customer customer2 = new CustomerBuilder()
                .setId(customer.getId())
                .setName(customer.getName())
                .setPhoneNumber(customer.getPhone())
                .createCustomer();

        Customer customer3 = new CustomerBuilder()
                .setId(customer.getId())
                .createCustomer();

        assertThat(customer).isEqualTo(customer);
        assertThat(customer).isEqualTo(customer2);
        assertThat(customer).isEqualTo(customer3);
    }

    @Property
    public void testNotEquals(@ForAll("customerProvider") Customer customer) {
        Customer customer2 = new CustomerBuilder()
                .setId(UUID.randomUUID())
                .setName(customer.getName())
                .setPhoneNumber(customer.getPhone())
                .createCustomer();

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
                .createCustomer();

        Customer customer3 = new CustomerBuilder()
                .setId(customer.getId())
                .createCustomer();

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
                        .createLocation()
        );

        return Combinators.combine(id, name, address, phoneNumber)
                .as(Customer::new);
    }
}