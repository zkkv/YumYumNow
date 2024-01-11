package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;

/**
 * Abstract class for the chain of responsibility pattern.
 * @param <T> The type of the object to validate
 *           (e.g. Courier, Customer, Vendor, Order)
 * To implement a new validator, extend this class and implement the process method.
 */
public abstract class AuthProcessor<T> {
    protected AuthProcessor<T> next;
    protected Object toValidate;

    /**
     * Constructor for the AuthProcessor.
     * @param next The next validator in the chain
     * @param toValidate The object to validate
     */
    public AuthProcessor(AuthProcessor<T> next, Object toValidate) {
        this.next = next;
        this.toValidate = toValidate;
    }

    /**
     * Process the validation.
     * @param delivery The delivery to validate against
     * @return True if the validation was successful, false otherwise
     */
    public abstract boolean process(Delivery delivery);
}

