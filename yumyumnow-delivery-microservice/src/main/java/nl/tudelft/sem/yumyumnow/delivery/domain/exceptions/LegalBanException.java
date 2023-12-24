package nl.tudelft.sem.yumyumnow.delivery.domain.exceptions;

public class LegalBanException extends Exception {
    public LegalBanException(String errorMessage) {
        super(errorMessage);
    }
}
