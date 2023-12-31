package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;


public class CourierValidator extends AuthProcessor<Courier> {
    VendorService vendorService;
    Courier toValidate;

    public CourierValidator(AuthProcessor next, Courier toValidate, VendorService vendorService) {
        super(next);
        this.vendorService = vendorService;
        this.toValidate = toValidate;
    }

    @Override
    public boolean process(Delivery delivery) {
        if (toValidate == null) return false;
        Vendor vendor = vendorService.getVendor(delivery.getVendorId().toString());
        if (!delivery.getCourierId().equals(toValidate.getId()))
            return false;
        if (vendor.getAllowsOnlyOwnCouriers() &&
                !vendor.getId().equals(toValidate.getVendor().getId()))
            return false;

        if (next == null) return true;

        return next.process(delivery);
    }
}
