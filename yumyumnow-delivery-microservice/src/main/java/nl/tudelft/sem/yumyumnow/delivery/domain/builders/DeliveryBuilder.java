package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryCurrentLocation;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;

import java.time.OffsetDateTime;
import java.util.UUID;

public class DeliveryBuilder {
    private UUID deliveryId;
    private UUID orderId;
    private UUID courierId;
    private UUID vendorId;
    private Delivery.StatusEnum status;
    private OffsetDateTime estimatedDeliveryTime;
    private OffsetDateTime estimatedPreparationFinishTime;
    private DeliveryCurrentLocation currentLocation;

    public DeliveryBuilder setId(UUID deliveryId) {
        this.deliveryId = deliveryId;
        return this;
    }

    public DeliveryBuilder setOrderId(UUID orderId) {
        this.orderId = orderId;
        return this;
    }

    public DeliveryBuilder setCourierId(UUID courierId) {
        this.courierId = courierId;
        return this;
    }

    public DeliveryBuilder setVendorId(UUID vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    public DeliveryBuilder setStatus(Delivery.StatusEnum status) {
        this.status = status;
        return this;
    }

    public DeliveryBuilder setEstimatedDeliveryTime(OffsetDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        return this;
    }

    public DeliveryBuilder setEstimatedPreparationFinishTime(OffsetDateTime estimatedPreparationFinishTime) {
        this.estimatedPreparationFinishTime = estimatedPreparationFinishTime;
        return this;
    }

    public DeliveryBuilder setCurrentLocation(DeliveryCurrentLocation location) {
        this.currentLocation = location;
        return this;
    }

    public Delivery createDelivery() {
        Delivery delivery = new Delivery();
        delivery.id(deliveryId);
        delivery.orderId(orderId);
        delivery.courierId(courierId);
        delivery.vendorId(vendorId);
        delivery.status(status);
        delivery.estimatedDeliveryTime(estimatedDeliveryTime);
        delivery.estimatedPreparationFinishTime(estimatedPreparationFinishTime);
        delivery.currentLocation(currentLocation);
        return delivery;
    }
}
