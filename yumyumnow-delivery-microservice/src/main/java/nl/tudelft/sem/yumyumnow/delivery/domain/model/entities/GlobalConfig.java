package nl.tudelft.sem.yumyumnow.delivery.domain.model.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class GlobalConfig {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID globalConfigId;

    private BigDecimal defaultMaxZone;
}
