package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;

import java.util.UUID;

/**
 * Builder for Customer.
 * Uses a fluent interface to make it easier to create objects.
 */
public class CustomerBuilder implements Builder<Customer> {
    private UUID id;
    private String name;
    private String phoneNumber;
    private Location address;

    public CustomerBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CustomerBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public CustomerBuilder setAddress(Location address) {
        this.address = address;
        return this;
    }

    @Override
    public Customer create() {
        return new Customer(id, name, address, phoneNumber);
    }

    @Override
    public void reset() {
        id = null;
        name = null;
        phoneNumber = null;
        address = null;
    }
}
