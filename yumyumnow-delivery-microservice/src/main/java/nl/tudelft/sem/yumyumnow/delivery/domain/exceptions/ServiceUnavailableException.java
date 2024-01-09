package nl.tudelft.sem.yumyumnow.delivery.domain.exceptions;

/**
 * Exception to be thrown when a service delivery microservice relies on does not respond in
 * expected manner.
 */
public class ServiceUnavailableException extends Exception {
    public ServiceUnavailableException(String errorMessage) {
        super(errorMessage);
    }
}
