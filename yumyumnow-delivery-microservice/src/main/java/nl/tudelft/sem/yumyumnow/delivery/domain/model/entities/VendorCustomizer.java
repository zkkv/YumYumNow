package nl.tudelft.sem.yumyumnow.delivery.domain.model.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class VendorCustomizer {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID vendor_id;

    private Boolean allowsOnlyOwnCouriers;
    private Double maxDeliveryDistance;
}
