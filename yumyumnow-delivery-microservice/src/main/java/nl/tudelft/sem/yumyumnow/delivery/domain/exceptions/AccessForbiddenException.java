package nl.tudelft.sem.yumyumnow.delivery.domain.exceptions;

/**
 * Exception to be thrown when the given user does not have rights to use an endpoint.
 */
public class AccessForbiddenException extends Exception {
    public AccessForbiddenException(String errorMessage) {
        super(errorMessage);
    }
}
