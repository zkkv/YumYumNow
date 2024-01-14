package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import nl.tudelft.sem.yumyumnow.delivery.model.Location;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationBuilderTest {
    @Test
    public void constructorTest() {
        LocationBuilder locationBuilder = new LocationBuilder();
        Location location = locationBuilder.create();

        assertThat(location).isNotNull();
        assertThat(location.getLatitude()).isNull();
        assertThat(location.getLongitude()).isNull();
        assertThat(location.getTimestamp()).isNull();
    }

    @Property
    public void setLatitude(
            @ForAll BigDecimal latitude
            ) {
        Location location = new LocationBuilder()
                .setLatitude(latitude)
                .create();

        assertThat(location.getLatitude()).isEqualTo(latitude);
    }

    @Property
    public void setLongitude(
            @ForAll BigDecimal longitude
            ) {
        Location location = new LocationBuilder()
                .setLongitude(longitude)
                .create();

        assertThat(location.getLongitude()).isEqualTo(longitude);
    }

    @Property
    public void reset() {
        LocationBuilder locationBuilder = new LocationBuilder()
                .setLatitude(BigDecimal.ONE)
                .setLongitude(BigDecimal.ONE)
                .setTimestamp(OffsetDateTime.now());

        locationBuilder.reset();

        Location location = locationBuilder.create();

        assertThat(location).isNotNull();
        assertThat(location.getLatitude()).isNull();
        assertThat(location.getLongitude()).isNull();
        assertThat(location.getTimestamp()).isNull();
    }

    @Test
    public void setTimestamp() {
        OffsetDateTime timestamp = OffsetDateTime.MIN;
        
        Location location = new LocationBuilder()
                .setTimestamp(timestamp)
                .create();

        assertThat(location.getTimestamp()).isEqualTo(timestamp);
    }
}
