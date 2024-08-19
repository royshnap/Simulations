package logic.definition.value.fixed;

import logic.definition.value.api.ValueGenerator;

public class FixedValueGenerator <T> implements ValueGenerator<T> {

    private T fixedValue;

    public FixedValueGenerator(T value){
        fixedValue = value;
    }

    @Override
    public T generateValue() {
        return fixedValue;
    }
}
