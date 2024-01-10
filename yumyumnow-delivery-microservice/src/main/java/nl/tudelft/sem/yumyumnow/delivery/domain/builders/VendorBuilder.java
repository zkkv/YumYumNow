package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;

import java.math.BigDecimal;
import java.util.UUID;

public class VendorBuilder {
    private UUID id;
    private Location address;
    private String phoneNumber;
    private boolean allowsOnlyOwnCouriers;
    private BigDecimal maxDeliveryZoneKm;

    public VendorBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public VendorBuilder setAddress(Location address) {
        this.address = address;
        return this;
    }

    public VendorBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public VendorBuilder setAllowsOnlyOwnCouriers(boolean allowsOnlyOwnCouriers) {
        this.allowsOnlyOwnCouriers = allowsOnlyOwnCouriers;
        return this;
    }

    public VendorBuilder setMaxDeliveryZoneKm(BigDecimal maxDeliveryZoneKm) {
        this.maxDeliveryZoneKm = maxDeliveryZoneKm;
        return this;
    }

    public Vendor createVendor() {
        return new Vendor(id, address, phoneNumber, allowsOnlyOwnCouriers, maxDeliveryZoneKm);
    }
}
