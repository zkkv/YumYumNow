package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.model.dto.DeliveryIdStatusPutRequest;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.domain.model.entities.StatusEnum;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.VendorCustomizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    private final VendorCustomizerRepository vendorCustomizerRepository;

    /**
     * Create a new DeliveryService.
     *
     * @param deliveryRepository         The repository to use.
     * @param vendorCustomizerRepository
     */
    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository, VendorCustomizerRepository vendorCustomizerRepository) {
        this.deliveryRepository = deliveryRepository;
        this.vendorCustomizerRepository = vendorCustomizerRepository;
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

    public Delivery updateStatus(UUID id, UUID userId, StatusEnum status){

        // TODO: Add verification for other users
        if ((status == StatusEnum.ACCEPTED || status == StatusEnum.REJECTED
                ) && vendorCustomizerRepository.findById(userId).isEmpty() ){
            return null;
        }

        Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);


        if(optionalDelivery.isEmpty()){
            return null;
        }


        Delivery delivery = optionalDelivery.get();

        delivery.setStatus(status);

        deliveryRepository.save(delivery);

        return delivery;
    }
}
