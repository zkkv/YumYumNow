package nl.tudelft.sem.yumyumnow.delivery.domain.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusEnum {
    PENDING("PENDING"),

    ACCEPTED("ACCEPTED"),

    REJECTED("REJECTED"),

    PREPARING("PREPARING"),

    GIVEN_TO_COURIER("GIVEN_TO_COURIER"),

    IN_TRANSIT("IN_TRANSIT"),

    DELIVERED("DELIVERED");

    private String value;

    StatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
        for (StatusEnum b : StatusEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
