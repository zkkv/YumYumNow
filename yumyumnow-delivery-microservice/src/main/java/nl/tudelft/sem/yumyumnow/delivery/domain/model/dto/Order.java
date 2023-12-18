package nl.tudelft.sem.yumyumnow.delivery.domain.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Order
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-04T08:14:40.728780+01:00[Europe/Amsterdam]")
public class Order {

    private UUID id;

    private Customer customer;

    private Vendor vendor;

    public Order id(UUID id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     * @return id
     */

    @Schema(name = "id", example = "7ddf136c-8dfa-11ee-b9d1-0242ac120002", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("id")
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Order customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    /**
     * Get customer
     * @return customer
     */
    @Valid
    @Schema(name = "customer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("customer")
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Order vendor(Vendor vendor) {
        this.vendor = vendor;
        return this;
    }

    /**
     * Get vendor
     * @return vendor
     */
    @Valid
    @Schema(name = "vendor", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("vendor")
    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(this.id, order.id) &&
                Objects.equals(this.customer, order.customer) &&
                Objects.equals(this.vendor, order.vendor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, vendor);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Order {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    customer: ").append(toIndentedString(customer)).append("\n");
        sb.append("    vendor: ").append(toIndentedString(vendor)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

