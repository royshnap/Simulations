package logic.definition.value.random;

import logic.definition.value.api.ValueGenerator;

public abstract class AbstractNumericRandomGenerator<T> extends AbstractRandomValueGenerator<T> {
    protected T from;
    protected T to;
    protected AbstractNumericRandomGenerator(T from, T to) {
        this.from = from;
        this.to = to;
    }
}
