package logic.definition.property.api;

import logic.definition.value.api.ValueGenerator;

public abstract class AbstractPropertyDefinition<T> implements PropertyDefinition{

    private String name;
    private PropertyType propertyType;

    private final ValueGenerator<T> valueGenerator;


    public AbstractPropertyDefinition(String name, PropertyType type, ValueGenerator<T> valueGenerator){
        this.name = name;
        propertyType = type;
        this.valueGenerator = valueGenerator;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PropertyType getType() {
        return propertyType;
    }

    @Override
    public Object generateValue() {
        return valueGenerator.generateValue();
    }

    @Override
    public ValueGenerator<T> getValueGenerator() {
        return valueGenerator;
    }
}
