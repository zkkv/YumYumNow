package nl.tudelft.sem.yumyumnow.delivery.domain.model.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 *  Each object of the class represent a mapping between a courier and a delivery.
 *
 * @author Kirill Zhankov
 */
@Entity
@IdClass(CourierToDelivery.CourierToDeliveryPrimaryKey.class)
public class CourierToDelivery {

    @EmbeddedId
    private CourierToDeliveryPrimaryKey courierIdDeliveryId;

    public CourierToDelivery(UUID courierId, UUID deliveryId) {
        courierIdDeliveryId = new CourierToDeliveryPrimaryKey(courierId, deliveryId);
    }

    public CourierToDelivery() {
    }

    public UUID getCourierId() {
        return courierIdDeliveryId.courierId;
    }

    public void setCourierId(UUID courierId) {
        this.courierIdDeliveryId.courierId = courierId;
    }

    public UUID getDeliveryId() {
        return courierIdDeliveryId.deliveryId;
    }

    public void setDeliveryId(UUID deliveryId) {
        this.courierIdDeliveryId.deliveryId = deliveryId;
    }

    @Embeddable
    public static class CourierToDeliveryPrimaryKey implements Serializable {

        @GeneratedValue(generator = "UUID")
        protected UUID courierId;

        @GeneratedValue(generator = "UUID")
        protected UUID deliveryId;

        public CourierToDeliveryPrimaryKey() {}

        public CourierToDeliveryPrimaryKey(UUID courierId, UUID deliveryId) {
            this.courierId = courierId;
            this.deliveryId = deliveryId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CourierToDeliveryPrimaryKey that = (CourierToDeliveryPrimaryKey)o;
            return Objects.equals(courierId, that.courierId) && Objects.equals(deliveryId, that.deliveryId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(courierId, deliveryId);
        }
    }

}
