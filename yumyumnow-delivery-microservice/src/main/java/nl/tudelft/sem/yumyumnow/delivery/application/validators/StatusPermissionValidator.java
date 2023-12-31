package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest.StatusEnum;

import java.util.Map;

public class StatusPermissionValidator extends AuthProcessor<StatusEnum> {
    private Delivery.StatusEnum toValidate;
    private Object user;

    public StatusPermissionValidator(Map<Class, AuthProcessor> next, StatusEnum ztoValidate, Object user) {
        super(next.get(user.getClass()));

        this.toValidate = toValidate;
        this.user = user;
    }

    @Override
    public boolean process(Delivery delivery) {
        if (user.getClass() == Vendor.class) {
            if (toValidate == Delivery.StatusEnum.IN_TRANSIT ||
                    toValidate == Delivery.StatusEnum.DELIVERED) {
                return false;
            }
        } else if (user.getClass() == Courier.class) {
            if (toValidate == Delivery.StatusEnum.ACCEPTED ||
                    toValidate == Delivery.StatusEnum.REJECTED ||
                    toValidate == Delivery.StatusEnum.PREPARING ||
                    toValidate == Delivery.StatusEnum.GIVEN_TO_COURIER ||
                    toValidate == Delivery.StatusEnum.PENDING) {
                return false;
            }
        }

        if (next == null) return true;

        return next.process(delivery);
    }
}
