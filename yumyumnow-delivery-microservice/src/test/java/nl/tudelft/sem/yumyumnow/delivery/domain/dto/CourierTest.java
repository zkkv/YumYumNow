package nl.tudelft.sem.yumyumnow.delivery.domain.dto;

import static org.assertj.core.api.Assertions.*;

import net.jqwik.api.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.CourierBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

public class CourierTest {

    @Property
    public void testEquals(@ForAll("courierProvider") Courier courier) {
        Courier courier2 = new CourierBuilder()
                .setId(courier.getId())
                .setVendor(courier.getVendor())
                .create();

        Courier courier3 = new CourierBuilder()
                .setId(courier.getId())
                .create();

        assertThat(courier).isEqualTo(courier);
        assertThat(courier).isEqualTo(courier2);
        assertThat(courier).isEqualTo(courier3);
    }

    @Test
    public void testNotEqualsWithNull() {
        Courier courier = new CourierBuilder()
                .setId(UUID.randomUUID())
                .create();

        Object o = null;

        assertNotEquals(courier, o);
    }

    @Test
    public void testNotEqualsWithOtherClass() {
        Courier courier = new CourierBuilder()
                .setId(UUID.randomUUID())
                .create();

        Object o = new Object();

        assertNotEquals(courier, o);
    }

    @Property
    public void testNotEquals(@ForAll("courierProvider") Courier courier) {
        Courier courier2 = new CourierBuilder()
                .setId(UUID.randomUUID())
                .setVendor(courier.getVendor())
                .create();

        assertThat(courier).isNotEqualTo(courier2);
        assertThat(courier).isNotEqualTo(null);
    }

    @Property
    public void testHashCode(
            @ForAll("courierProvider") Courier courier) {
        Courier courier2 = new CourierBuilder()
                .setId(courier.getId())
                .setVendor(courier.getVendor())
                .create();

        Courier courier3 = new CourierBuilder()
                .setId(courier.getId())
                .create();

        assertThat(courier.hashCode()).isEqualTo(courier2.hashCode());
        assertThat(courier.hashCode()).isEqualTo(courier3.hashCode());
    }

    @Provide
    public Arbitrary<Courier> courierProvider() {
        Arbitrary<UUID> id = Arbitraries
                .longs()
                .tuple2()
                .map(longs -> new UUID(longs.get1(), longs.get2()));

        Arbitrary<Vendor> vendor = Arbitraries
                .longs()
                .tuple2()
                .map(longs -> new VendorBuilder()
                        .setId(new UUID(longs.get1(), longs.get2()))
                        .create());

        return Combinators.combine(id, vendor)
                .as(Courier::new);
    }

    @Test
    void courierToStringTest() {
        UUID id = UUID.randomUUID();
        Courier courier = new CourierBuilder()
                .setId(id)
                .create();

        String idString = id.toString();

        String unformatted = """
                class Courier {
                    id: %s
                    vendor: null
                }""";
        String expected = String.format(unformatted, idString);
        assertEquals(expected, courier.toString());
    }
}
