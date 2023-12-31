package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.CourierService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest.StatusEnum;

import java.util.Map;
import java.util.UUID;

public class StatusPermissionValidator extends AuthProcessor<StatusEnum> {
    private StatusEnum toValidate;
    private Object user;

    public StatusPermissionValidator(
            Map<Class, AuthProcessor> next,
            StatusEnum toValidate, UUID user,
            VendorService vendorService,
            CourierService courierService) {

        super(null, toValidate);

        Vendor vendor = vendorService.getVendor(user.toString());
        Courier courier = courierService.getCourier(user.toString());

        Object gotUser = null;

        if (vendor != null) {
            gotUser = vendor;
        } else if (courier != null) {
            gotUser = courier;
        } else {
            gotUser = null;
        }

        this.next = next.get(gotUser.getClass());

        this.toValidate = toValidate;
        this.user = gotUser;
    }

    @Override
    public boolean process(Delivery delivery) {
        if (user.getClass() == Vendor.class) {
            if (toValidate == StatusEnum.IN_TRANSIT ||
                    toValidate == StatusEnum.DELIVERED ||
                    toValidate == StatusEnum.PENDING) {
                return false;
            }
        } else if (user.getClass() == Courier.class) {
            if (toValidate == StatusEnum.ACCEPTED ||
                    toValidate == StatusEnum.REJECTED ||
                    toValidate == StatusEnum.PREPARING ||
                    toValidate == StatusEnum.GIVEN_TO_COURIER ||
                    toValidate == StatusEnum.PENDING) {
                return false;
            }
        }

        if (next == null) return true;
        return next.process(delivery);
    }
}
