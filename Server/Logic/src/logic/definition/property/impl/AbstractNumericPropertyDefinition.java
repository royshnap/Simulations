package logic.definition.property.impl;

import logic.definition.property.api.AbstractPropertyDefinition;
import logic.definition.property.api.PropertyType;
import logic.definition.value.api.ValueGenerator;

public abstract class AbstractNumericPropertyDefinition<T> extends AbstractPropertyDefinition<T> {
    private T from;
    private T to;


    public AbstractNumericPropertyDefinition(String name, PropertyType type, ValueGenerator<T> valueGenerator
    ,T from, T to) {
        super(name, type, valueGenerator);
        this.from = from;
        this.to = to;
    }

    public T getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }
}



