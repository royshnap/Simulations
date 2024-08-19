package logic.definition.property.impl;

import logic.definition.property.api.AbstractPropertyDefinition;
import logic.definition.property.api.PropertyType;
import logic.definition.value.api.ValueGenerator;

public class FloatPropertyDefinition extends AbstractNumericPropertyDefinition<Float> {

    public FloatPropertyDefinition(String name, PropertyType type, ValueGenerator<Float> valueGenerator,
                                   float from, float to) {
        super(name, type, valueGenerator,from,to);

    }

}
