package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.CourierToDelivery;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.StatusEnum;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.CourierToDeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.VendorCustomizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    private final VendorCustomizerRepository vendorCustomizerRepository;

    private final CourierToDeliveryRepository courierToDeliveryRepository;

    /**
     * Create a new DeliveryService.
     *
     * @param deliveryRepository         The repository to use.
     * @param vendorCustomizerRepository
     * @param courierToDeliveryRepository
     */
    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository,
                           VendorCustomizerRepository vendorCustomizerRepository,
                           CourierToDeliveryRepository courierToDeliveryRepository) {
        this.deliveryRepository = deliveryRepository;
        this.vendorCustomizerRepository = vendorCustomizerRepository;
        this.courierToDeliveryRepository = courierToDeliveryRepository;
    }

    /**
     * Create a delivery based on order data.
     *
     * @param order The order ID to which the delivery corresponds
     *              (UUID).
     * @param vendor The vendor ID to which the delivery corresponds
     *               (UUID).
     * @return The created delivery.
     */
    public Delivery createDelivery(UUID order, UUID vendor) {
        Delivery delivery = new Delivery();

        // TODO: Get order details from Order microservice
        // TODO: Get vendor details from Vendor microservice

        delivery.setStatus(StatusEnum.PENDING);

        return deliveryRepository.save(delivery);
    }

    /**
     * Update the estimatedPrepTime of a delivery
     * @param deliveryID the ID of the delivery to be updated
     * @param vendor the ID of the vendor that updates the delivery
     * @param estimatedPrepTime the new estimated time
     * @return the updated delivery
     */
    public Delivery addPrepTime(UUID deliveryID, UUID vendor, OffsetDateTime estimatedPrepTime){

        if(vendorCustomizerRepository.findById(vendor).isEmpty()){
            return null;
        }

        Optional<Delivery> optionalDelivery = deliveryRepository.findById(deliveryID);


        if(optionalDelivery.isEmpty()){
            return null;
        }


        Delivery delivery = optionalDelivery.get();

        if(delivery.getStatus() != StatusEnum.ACCEPTED){
            return null;
        }

        delivery.estimatedPreparationFinishTime(estimatedPrepTime);

        deliveryRepository.save(delivery);

        return delivery;
    }

    /**
     * Updates status of the delivery with verification of the user rights.
     *
     * @param id        delivery id.
     * @param userId    user id, for valid update user has to be either a courier or a vendor,
     *                  depending on which status they are trying to set.
     * @param status    the new status of the delivery.
     * @return          delivery object with the update status, or null if user has no right to
     *                  update it or if delivery is not found.
     * @author          Horia Radu, Kirill Zhankov
     */
    public Delivery updateStatus(UUID id, UUID userId, StatusEnum status) {

        // TODO: Add verification for other statuses
        boolean isCourierMatchedWithDelivery = courierToDeliveryRepository.findById(
                new CourierToDelivery.CourierToDeliveryPrimaryKey(userId, id)).isPresent();

        if ((status == StatusEnum.IN_TRANSIT || status == StatusEnum.DELIVERED)
                && !isCourierMatchedWithDelivery) {
            return null;
        }

        if ((status == StatusEnum.ACCEPTED || status == StatusEnum.REJECTED)
                && vendorCustomizerRepository.findById(userId).isEmpty()) {
            return null;
        }

        Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);


        if (optionalDelivery.isEmpty()) {
            return null;
        }


        Delivery delivery = optionalDelivery.get();

        delivery.setStatus(status);

        deliveryRepository.save(delivery);

        return delivery;
    }
}
