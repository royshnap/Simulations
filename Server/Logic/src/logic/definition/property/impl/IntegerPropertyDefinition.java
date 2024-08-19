package logic.definition.property.impl;

import logic.definition.property.api.AbstractPropertyDefinition;
import logic.definition.property.api.PropertyType;
import logic.definition.value.api.ValueGenerator;

public class IntegerPropertyDefinition extends AbstractNumericPropertyDefinition<Integer> {


    public IntegerPropertyDefinition(String name, PropertyType type, ValueGenerator<Integer> valueGenerator,
                                     int from, int to) {
        super(name, type, valueGenerator,from,to);

    }


}
