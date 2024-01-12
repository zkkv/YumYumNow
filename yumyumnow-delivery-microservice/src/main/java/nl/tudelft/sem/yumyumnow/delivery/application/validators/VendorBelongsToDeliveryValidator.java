package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;

import java.util.UUID;

/**
 * Validates that the vendor of the delivery is the same as the one provided.
 */
public class VendorBelongsToDeliveryValidator extends AuthProcessor<Vendor> {
    private Vendor toValidate;

    /**
     * Constructor for VendorValidator.
     * @param next The next validator in the chain
     * @param toValidate ID of the vendor to validate
     * @param vendorService Vendor service to get the vendor from
     */
    public VendorBelongsToDeliveryValidator(AuthProcessor next, UUID toValidate, VendorService vendorService) {
        super(next, toValidate);
        this.toValidate = vendorService.getVendor(toValidate.toString());
    }

    /**
     * Validate the vendor.
     * Steps:
     * 1. Check if the vendor exists
     * 2. Check if the vendor is the same as the one provided
     * 3. If the next validator exists, call it
     * @param delivery The delivery to validate against
     * @return True if the validation was successful, false otherwise
     */
    @Override
    public boolean process(Delivery delivery) {
        if (!delivery.getVendorId().equals(toValidate.getId()))
            return false;

        if (next == null) return true;

        return next.process(delivery);
    }
}
