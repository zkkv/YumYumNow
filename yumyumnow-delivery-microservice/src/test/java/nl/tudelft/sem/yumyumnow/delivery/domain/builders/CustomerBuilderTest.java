package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import net.jqwik.api.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class CustomerBuilderTest {
    @Test
    public void testConstructor() {
        CustomerBuilder customerBuilder = new CustomerBuilder();
        Customer customer = customerBuilder.create();

        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isNull();
        assertThat(customer.getName()).isNull();
        assertThat(customer.getPhone()).isNull();
        assertThat(customer.getDeliveryAddress()).isNull();
    }

    @Property
    public void setId(
            @ForAll("uuids") UUID id
    ) {
        Customer customer = new CustomerBuilder()
                .setId(id)
                .create();

        assertThat(customer.getId()).isEqualTo(id);
    }

    @Property
    public void setName(
            @ForAll String name
    ) {
        Customer customer = new CustomerBuilder()
                .setName(name)
                .create();

        assertThat(customer.getName()).isEqualTo(name);
    }

    @Property
    public void setPhoneNumber(
            @ForAll String phoneNumber
    ) {
        Customer customer = new CustomerBuilder()
                .setPhoneNumber(phoneNumber)
                .create();

        assertThat(customer.getPhone()).isEqualTo(phoneNumber);
    }

    @Property
    public void setAddress(
            @ForAll BigDecimal latitude,
            @ForAll BigDecimal longitude
    ) {
        Location address = new Location();

        address.setLatitude(latitude);
        address.setLongitude(longitude);

        Customer customer = new CustomerBuilder()
                .setAddress(address)
                .create();

        assertThat(customer.getDeliveryAddress()).isEqualTo(address);
    }

    @Property
    public void createCustomer(
            @ForAll("uuids") UUID id,
            @ForAll String name,
            @ForAll String phoneNumber,
            @ForAll BigDecimal latitude,
            @ForAll BigDecimal longitude
    ) {
        Location address = new Location();

        address.setLatitude(latitude);
        address.setLongitude(longitude);

        Customer customer = new CustomerBuilder()
                .setId(id)
                .setName(name)
                .setPhoneNumber(phoneNumber)
                .setAddress(address)
                .create();

        assertThat(customer.getId()).isEqualTo(id);
        assertThat(customer.getName()).isEqualTo(name);
        assertThat(customer.getPhone()).isEqualTo(phoneNumber);
        assertThat(customer.getDeliveryAddress()).isEqualTo(address);
    }

    @Property
    public void reset(
            @ForAll("uuids") UUID id,
            @ForAll String name,
            @ForAll String phoneNumber,
            @ForAll BigDecimal latitude,
            @ForAll BigDecimal longitude
    ) {
        Location address = new Location();

        address.setLatitude(latitude);
        address.setLongitude(longitude);

        CustomerBuilder customerBuilder = new CustomerBuilder()
                .setId(id)
                .setName(name)
                .setPhoneNumber(phoneNumber)
                .setAddress(address);

        customerBuilder.reset();

        Customer customer  = customerBuilder.create();


        assertThat(customer.getId()).isNull();
        assertThat(customer.getName()).isNull();
        assertThat(customer.getPhone()).isNull();
        assertThat(customer.getDeliveryAddress()).isNull();
    }

    @Provide
    public Arbitrary<UUID> uuids() {
        return Arbitraries.randomValue(
                (random) -> UUID.randomUUID()
        );
    }
}
