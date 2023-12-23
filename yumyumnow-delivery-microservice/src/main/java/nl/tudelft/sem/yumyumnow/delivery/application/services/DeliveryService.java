package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.GlobalConfigRepository;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.VendorCustomizerRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryAdminMaxZoneGet200Response;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryVendorIdMaxZonePutRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final VendorCustomizerRepository vendorCustomizerRepository;
    private final GlobalConfigRepository globalConfigRepository;
    
    /**
     * Create a new DeliveryService.
     *
     * @param deliveryRepository    The repository to use for delivery
     * @param vendorCustomizerRepository   The repository for customized vendor
     * @param globalConfigRepository    The repository for global configuration
     */
    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository,
                           VendorCustomizerRepository vendorCustomizerRepository,
                           GlobalConfigRepository globalConfigRepository) {
        this.deliveryRepository = deliveryRepository;
        this.vendorCustomizerRepository = vendorCustomizerRepository;
        this.globalConfigRepository = globalConfigRepository;
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

        delivery.setStatus(Delivery.StatusEnum.PENDING);

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

        if(delivery.getStatus() != Delivery.StatusEnum.ACCEPTED){
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
    public Delivery updateStatus(UUID id, UUID userId, DeliveryIdStatusPutRequest.StatusEnum status) {

        // TODO: This has to be converted to a validator pattern

        boolean isValidStatusForVendor = status == DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED || status == DeliveryIdStatusPutRequest.StatusEnum.REJECTED
                || status == DeliveryIdStatusPutRequest.StatusEnum.GIVEN_TO_COURIER || status == DeliveryIdStatusPutRequest.StatusEnum.PREPARING;
        if (isValidStatusForVendor
                && (vendorCustomizerRepository.findById(userId).isEmpty())) {
            return null;
        }
        if(vendorCustomizerRepository.findById(userId).isPresent() && !isValidStatusForVendor){
            return null;
        }

        Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);


        if (optionalDelivery.isEmpty()) {
            return null;
        }


        Delivery delivery = optionalDelivery.get();

        switch (status){
            case PENDING -> delivery.setStatus(Delivery.StatusEnum.PENDING);
            case ACCEPTED -> delivery.setStatus(Delivery.StatusEnum.ACCEPTED);
            case REJECTED -> delivery.setStatus(Delivery.StatusEnum.REJECTED);
            case DELIVERED -> delivery.setStatus(Delivery.StatusEnum.DELIVERED);
            case PREPARING -> delivery.setStatus(Delivery.StatusEnum.PREPARING);
            case IN_TRANSIT -> delivery.setStatus(Delivery.StatusEnum.IN_TRANSIT);
            case GIVEN_TO_COURIER -> delivery.setStatus(Delivery.StatusEnum.GIVEN_TO_COURIER);
        }

        deliveryRepository.save(delivery);

        return delivery;
    }


    /**
     * Update the maximum delivery zone of a vendor
     *
     * @param vendorId the current vendorId
     * @param deliveryVendorIdMaxZonePutRequest contains id for the vendor to update (should be the same as current vendorId)
     *                                          and the new maximium delivery zone
     * @param vendorService vendor service to interact with user api
     * @return the vendorID with its updated maximum delivery zone
     */
    public DeliveryVendorIdMaxZonePutRequest vendorMaxZone(UUID vendorId, DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest,
                                                           VendorService vendorService){
        UUID vendorToUpdate = deliveryVendorIdMaxZonePutRequest.getVendorId();
        BigDecimal radiusKm = deliveryVendorIdMaxZonePutRequest.getRadiusKm();

        if(vendorId != vendorToUpdate || vendorService.getVendor(vendorId) == null) return null;

        Map<String, Object> vendorMap = vendorService.getVendor(vendorId);

        Object allowOwnCourier = vendorMap.get("allowOnlyOwnCouriers");
        if(allowOwnCourier instanceof Boolean && (Boolean) allowOwnCourier){
            vendorMap.put("maxDeliveryZone", radiusKm);

            boolean response = vendorService.putVendor(vendorId,vendorMap);
            if(response){
                return deliveryVendorIdMaxZonePutRequest;
            }
        }
        return null;
    }

    /**
     * Get the default maximum delivery zone as an admin.
     *
     * @param adminId the id of admin
     * @param adminService admin service from user microservice
     * @return the response contains admin id and default maximum delivery zone
     */
    public DeliveryAdminMaxZoneGet200Response adminGetMaxZone(UUID adminId, AdminService adminService){
//        BigDecimal defaultMaxZone = globalConfigRepository.findById(globalConfigId);
        return null;
    }
}
