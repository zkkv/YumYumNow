package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.CourierService;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import java.util.UUID;


public class CourierExistsValidator extends AuthProcessor<Courier> {
    Courier toValidate;

    /**
     * Validate a courier exists.
     *
     * @param next The next validator in the chain
     * @param toValidate ID of the courier to validate
     * @param courierService Courier service to get the courier from
     */
    public CourierExistsValidator(
            AuthProcessor next,
            UUID toValidate, CourierService courierService) {

        super(next, toValidate);
        this.toValidate = courierService.getCourier(toValidate.toString());
    }

    @Override
    public boolean process(Delivery delivery) {
        if (toValidate == null) {
            return false;
        }

        if (next == null) {
            return true;
        }
        return next.process(delivery);
    }
}
