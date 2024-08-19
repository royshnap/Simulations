package dto;


public class PropertyInstanceDTO {

    private final PropertyDefinitionDTO propertyDefinition;
    private Object value;

    public void setValue(Object value) {
        this.value = value;
    }

    public PropertyDefinitionDTO getPropertyDefinition() {
        return propertyDefinition;
    }

    public Object getValue() {
        return value;
    }

    public PropertyInstanceDTO(PropertyDefinitionDTO propertyDefinition, Object value) {
        this.propertyDefinition = propertyDefinition;
        this.value = value;
    }
}
