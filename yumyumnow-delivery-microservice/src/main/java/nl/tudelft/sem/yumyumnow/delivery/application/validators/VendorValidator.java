package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;

import java.util.UUID;

public class VendorValidator extends AuthProcessor<Vendor> {
    private Vendor toValidate;

    public VendorValidator(AuthProcessor next, UUID toValidate, VendorService vendorService) {
        super(next, toValidate);
        this.toValidate = vendorService.getVendor(toValidate.toString());
    }

    @Override
    public boolean process(Delivery delivery) {
        if (toValidate == null) return false;
        if (!delivery.getVendorId().equals(toValidate.getId()))
            return false;

        if (next == null) return true;

        return next.process(delivery);
    }
}
