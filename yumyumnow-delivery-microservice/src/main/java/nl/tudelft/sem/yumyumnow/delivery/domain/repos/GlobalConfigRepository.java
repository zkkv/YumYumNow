package nl.tudelft.sem.yumyumnow.delivery.domain.repos;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.GlobalConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Repository for global parameters.
 */
@Repository
public interface GlobalConfigRepository extends JpaRepository<GlobalConfig, UUID> {
}
