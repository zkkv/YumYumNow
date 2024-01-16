package nl.tudelft.sem.yumyumnow.delivery.domain.builders;
import net.jqwik.api.*;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryCurrentLocation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class DeliveryBuilderTest {
    @Test
    void testDeliveryBuilderConstructor() {
        DeliveryBuilder deliveryBuilder = new DeliveryBuilder();
        Delivery delivery = deliveryBuilder.create();

        assertThat(delivery).isNotNull();
        assertThat(delivery.getId()).isNull();
        assertThat(delivery.getOrderId()).isNull();
        assertThat(delivery.getCourierId()).isNull();
        assertThat(delivery.getVendorId()).isNull();
        assertThat(delivery.getStatus()).isNull();
        assertThat(delivery.getEstimatedDeliveryTime()).isNull();
        assertThat(delivery.getEstimatedPreparationFinishTime()).isNull();
    }

    @Property
    void setDeliveryIdTest(
            @ForAll("uuidProvider") UUID id
    ) {
        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .create();
        assertThat(delivery.getId()).isEqualTo(id);
    }
    @Property
    void setCourierIdTest(
            @ForAll("uuidProvider") UUID id
    ) {
        Delivery delivery = new DeliveryBuilder()
                .setCourierId(id)
                .create();
        assertThat(delivery.getCourierId()).isEqualTo(id);
    }
    @Property
    void setVendorIdTest(
            @ForAll("uuidProvider") UUID id
    ) {
        Delivery delivery = new DeliveryBuilder()
                .setVendorId(id)
                .create();
        assertThat(delivery.getVendorId()).isEqualTo(id);
    }
    @Property
    void setOrderIdTest(
            @ForAll("uuidProvider") UUID id
    ) {
        Delivery delivery = new DeliveryBuilder()
                .setOrderId(id)
                .create();
        assertThat(delivery.getOrderId()).isEqualTo(id);
    }

    @Property
    void setStatusTest(
            @ForAll Delivery.StatusEnum status
            ) {
        Delivery delivery = new DeliveryBuilder()
                .setStatus(status)
                .create();
        assertThat(delivery.getStatus()).isEqualTo(status);
    }

    @Test
    void setEstimatedDeliveryTimeTest() {
        LocalDate localDate = LocalDate.of(2023, 12, 10);
        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        OffsetDateTime date = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        Delivery delivery = new DeliveryBuilder()
                .setEstimatedDeliveryTime(date)
                .create();
        assertThat(delivery.getEstimatedDeliveryTime()).isEqualTo(date);
    }
    @Test
    void setEstimatedPreparationFinishTest() {
        LocalDate localDate = LocalDate.of(2023, 12, 10);
        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        OffsetDateTime date = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        Delivery delivery = new DeliveryBuilder()
                .setEstimatedPreparationFinishTime(date)
                .create();
        assertThat(delivery.getEstimatedPreparationFinishTime()).isEqualTo(date);
    }

    @Property
    void setCurrentLocationTest(
            @ForAll BigDecimal latitude,
            @ForAll BigDecimal longitude
    ) {
        DeliveryCurrentLocation location = new DeliveryCurrentLocation();
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        Delivery delivery = new DeliveryBuilder()
                .setCurrentLocation(location)
                .create();

        assertThat(delivery.getCurrentLocation()).isEqualTo(location);
    }

    @Property
    void createDeliveryTest(
            @ForAll("uuidProvider") UUID id,
            @ForAll BigDecimal latitude,
            @ForAll BigDecimal longitude,
            @ForAll Delivery.StatusEnum status
    ) {
        DeliveryCurrentLocation location = new DeliveryCurrentLocation();
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        LocalDate localDate = LocalDate.of(2023, 12, 10);
        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        OffsetDateTime date = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        Delivery delivery = new DeliveryBuilder()
                .setId(id)
                .setCourierId(id)
                .setVendorId(id)
                .setOrderId(id)
                .setCurrentLocation(location)
                .setEstimatedDeliveryTime(date)
                .setEstimatedPreparationFinishTime(date)
                .setStatus(status)
                .create();

        assertThat(delivery.getId()).isEqualTo(id);
        assertThat(delivery.getCourierId()).isEqualTo(id);
        assertThat(delivery.getVendorId()).isEqualTo(id);
        assertThat(delivery.getOrderId()).isEqualTo(id);
        assertThat(delivery.getCurrentLocation()).isEqualTo(location);
        assertThat(delivery.getEstimatedDeliveryTime()).isEqualTo(date);
        assertThat(delivery.getEstimatedPreparationFinishTime()).isEqualTo(date);
        assertThat(delivery.getStatus()).isEqualTo(status);
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
        LocalDate localDate = LocalDate.of(2023, 12, 10);
        LocalTime localTime = LocalTime.of(12, 0);
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        OffsetDateTime date = OffsetDateTime.of(localDate.atTime(localTime), zoneOffset);

        UUID id = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();
        UUID vendorId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        DeliveryCurrentLocation location = new DeliveryCurrentLocation();
        location.setLatitude(new BigDecimal(50));
        location.setLongitude(new BigDecimal(42));


        DeliveryBuilder builder = new DeliveryBuilder()
                .setId(id)
                .setCourierId(courierId)
                .setVendorId(vendorId)
                .setOrderId(orderId)
                .setStatus(Delivery.StatusEnum.ACCEPTED)
                .setCurrentLocation(location)
                .setEstimatedPreparationFinishTime(date)
                .setEstimatedDeliveryTime(date);

        builder.reset();

        Delivery delivery  = builder.create();

        assertThat(delivery).isNotNull();
        assertThat(delivery.getId()).isNull();
        assertThat(delivery.getOrderId()).isNull();
        assertThat(delivery.getCourierId()).isNull();
        assertThat(delivery.getVendorId()).isNull();
        assertThat(delivery.getStatus()).isNull();
        assertThat(delivery.getEstimatedDeliveryTime()).isNull();
        assertThat(delivery.getEstimatedPreparationFinishTime()).isNull();
    }

}
