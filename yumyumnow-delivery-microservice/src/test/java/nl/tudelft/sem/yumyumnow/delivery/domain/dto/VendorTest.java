package nl.tudelft.sem.yumyumnow.delivery.domain.dto;

import net.jqwik.api.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.LocationBuilder;
import nl.tudelft.sem.yumyumnow.delivery.domain.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class VendorTest {

    @Property
    public void testEquals(
            @ForAll("vendorProvider") Vendor vendor) {
        Vendor vendor2 = new VendorBuilder()
                .setId(vendor.getId())
                .setAddress(vendor.getAddress())
                .setPhoneNumber(vendor.getPhone())
                .setMaxDeliveryZoneKm(vendor.getMaxDeliveryZoneKm())
                .setAllowsOnlyOwnCouriers(vendor.getAllowsOnlyOwnCouriers())
                .createVendor();

        Vendor vendor3 = new VendorBuilder()
                .setId(vendor.getId())
                .createVendor();

        assertThat(vendor).isEqualTo(vendor);
        assertThat(vendor).isEqualTo(vendor2);
        assertThat(vendor).isEqualTo(vendor3);
    }

    @Property
    public void testNotEquals(
            @ForAll("vendorProvider") Vendor vendor) {
        Vendor vendor2 = new VendorBuilder()
                .setId(UUID.randomUUID())
                .setAddress(vendor.getAddress())
                .setPhoneNumber(vendor.getPhone())
                .setMaxDeliveryZoneKm(vendor.getMaxDeliveryZoneKm())
                .setAllowsOnlyOwnCouriers(vendor.getAllowsOnlyOwnCouriers())
                .createVendor();

        assertThat(vendor).isNotEqualTo(vendor2);
        assertThat(vendor).isNotEqualTo(null);
    }

    @Property
    public void testHashCode(
            @ForAll("vendorProvider") Vendor vendor) {
        Vendor vendor2 = new VendorBuilder()
                .setId(vendor.getId())
                .setAddress(vendor.getAddress())
                .setPhoneNumber(vendor.getPhone())
                .setMaxDeliveryZoneKm(vendor.getMaxDeliveryZoneKm())
                .setAllowsOnlyOwnCouriers(vendor.getAllowsOnlyOwnCouriers())
                .createVendor();

        Vendor vendor3 = new VendorBuilder()
                .setId(vendor.getId())
                .createVendor();

        assertThat(vendor.hashCode()).isEqualTo(vendor2.hashCode());
        assertThat(vendor.hashCode()).isEqualTo(vendor3.hashCode());
    }

    @Provide
    public Arbitrary<Vendor> vendorProvider() {
        Arbitrary<UUID> id = Arbitraries
                .longs()
                .tuple2()
                .map(tuple -> new UUID(tuple.get1(), tuple.get2()));

        Arbitrary<Location> address = Arbitraries
                .longs()
                .tuple2()
                .map(tuple -> new LocationBuilder()
                        .setLatitude(BigDecimal.valueOf(tuple.get1()))
                        .setLongitude(BigDecimal.valueOf(tuple.get2()))
                        .setTimestamp(OffsetDateTime.now())
                        .createLocation());

        Arbitrary<String> phoneNumber = Arbitraries.strings();

        Arbitrary<Boolean> allowsOnlyOwnCouriers = Arbitraries.integers()
                .map(integer -> integer % 2 == 0);

        Arbitrary<BigDecimal> maxDeliveryZoneKm = Arbitraries
                .longs()
                .map(BigDecimal::valueOf);

        return Combinators
                .combine(id, address, phoneNumber, allowsOnlyOwnCouriers, maxDeliveryZoneKm)
                .as(Vendor::new);
    }
}
