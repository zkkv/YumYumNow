package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.CourierService;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;

import java.util.UUID;


public class CourierExistsValidator extends AuthProcessor<Courier> {
    Courier toValidate;

    public CourierExistsValidator(
            AuthProcessor next,
            UUID toValidate, CourierService courierService) {

        super(next, toValidate);
        this.toValidate = courierService.getCourier(toValidate.toString());
    }

    @Override
    public boolean process(Delivery delivery) {
        if (toValidate == null) return false;

        if (next == null) return true;
        return next.process(delivery);
    }
}
