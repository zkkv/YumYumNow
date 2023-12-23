package nl.tudelft.sem.yumyumnow.delivery.domain.exceptions;

public class BadArgumentsException extends Exception {
    public BadArgumentsException(String errorMessage) {
        super(errorMessage);
    }
}
