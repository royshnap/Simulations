package logic.definition.entity.api;

import logic.definition.property.api.PropertyDefinition;
import java.util.List;

public interface EntityDefinition {
    void setStartPopulation(int startPopulation);
    String getName();
    List<PropertyDefinition> getProps();
    PropertyDefinition findProperty(String propertyName);
    void addProperty(PropertyDefinition property);
    boolean equals(Object o);

    int getStartPopulation();
    int getEndPopulation();
    void setEndPopulation(int endPopulation);


    PropertyDefinition getPropertyDefinitionByName(String propertyName);
}
