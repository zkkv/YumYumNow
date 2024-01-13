package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryCurrentLocation;
import java.time.OffsetDateTime;
import java.util.UUID;

public class DeliveryBuilder implements Builder<Delivery> {
    private UUID deliveryId;
    private UUID orderId;
    private UUID courierId;
    private UUID vendorId;
    private Delivery.StatusEnum status;
    private OffsetDateTime estimatedDeliveryTime;
    private OffsetDateTime estimatedPreparationFinishTime;
    private DeliveryCurrentLocation currentLocation;

    /**
     * A setter method for the id field of a DeliveryBuilder.
     *
     * @param deliveryId the id to be set as the Delivery id
     * @return the DeliveryBuilder with the id field changed
     */
    public DeliveryBuilder setId(UUID deliveryId) {
        this.deliveryId = deliveryId;
        return this;
    }

    /**
     * A setter method for the orderId field of a DeliveryBuilder.
     *
     * @param orderId the id to be set as the orderId
     * @return the DeliveryBuilder with the orderId field changed
     */
    public DeliveryBuilder setOrderId(UUID orderId) {
        this.orderId = orderId;
        return this;
    }

    /**
     * A setter method for the courierId field of a DeliveryBuilder.
     *
     * @param courierId the id to be set as the courierId
     * @return the DeliveryBuilder with the courierId field changed
     */
    public DeliveryBuilder setCourierId(UUID courierId) {
        this.courierId = courierId;
        return this;
    }

    /**
     * A setter method for the vendorId field of a DeliveryBuilder.
     *
     * @param vendorId the id to be set as the vendorId
     * @return the DeliveryBuilder with the vendorId field changed
     */
    public DeliveryBuilder setVendorId(UUID vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    /**
     * A setter method for the status field of a DeliveryBuilder.
     *
     * @param status the StatusEnum to be set as the delivery status
     * @return the DeliveryBuilder with the status field changed
     */
    public DeliveryBuilder setStatus(Delivery.StatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * A setter method for the estimatedDeliveryTime field of a DeliveryBuilder.
     *
     * @param estimatedDeliveryTime the time to be set as the estimated delivery time
     * @return the DeliveryBuilder with the estimatedDeliveryTime field changed
     */
    public DeliveryBuilder setEstimatedDeliveryTime(OffsetDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        return this;
    }

    /**
     * A setter method for the estimatedPreparationFinishTime field of a DeliveryBuilder.
     *
     * @param estimatedPreparationFinishTime the time to be set as the estimated preparation finish time
     * @return the DeliveryBuilder with the estimatedPreparationFinishTime field changed
     */
    public DeliveryBuilder setEstimatedPreparationFinishTime(OffsetDateTime estimatedPreparationFinishTime) {
        this.estimatedPreparationFinishTime = estimatedPreparationFinishTime;
        return this;
    }

    /**
     * A setter method for the currentLocation field of a DeliveryBuilder.
     *
     * @param location the location to be set as the currentLocation
     * @return the DeliveryBuilder with the currentLocation field changed
     */
    public DeliveryBuilder setCurrentLocation(DeliveryCurrentLocation location) {
        this.currentLocation = location;
        return this;
    }

    /**
     * A method that creates a Delivery using all the fields of the DeliveryBuilder instance.
     *
     * @return a new Delivery with all the fields set to the DeliveryBuilder fields
     */
    public Delivery create() {
        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setOrderId(orderId);
        delivery.setCourierId(courierId);
        delivery.setVendorId(vendorId);
        delivery.setStatus(status);
        delivery.setEstimatedDeliveryTime(estimatedDeliveryTime);
        delivery.setEstimatedPreparationFinishTime(estimatedPreparationFinishTime);
        delivery.setCurrentLocation(currentLocation);
        return delivery;
    }

    @Override
    public void reset() {
        deliveryId = null;
        orderId = null;
        courierId = null;
        vendorId = null;
        status = null;
        estimatedDeliveryTime = null;
        estimatedPreparationFinishTime = null;
        currentLocation = null;
    }
}
