package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;
import java.util.Map;
import java.util.Objects;

public class UserIsAdminValidator extends AuthProcessor<Map<String, Object>> {

    /**
     * Constructor for the UserIsAdminValidator class.
     *
     * @param next           Map of the next validators in the chain by user type
     * @param toValidate     The user Map to validate
     */
    public UserIsAdminValidator(
            AuthProcessor next,
            Map<String, Object> toValidate) {

        super(next, toValidate);
    }

    /**
     * Validate that user is an admin.
     * Steps:
     * 1. Check if the user is an admin.
     * 2. If the next validator exists, call it.
     *
     * @param delivery The delivery to validate against
     * @return whether the user is an admin
     */
    @Override
    public boolean process(Delivery delivery) {
        String type = (String) ((Map) toValidate).get("userType");

        if (!Objects.equals(type, "Admin")) {
            return false;
        }
        if (next == null) {
            return true;
        }
        return next.process(delivery);
    }
}
