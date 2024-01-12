package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;

import java.util.UUID;

/**
 * Builder for Courier.
 * Uses a fluent interface to make it easier to create objects.
 */
public class CourierBuilder implements Builder<Courier> {
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

    @Override
    public Courier create() {
        return new Courier(id, vendor);
    }

    @Override
    public void reset() {
        id = null;
        vendor = null;
    }
}
