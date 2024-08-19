package logic.definition.property.impl;

import logic.definition.property.api.AbstractPropertyDefinition;
import logic.definition.property.api.PropertyType;
import logic.definition.value.api.ValueGenerator;

public class BooleanPropertyDefinition extends AbstractPropertyDefinition<Boolean> {

    public BooleanPropertyDefinition(String name, PropertyType type, ValueGenerator<Boolean> valueGenerator) {
        super(name, type, valueGenerator);
    }
}
