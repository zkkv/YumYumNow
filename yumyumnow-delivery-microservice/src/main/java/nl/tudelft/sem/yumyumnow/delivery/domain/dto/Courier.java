package nl.tudelft.sem.yumyumnow.delivery.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class Courier {
    private UUID id;
    private Vendor vendor;

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else if (this == obj) {
            return true;
        }
        return Objects.equals(this.id, ((Courier) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "class Courier {\n" +
                "    id: " + toIndentedString(id) + "\n" +
                "    vendor: " + toIndentedString(vendor) + "\n" +
                "}";
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
