package nl.tudelft.sem.yumyumnow.delivery.domain.exceptions;

public class ServiceUnavailableException extends Exception {
    public ServiceUnavailableException(String errorMessage) {
        super(errorMessage);
    }
}
