package nl.tudelft.sem.yumyumnow.delivery.domain.exceptions;

public class AccessForbiddenException extends Exception {
    public AccessForbiddenException(String errorMessage) {
        super(errorMessage);
    }
}
