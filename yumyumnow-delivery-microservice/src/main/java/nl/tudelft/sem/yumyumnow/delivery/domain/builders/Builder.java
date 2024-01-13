package nl.tudelft.sem.yumyumnow.delivery.domain.builders;

public interface Builder<T> {
    T create();

    void reset();
}
