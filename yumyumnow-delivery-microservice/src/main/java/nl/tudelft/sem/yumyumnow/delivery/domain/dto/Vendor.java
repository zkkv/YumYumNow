package nl.tudelft.sem.yumyumnow.delivery.domain.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Vendor
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-05T14:01:31.567768+01:00[Europe/Amsterdam]")
public class Vendor {

    private UUID id;

    private String name;

    private String address;

    private String phone;

    private Boolean allowsOnlyOwnCouriers;

    private BigDecimal maxDeliveryZoneKm;

    public Vendor id(UUID id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     * @return id
     */
    @Valid
    @Schema(name = "id", example = "7ddf1a10-8dfa-11ee-b9d1-0242ac120002", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("id")
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Vendor name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     * @return name
     */

    @Schema(name = "name", example = "John Smith", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vendor address(String address) {
        this.address = address;
        return this;
    }

    /**
     * Get address
     * @return address
     */

    @Schema(name = "address", example = "Professor Schermerhornstraat 19 2628 CN Delft", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Vendor phone(String phone) {
        this.phone = phone;
        return this;
    }

    /**
     * Get phone
     * @return phone
     */

    @Schema(name = "phone", example = "+31 71 520 9297", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Vendor allowsOnlyOwnCouriers(Boolean allowsOnlyOwnCouriers) {
        this.allowsOnlyOwnCouriers = allowsOnlyOwnCouriers;
        return this;
    }

    /**
     * Get allowsOnlyOwnCouriers
     * @return allowsOnlyOwnCouriers
     */

    @Schema(name = "allowsOnlyOwnCouriers", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("allowsOnlyOwnCouriers")
    public Boolean getAllowsOnlyOwnCouriers() {
        return allowsOnlyOwnCouriers;
    }

    public void setAllowsOnlyOwnCouriers(Boolean allowsOnlyOwnCouriers) {
        this.allowsOnlyOwnCouriers = allowsOnlyOwnCouriers;
    }

    public Vendor maxDeliveryZoneKm(BigDecimal maxDeliveryZoneKm) {
        this.maxDeliveryZoneKm = maxDeliveryZoneKm;
        return this;
    }

    /**
     * Get maxDeliveryZoneKm
     * @return maxDeliveryZoneKm
     */
    @Valid
    @Schema(name = "maxDeliveryZoneKm", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("maxDeliveryZoneKm")
    public BigDecimal getMaxDeliveryZoneKm() {
        return maxDeliveryZoneKm;
    }

    public void setMaxDeliveryZoneKm(BigDecimal maxDeliveryZoneKm) {
        this.maxDeliveryZoneKm = maxDeliveryZoneKm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vendor vendor = (Vendor) o;
        return Objects.equals(this.id, vendor.id) &&
                Objects.equals(this.name, vendor.name) &&
                Objects.equals(this.address, vendor.address) &&
                Objects.equals(this.phone, vendor.phone) &&
                Objects.equals(this.allowsOnlyOwnCouriers, vendor.allowsOnlyOwnCouriers) &&
                Objects.equals(this.maxDeliveryZoneKm, vendor.maxDeliveryZoneKm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, phone, allowsOnlyOwnCouriers, maxDeliveryZoneKm);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Vendor {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    address: ").append(toIndentedString(address)).append("\n");
        sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
        sb.append("    allowsOnlyOwnCouriers: ").append(toIndentedString(allowsOnlyOwnCouriers)).append("\n");
        sb.append("    maxDeliveryZoneKm: ").append(toIndentedString(maxDeliveryZoneKm)).append("\n");
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

