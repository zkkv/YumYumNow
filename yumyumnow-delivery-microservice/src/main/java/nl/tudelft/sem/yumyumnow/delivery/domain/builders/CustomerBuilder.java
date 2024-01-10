package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;

import java.util.UUID;

/**
 * Builder for Customer.
 * Uses a fluent interface to make it easier to create objects.
 */
public class CustomerBuilder {
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

    public Customer createCustomer() {
        return new Customer(id, name, address, phoneNumber);
    }
}
