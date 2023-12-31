package nl.tudelft.sem.yumyumnow.delivery.application.validators;

import lombok.Setter;
import nl.tudelft.sem.yumyumnow.delivery.domain.dto.Courier;
import nl.tudelft.sem.yumyumnow.delivery.model.Delivery;

public abstract class AuthProcessor<T> {
    protected AuthProcessor<T> next;
    protected Object toValidate;

    public AuthProcessor(AuthProcessor<T> next, Object toValidate) {
        this.next = next;
        this.toValidate = toValidate;
    }

    public abstract boolean process(Delivery delivery);
}

