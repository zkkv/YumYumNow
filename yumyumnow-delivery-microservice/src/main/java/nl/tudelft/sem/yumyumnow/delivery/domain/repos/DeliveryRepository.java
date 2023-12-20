package nl.tudelft.sem.yumyumnow.delivery.domain.repos;

import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for Delivery entities.
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
}
