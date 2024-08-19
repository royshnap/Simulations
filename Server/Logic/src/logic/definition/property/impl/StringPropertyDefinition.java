package logic.definition.property.impl;

import logic.definition.property.api.AbstractPropertyDefinition;
import logic.definition.property.api.PropertyType;
import logic.definition.value.api.ValueGenerator;

public class StringPropertyDefinition extends AbstractPropertyDefinition<String> {

    public StringPropertyDefinition(String name, PropertyType type, ValueGenerator<String> valueGenerator) {
        super(name, type, valueGenerator);
    }
}
