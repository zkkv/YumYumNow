package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.CourierService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;

import java.util.UUID;


public class CourierValidator extends AuthProcessor<Courier> {
    VendorService vendorService;
    Courier toValidate;

    public CourierValidator(
            AuthProcessor next,
            UUID toValidate, CourierService courierService,
            VendorService vendorService) {

        super(next, toValidate);
        this.vendorService = vendorService;
        this.toValidate = courierService.getCourier(toValidate.toString());
    }

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
