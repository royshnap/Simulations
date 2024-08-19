package logic.definition.value.random;

import logic.definition.value.api.ValueGenerator;

public abstract class AbstractRandomValueGenerator<T> implements ValueGenerator<T> {

    @Override
    public abstract T generateValue();
}
