package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.CourierService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;

import java.util.UUID;

/**
 * CourierValidator.
 * Validates that the courier is allowed to deliver the order.
 */
public class CourierValidator extends AuthProcessor<Courier> {
    VendorService vendorService;
    Courier toValidate;

    /**
     * Constructor for CourierValidator.
     * @param next The next validator in the chain
     * @param toValidate ID of the courier to validate
     * @param courierService Courier service to get the courier from
     * @param vendorService Vendor service to get the vendor from
     */
    public CourierValidator(
            AuthProcessor next,
            UUID toValidate, CourierService courierService,
            VendorService vendorService) {

        super(next, toValidate);
        this.vendorService = vendorService;
        this.toValidate = courierService.getCourier(toValidate.toString());
    }

    /**
     * Validate the courier
     * Steps:
     * 1. Check if the courier exists
     * 2. Check if the courier is allowed to deliver the order
     * 3. Check if the vendor allows only its own couriers, and if so,
     *   check if the courier is from the same vendor
     * 4. If the next validator exists, call it
     * @param delivery The delivery to validate against
     * @return True if the validation was successful, false otherwise
     */
    @Override
    public boolean process(Delivery delivery) {
        if (toValidate == null) return false;


        if (!delivery.getCourierId().equals(toValidate.getId()))
            return false;

        Vendor vendor = vendorService.getVendor(delivery.getVendorId().toString());
        if (vendor == null) return true;

        if (vendor.getAllowsOnlyOwnCouriers() &&
                !vendor.getId().equals(toValidate.getVendor().getId()))
            return false;

        if (next == null) return true;
        return next.process(delivery);
    }
}
