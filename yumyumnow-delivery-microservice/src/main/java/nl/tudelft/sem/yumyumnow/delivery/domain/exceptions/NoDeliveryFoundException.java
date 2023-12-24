package nl.tudelft.sem.yumyumnow.delivery.domain.exceptions;

/**
 * Exception to be thrown when the delivery with the given id cannot be found.
 */
public class NoDeliveryFoundException extends Exception {
    public NoDeliveryFoundException(String errorMessage) {
        super(errorMessage);
    }
}
