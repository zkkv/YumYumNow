package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import nl.tudelft.sem.yumyumnow.delivery.model.Location;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class LocationBuilder {
    private BigDecimal latitude;
    private BigDecimal longitude;
    private OffsetDateTime timestamp;

    public LocationBuilder setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
        return this;
    }

    public LocationBuilder setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
        return this;
    }

    public LocationBuilder setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Location createLocation() {
        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTimestamp(timestamp);
        return location;
    }
}
