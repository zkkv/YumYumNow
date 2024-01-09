package nl.tudelft.sem.yumyumnow.delivery.domain.exceptions;

/**
 * Exception to be thrown when there is a legal reason of why a valid response cannot be sent back.
 */
public class LegalBanException extends Exception {
    public LegalBanException(String errorMessage) {
        super(errorMessage);
    }
}
