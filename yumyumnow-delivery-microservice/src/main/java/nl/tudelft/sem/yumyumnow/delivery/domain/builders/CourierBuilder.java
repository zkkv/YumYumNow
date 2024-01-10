package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;

import java.util.UUID;

/**
 * Builder for Courier.
 * Uses a fluent interface to make it easier to create objects.
 */
public class CourierBuilder {
    UUID id;
    Vendor vendor;

    public CourierBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public CourierBuilder setVendor(Vendor vendor) {
        this.vendor = vendor;
        return this;
    }

    public Courier createCourier() {
        return new Courier(id, vendor);
    }
}
