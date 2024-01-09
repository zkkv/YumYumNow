package nl.tudelft.sem.yumyumnow.delivery.domain.exceptions;

public class NoDeliveryFoundException extends Exception {
    public NoDeliveryFoundException(String errorMessage) {
        super(errorMessage);
    }
}
