package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.CourierService;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import java.util.UUID;


public class CourierBelongsToDeliveryValidator extends AuthProcessor<Courier> {
    Courier toValidate;

    /**
     * Validator for whether courier belongs to a delivery.
     *
     * @param next The next validator in the chain
     * @param toValidate the courier to validate
     * @param courierService service of courier
     */
    public CourierBelongsToDeliveryValidator(
            AuthProcessor next,
            UUID toValidate, CourierService courierService) {

        super(next, toValidate);
        this.toValidate = courierService.getCourier(toValidate.toString());
    }

    @Override
    public boolean process(Delivery delivery) {
        if (!delivery.getCourierId().equals(toValidate.getId())) {
            return false;
        }

        if (next == null) {
            return true;
        }

        return next.process(delivery);
    }
}
