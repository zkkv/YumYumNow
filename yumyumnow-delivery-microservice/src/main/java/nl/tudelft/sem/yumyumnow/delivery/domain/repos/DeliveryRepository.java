package nl.tudelft.sem.yumyumnow.delivery.domain.repos;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
}
