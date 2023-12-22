package nl.tudelft.sem.yumyumnow.delivery.domain.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;

import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Customer
 */

@Setter
public class Customer {

    private UUID id;

    private String name;

    private String deliveryAddress;

    private String phone;

    public Customer id(UUID id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     * @return id
     */
    @Valid
    @Schema(name = "id", example = "7ddf1830-8dfa-11ee-b9d1-0242ac120002", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("id")
    public UUID getId() {
        return id;
    }

    public Customer name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     * @return name
     */

    @Schema(name = "name", example = "Max Verstappen", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public Customer deliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    /**
     * Get deliveryAddress
     * @return deliveryAddress
     */

    @Schema(name = "deliveryAddress", example = "Mekelweg 5, 2628 CD Delft", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("deliveryAddress")
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public Customer phone(String phone) {
        this.phone = phone;
        return this;
    }

    /**
     * Get phone
     * @return phone
     */

    @Schema(name = "phone", example = "+31 71 520 9291", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Customer customer = (Customer) o;
        return Objects.equals(this.id, customer.id) &&
                Objects.equals(this.name, customer.name) &&
                Objects.equals(this.deliveryAddress, customer.deliveryAddress) &&
                Objects.equals(this.phone, customer.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, deliveryAddress, phone);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Customer {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    deliveryAddress: ").append(toIndentedString(deliveryAddress)).append("\n");
        sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
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

