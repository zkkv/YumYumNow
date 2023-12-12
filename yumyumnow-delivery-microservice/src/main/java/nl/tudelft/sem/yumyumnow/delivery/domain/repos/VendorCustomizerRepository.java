package nl.tudelft.sem.yumyumnow.delivery.domain.repos;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.VendorCustomizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for VendorCustomizer entities.
 */
@Repository
public interface VendorCustomizerRepository extends JpaRepository<VendorCustomizer, UUID> {
}
