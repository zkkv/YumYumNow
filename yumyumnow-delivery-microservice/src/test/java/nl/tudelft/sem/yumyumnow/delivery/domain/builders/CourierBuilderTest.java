package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import net.jqwik.api.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class CourierBuilderTest {

    @Test
    public void testConstructor() {
        CourierBuilder courierBuilder = new CourierBuilder();
        Courier courier = courierBuilder.create();

        assertThat(courier).isNotNull();
        assertThat(courier.getId()).isNull();
        assertThat(courier.getVendor()).isNull();
    }

    @Property
    void setId(
            @ForAll("uuids") UUID id
    ) {
        Courier courier = new CourierBuilder()
                .setId(id)
                .create();

        assertThat(courier.getId()).isEqualTo(id);
    }

    @Property
    void setVendor(
            @ForAll("vendors") Vendor vendor
            ) {
        Courier courier = new CourierBuilder()
                .setVendor(vendor)
                .create();

        assertThat(courier.getVendor()).isEqualTo(vendor);
    }

    @Property
    void createCourier(
            @ForAll("uuids") UUID id,
            @ForAll("vendors") Vendor vendor
            ) {
        Courier courier = new CourierBuilder()
                .setId(id)
                .setVendor(vendor)
                .create();

        assertThat(courier.getId()).isEqualTo(id);
        assertThat(courier.getVendor()).isEqualTo(vendor);
    }

    @Property
    void reset(
            @ForAll("uuids") UUID id,
            @ForAll("vendors") Vendor vendor
            ) {
        CourierBuilder courierBuilder = new CourierBuilder()
                .setId(id)
                .setVendor(vendor);

        courierBuilder.reset();

        Courier courier  = courierBuilder.create();


        assertThat(courier.getId()).isNull();
        assertThat(courier.getVendor()).isNull();
    }

    @Provide
    Arbitrary<UUID> uuids() {
        return Arbitraries.randomValue(
                (random) -> UUID.randomUUID()
        );
    }

    @Provide
    Arbitrary<Vendor> vendors() {
        return Arbitraries.randomValue(
                (random) -> new VendorBuilder()
                        .setId(UUID.randomUUID())
                        .create()
        );
    }

}
