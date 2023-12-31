package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;

public abstract class AuthProcessor<T> {
    protected AuthProcessor<T> next;

    public AuthProcessor(AuthProcessor<T> next) {
        this.next = next;
    }

    public abstract boolean process(Delivery delivery);
}

