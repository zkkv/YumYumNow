package nl.tudelft.sem.yumyumnow.delivery.domain.model.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

/**
 *  Courier entity with {@code courierId} as primary key.
 *
 * @author Kirill Zhankov
 */
@Entity
public class Courier {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID courierId;

    public Courier(UUID courierId) {
        this.courierId = courierId;
    }

    public Courier() {
    }

    public UUID getCourierId() {
        return courierId;
    }

    public void setCourierId(UUID courierId) {
        this.courierId = courierId;
    }

}
