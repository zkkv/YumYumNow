package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import java.util.UUID;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Order;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;

public class OrderBuilder implements Builder<Order> {
    private UUID id;
    private Vendor vendor;
    private Customer customer;

    /**
     * A setter for the Order id of the OrderBuilder.
     *
     * @param id the UUID to be set as the order id
     * @return the instance of the OrderBuilder that it was called on, with the id field updated
     */
    public OrderBuilder setOrderId(UUID id) {
        this.id = id;
        return this;
    }

    /**
     * A setter for the Order vendor of the OrderBuilder.
     *
     * @param vendor the Vendor to be set as the vendor for this order
     * @return the instance of the OrderBuilder that it was called on, with the vendor field updated
     */
    public OrderBuilder setOrderVendor(Vendor vendor) {
        this.vendor = vendor;
        return this;
    }

    /**
     * A setter for the Order customer of the OrderBuilder.
     *
     * @param customer the Customer to be set as the customer for this order
     * @return the instance of the OrderBuilder that it was called on, with the customer field updated
     */
    public OrderBuilder setOrderCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    /**
     * A method that creates a new Order object using the field of the OrderBuilder.
     *
     * @return the Order with the same fields as the OrderBuilder
     */
    @Override
    public Order create() {
        return new Order(id, vendor, customer);
    }

    /**
     * a method that resets the fields of the OrderBuilder.
     */
    @Override
    public void reset() {
        id = null;
        vendor = null;
        customer = null;
    }
}
