package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import net.jqwik.api.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class VendorBuilderTest {

    @Test
    void testConstructor() {
        VendorBuilder vendorBuilder = new VendorBuilder();
        Vendor vendor = vendorBuilder.create();

        assertThat(vendor).isNotNull();
        assertThat(vendor.getId()).isNull();
        assertThat(vendor.getAddress()).isNull();
        assertThat(vendor.getPhone()).isNull();
        assertThat(vendor.getAllowsOnlyOwnCouriers()).isFalse();
        assertThat(vendor.getMaxDeliveryZoneKm()).isNull();
    }

    @Property
    void setId(
            @ForAll("uuids") UUID id
    ) {
        Vendor vendor = new VendorBuilder()
                .setId(id)
                .create();

        assertThat(vendor.getId()).isEqualTo(id);
    }

    @Property
    void setAddress(
            @ForAll BigDecimal latitude,
            @ForAll BigDecimal longitude
            ) {
        Location address = new Location();
        address.setLatitude(latitude);
        address.setLongitude(longitude);
        address.setTimestamp(OffsetDateTime.now());

        Vendor vendor = new VendorBuilder()
                .setAddress(address)
                .create();

        assertThat(vendor.getAddress()).isEqualTo(address);
    }

    @Property
    void setPhoneNumber(
            @ForAll String phoneNumber
            ) {
        Vendor vendor = new VendorBuilder()
                .setPhoneNumber(phoneNumber)
                .create();

        assertThat(vendor.getPhone()).isEqualTo(phoneNumber);
    }

    @Property
    void setAllowsOnlyOwnCouriers(
            @ForAll boolean allowsOnlyOwnCouriers
            ) {
        Vendor vendor = new VendorBuilder()
                .setAllowsOnlyOwnCouriers(allowsOnlyOwnCouriers)
                .create();

        assertThat(vendor.getAllowsOnlyOwnCouriers()).isEqualTo(allowsOnlyOwnCouriers);
    }

    @Property
    void setMaxDeliveryZoneKm(
            @ForAll BigDecimal maxDeliveryZoneKm
            ) {
        Vendor vendor = new VendorBuilder()
                .setMaxDeliveryZoneKm(maxDeliveryZoneKm)
                .create();

        assertThat(vendor.getMaxDeliveryZoneKm()).isEqualTo(maxDeliveryZoneKm);
    }

    @Property
    void createVendor(
            @ForAll("uuids") UUID id,
            @ForAll BigDecimal latitude,
            @ForAll BigDecimal longitude,
            @ForAll String phoneNumber,
            @ForAll boolean allowsOnlyOwnCouriers,
            @ForAll BigDecimal maxDeliveryZoneKm
            ) {
        Location address = new Location();
        address.setLatitude(latitude);
        address.setLongitude(longitude);
        address.setTimestamp(OffsetDateTime.now());

        Vendor vendor = new VendorBuilder()
                .setId(id)
                .setAddress(address)
                .setPhoneNumber(phoneNumber)
                .setAllowsOnlyOwnCouriers(allowsOnlyOwnCouriers)
                .setMaxDeliveryZoneKm(maxDeliveryZoneKm)
                .create();

        assertThat(vendor.getId()).isEqualTo(id);
        assertThat(vendor.getAddress()).isEqualTo(address);
        assertThat(vendor.getPhone()).isEqualTo(phoneNumber);
        assertThat(vendor.getAllowsOnlyOwnCouriers()).isEqualTo(allowsOnlyOwnCouriers);
        assertThat(vendor.getMaxDeliveryZoneKm()).isEqualTo(maxDeliveryZoneKm);
    }

    @Property
    void reset(
            @ForAll("uuids") UUID id,
            @ForAll BigDecimal latitude,
            @ForAll BigDecimal longitude,
            @ForAll String phoneNumber,
            @ForAll boolean allowsOnlyOwnCouriers,
            @ForAll BigDecimal maxDeliveryZoneKm
            ) {
        Location address = new Location();
        address.setLatitude(latitude);
        address.setLongitude(longitude);
        address.setTimestamp(OffsetDateTime.now());

        VendorBuilder vendorBuilder = new VendorBuilder()
                .setId(id)
                .setAddress(address)
                .setPhoneNumber(phoneNumber)
                .setAllowsOnlyOwnCouriers(allowsOnlyOwnCouriers)
                .setMaxDeliveryZoneKm(maxDeliveryZoneKm);

        vendorBuilder.reset();

        Vendor vendor  = vendorBuilder.create();


        assertThat(vendor.getId()).isNull();
        assertThat(vendor.getAddress()).isNull();
        assertThat(vendor.getPhone()).isNull();
        assertThat(vendor.getAllowsOnlyOwnCouriers()).isFalse();
        assertThat(vendor.getMaxDeliveryZoneKm()).isNull();
    }

    @Provide
    Arbitrary<UUID> uuids() {
        return Arbitraries
                .longs()
                .tuple2()
                .map(longs -> new UUID(longs.get1(), longs.get2()));
    }
}
