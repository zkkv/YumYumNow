package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;

public class VendorValidator extends AuthProcessor<Vendor> {
    private Vendor toValidate;

    public VendorValidator(AuthProcessor<Vendor> next, Vendor toValidate) {
        super(next);
        this.toValidate = toValidate;
    }

    @Override
    public boolean process(Delivery delivery) {
        if (!delivery.getVendorId().equals(toValidate.getId()))
            return false;

        if (next == null) return true;

        return next.process(delivery);
    }
}
