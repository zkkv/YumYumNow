package nl.tudelft.sem.yumyumnow.delivery.domain.dto;

import java.util.Objects;
import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Setter;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;

/**
 * Courier DTO.
 * Used for easy data representation and transfer with other microservices.
 */
@AllArgsConstructor @Setter
@Getter
public class Vendor {

    private UUID id;

    private Location address;

    private String phone;

    private Boolean allowsOnlyOwnCouriers;

    private BigDecimal maxDeliveryZoneKm;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vendor vendor = (Vendor) o;
        return Objects.equals(this.id, vendor.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Vendor {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

