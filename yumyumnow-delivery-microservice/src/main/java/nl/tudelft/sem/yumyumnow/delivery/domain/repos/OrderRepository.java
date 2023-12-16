package nl.tudelft.sem.yumyumnow.delivery.domain.repos;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
