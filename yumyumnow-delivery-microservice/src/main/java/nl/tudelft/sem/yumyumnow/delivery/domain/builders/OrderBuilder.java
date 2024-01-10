package nl.tudelft.sem.yumyumnow.delivery.domain.builders;
import java.util.UUID;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Customer;
public class OrderBuilder {
    private UUID id;
    private Vendor vendor;
    private Customer customer;

    public OrderBuilder setOrderId(UUID id) {
        this.id = id;
        return this;
    }

    public OrderBuilder setOrderVendor(Vendor vendor) {
        this.vendor = vendor;
        return this;
    }

    public OrderBuilder setOrderCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

}
