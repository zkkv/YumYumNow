package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;

public abstract class AuthProcessor<T> {
    public AuthProcessor<T> next;

    public abstract boolean process(T toValidate, Delivery delivery);
}

