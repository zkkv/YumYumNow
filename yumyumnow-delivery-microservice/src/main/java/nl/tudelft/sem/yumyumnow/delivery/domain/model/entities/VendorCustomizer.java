package nl.tudelft.sem.yumyumnow.delivery.domain.model.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

/**
 * VendorCustomizer entity
 * - vendor_id: UUID (primary key)
 * - allowsOnlyOwnCouriers: Boolean (whether the vendor only allows their own couriers to deliver their orders)
 * - maxDeliveryDistance: Double (maximum distance from the vendor to the customer)
 */
@Entity
public class VendorCustomizer {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID vendor_id;

    private Boolean allowsOnlyOwnCouriers;
    private Double maxDeliveryDistance;
}
