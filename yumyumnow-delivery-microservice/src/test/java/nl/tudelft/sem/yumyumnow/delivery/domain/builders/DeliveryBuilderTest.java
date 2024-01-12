package nl.tudelft.sem.yumyumnow.delivery.domain.builders;
import net.jqwik.api.*;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryCurrentLocation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class DeliveryBuilderTest {
    @Test
    void testDeliveryBuilderConstructor() {
        DeliveryBuilder deliveryBuilder = new DeliveryBuilder();
        Delivery delivery = deliveryBuilder.createDelivery();

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
                .createDelivery();
        assertThat(delivery.getId()).isEqualTo(id);
    }
    @Property
    void setCourierIdTest(
            @ForAll("uuidProvider") UUID id
    ) {
        Delivery delivery = new DeliveryBuilder()
                .setCourierId(id)
                .createDelivery();
        assertThat(delivery.getCourierId()).isEqualTo(id);
    }
    @Property
    void setVendorIdTest(
            @ForAll("uuidProvider") UUID id
    ) {
        Delivery delivery = new DeliveryBuilder()
                .setVendorId(id)
                .createDelivery();
        assertThat(delivery.getVendorId()).isEqualTo(id);
    }
    @Property
    void setOrderIdTest(
            @ForAll("uuidProvider") UUID id
    ) {
        Delivery delivery = new DeliveryBuilder()
                .setOrderId(id)
                .createDelivery();
        assertThat(delivery.getOrderId()).isEqualTo(id);
    }

    @Property
    void setStatusTest(
            @ForAll Delivery.StatusEnum status
            ) {
        Delivery delivery = new DeliveryBuilder()
                .setStatus(status)
                .createDelivery();
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
                .createDelivery();
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
                .createDelivery();
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
                .createDelivery();

        assertThat(delivery.getCurrentLocation()).isEqualTo(location);
    }

    @Provide
    Arbitrary<UUID> uuidProvider() {
        return Arbitraries
                .longs()
                .tuple2()
                .map(longs -> new UUID(longs.get1(), longs.get2()));
    }

}
