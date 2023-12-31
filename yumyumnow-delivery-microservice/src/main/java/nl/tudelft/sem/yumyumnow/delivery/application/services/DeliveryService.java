package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.application.validators.VendorValidator;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.AccessForbiddenException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.BadArgumentException;
import nl.tudelft.sem.yumyumnow.delivery.domain.exceptions.NoDeliveryFoundException;
import nl.tudelft.sem.yumyumnow.delivery.domain.repos.DeliveryRepository;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryVendorIdMaxZonePutRequest;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final VendorService vendorService;

    /**
     * Create a new DeliveryService.
     *
     * @param deliveryRepository The repository to use.
     */
    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository, VendorService vendorService) {
        this.deliveryRepository = deliveryRepository;
        this.vendorService = vendorService;
    }

    /**
     * Create a delivery based on order data.
     *
     * @param order  The order ID to which the delivery corresponds
     *               (UUID).
     * @param vendor The vendor ID to which the delivery corresponds
     *               (UUID).
     * @return The created delivery.
     */
    public Delivery createDelivery(UUID order, UUID vendor) {
        Delivery delivery = new Delivery();

        delivery.setOrderId(order);
        delivery.setOrderId(order);
        delivery.setStatus(Delivery.StatusEnum.PENDING);

        return deliveryRepository.save(delivery);
    }

    /**
     * Update the estimatedPrepTime of a delivery
     *
     * @param deliveryId        the ID of the delivery to be updated
     * @param vendor            the ID of the vendor that updates the delivery
     * @param estimatedPrepTime the new estimated time
     * @return the updated delivery
     */
    public Delivery changePrepTime(UUID deliveryId, UUID vendorId, OffsetDateTime estimatedPrepTime) {


        Optional<Delivery> optionalDelivery = deliveryRepository.findById(deliveryId);


        if (optionalDelivery.isEmpty()) {
            return null;
        }

        Delivery delivery = optionalDelivery.get();

        Vendor vendor = vendorService.getVendor(vendorId.toString());

        VendorValidator vendorValidator = new VendorValidator(null, vendor);

        if (delivery.getStatus() != Delivery.StatusEnum.ACCEPTED || !vendorValidator.process(delivery)) {
            return null;
        }

        delivery.estimatedPreparationFinishTime(estimatedPrepTime);

        deliveryRepository.save(delivery);

        return delivery;
    }

    /**
     * Updates status of the delivery with verification of the user rights.
     *
     * @param id     delivery id.
     * @param userId user id, for valid update user has to be either a courier or a vendor,
     *               depending on which status they are trying to set.
     * @param status the new status of the delivery.
     * @return delivery object with the update status, or null if user has no right to
     * update it or if delivery is not found.
     * @author Horia Radu, Kirill Zhankov
     */
    public Delivery updateStatus(UUID id, UUID userId, DeliveryIdStatusPutRequest.StatusEnum status)
            throws NoDeliveryFoundException, AccessForbiddenException, BadArgumentException {

        // TODO: This has to be converted to a validator pattern

        Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);

        if (optionalDelivery.isEmpty()) {
            throw new NoDeliveryFoundException("No delivery found by id.");
        }

        Delivery delivery = optionalDelivery.get();

        boolean isValidStatusForVendor = status == DeliveryIdStatusPutRequest.StatusEnum.ACCEPTED
                || status == DeliveryIdStatusPutRequest.StatusEnum.REJECTED
                || status == DeliveryIdStatusPutRequest.StatusEnum.GIVEN_TO_COURIER
                || status == DeliveryIdStatusPutRequest.StatusEnum.PREPARING;

        boolean isVendorMatchedWithDelivery = delivery.getVendorId() == userId;

        if (isValidStatusForVendor && !isVendorMatchedWithDelivery) {
            throw new AccessForbiddenException("Delivery contains a different vendor id.");
        }

        boolean isValidStatusForCourier = status == DeliveryIdStatusPutRequest.StatusEnum.IN_TRANSIT
                || status == DeliveryIdStatusPutRequest.StatusEnum.DELIVERED;

        boolean isCourierMatchedWithDelivery = delivery.getCourierId() == userId;

        if (isValidStatusForCourier && !isCourierMatchedWithDelivery) {
            throw new AccessForbiddenException("Delivery contains a different vendor id.");
        }

        switch (status) {
            case ACCEPTED -> delivery.setStatus(Delivery.StatusEnum.ACCEPTED);
            case REJECTED -> delivery.setStatus(Delivery.StatusEnum.REJECTED);
            case DELIVERED -> delivery.setStatus(Delivery.StatusEnum.DELIVERED);
            case PREPARING -> delivery.setStatus(Delivery.StatusEnum.PREPARING);
            case IN_TRANSIT -> delivery.setStatus(Delivery.StatusEnum.IN_TRANSIT);
            case GIVEN_TO_COURIER -> delivery.setStatus(Delivery.StatusEnum.GIVEN_TO_COURIER);
            default -> throw new BadArgumentException(
                    "Status can only be one of: ACCEPTED, REJECTED, DELIVERED, " +
                            "PREPARING, IN_TRANSIT, GIVEN_TO_COURIER");
        }

        deliveryRepository.save(delivery);

        return delivery;
    }


    /**
     * Update the maximum delivery zone of a vendor
     *
     * @param vendorId                          the current vendorId
     * @param deliveryVendorIdMaxZonePutRequest contains id for the vendor to update (should be the same as current vendorId)
     *                                          and the new maximium delivery zone
     * @param vendorService                     vendor service to interact with user api
     * @return the vendorID with its updated maximum delivery zone
     */
    public DeliveryVendorIdMaxZonePutRequest vendorMaxZone(UUID vendorId, DeliveryVendorIdMaxZonePutRequest deliveryVendorIdMaxZonePutRequest,
                                                           VendorService vendorService) {
        UUID vendorToUpdate = deliveryVendorIdMaxZonePutRequest.getVendorId();
        BigDecimal radiusKm = deliveryVendorIdMaxZonePutRequest.getRadiusKm();

        if (vendorId != vendorToUpdate || vendorService.getVendor(vendorId.toString()) == null) return null;

        Vendor vendor = vendorService.getVendor(vendorId.toString());

        if (vendor.getAllowsOnlyOwnCouriers()) {
            vendor.setMaxDeliveryZoneKm(radiusKm);

            boolean response = vendorService.putVendor(vendor);
            if (response) {
                return deliveryVendorIdMaxZonePutRequest;
            }
        }
        return null;
    }

}
