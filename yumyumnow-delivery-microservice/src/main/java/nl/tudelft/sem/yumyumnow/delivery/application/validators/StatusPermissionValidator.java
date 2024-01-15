package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.application.services.CourierService;
import nl.tudelft.sem.yumyumnow.delivery.application.services.VendorService;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Vendor;
import nl.tudelft.sem.yumyumnow.delivery.model.DeliveryIdStatusPutRequest.StatusEnum;
import java.util.Map;
import java.util.UUID;

/**
 * Validates if the user has the permission to change the status of the delivery.
 */
public class StatusPermissionValidator extends AuthProcessor<StatusEnum> {
    private StatusEnum toValidate;
    private Object user;

    /**
     * Constructor for StatusPermissionValidator.
     * Gets the user from the vendor or courier service and sets the next validator to the correct one.
     *
     * @param next Map of the next validators in the chain by user type
     * @param toValidate The status to validate
     * @param user The user ID to validate
     * @param vendorService Vendor service to get the vendor from
     * @param courierService Courier service to get the courier from
     */
    public StatusPermissionValidator(
            Map<Class, AuthProcessor> next,
            StatusEnum toValidate, UUID user,
            VendorService vendorService,
            CourierService courierService) {

        super(null, toValidate);

        Vendor vendor = vendorService.getVendor(user.toString());
        Courier courier = courierService.getCourier(user.toString());

        Object gotUser;

        if (vendor != null) {
            gotUser = vendor;
        } else if (courier != null) {
            gotUser = courier;
        } else {
            return;
        }

        if (next != null) {
            this.next = next.get(gotUser.getClass());
        }

        this.toValidate = toValidate;
        this.user = gotUser;
    }

    /**
     * Validate the status.
     * Steps:
     * 1. Check if the user exists
     * 2. Check if the user has the permission to change the status based on its type
     * 3. If the next validator exists, call it.
     *
     * @param delivery The delivery to validate against
     * @return whether the status allow permission
     */
    @Override
    public boolean process(Delivery delivery) {
        if (user == null) {
            return false;
        }

        if (user.getClass() == Vendor.class) {
            if (toValidate == StatusEnum.IN_TRANSIT
                    || toValidate == StatusEnum.DELIVERED
                    || toValidate == StatusEnum.PENDING) {
                return false;
            }
        } else if (user.getClass() == Courier.class) {
            if (toValidate == StatusEnum.ACCEPTED
                    || toValidate == StatusEnum.REJECTED
                    || toValidate == StatusEnum.PREPARING
                    || toValidate == StatusEnum.GIVEN_TO_COURIER
                    || toValidate == StatusEnum.PENDING) {
                return false;
            }
        }

        if (next == null) {
            return true;
        }
        return next.process(delivery);
    }
}
