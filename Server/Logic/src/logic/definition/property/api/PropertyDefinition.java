package logic.definition.property.api;

import logic.definition.value.api.ValueGenerator;

public interface PropertyDefinition<T> {
    String getName();
    PropertyType getType();
    Object generateValue();

    ValueGenerator<T> getValueGenerator();

}
