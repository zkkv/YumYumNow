package nl.tudelft.sem.yumyumnow.delivery.domain.repos;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.CourierToDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for Courier entities.
 */
@Repository
public interface CourierToDeliveryRepository extends JpaRepository<CourierToDelivery,
        CourierToDelivery.CourierToDeliveryPrimaryKey> {
}
