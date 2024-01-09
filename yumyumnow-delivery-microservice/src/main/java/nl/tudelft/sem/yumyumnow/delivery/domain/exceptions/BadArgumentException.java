package nl.tudelft.sem.yumyumnow.delivery.domain.exceptions;

public class BadArgumentException extends Exception {
    public BadArgumentException(String errorMessage) {
        super(errorMessage);
    }
}
