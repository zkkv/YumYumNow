package nl.tudelft.sem.yumyumnow.delivery.domain.exceptions;

/**
 * Exception to be thrown when the passed arguments are invalid.
 */
public class BadArgumentException extends Exception {
    public BadArgumentException(String errorMessage) {
        super(errorMessage);
    }
}
