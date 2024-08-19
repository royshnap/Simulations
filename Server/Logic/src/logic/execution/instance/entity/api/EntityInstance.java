package logic.execution.instance.entity.api;
import logic.definition.entity.api.EntityDefinition;
import logic.execution.instance.property.api.PropertyInstance;

import java.util.Collection;

public interface EntityInstance {
    Collection<PropertyInstance> getProps();
    PropertyInstance getPropertyByName(String name);
    void addPropertyInstance(PropertyInstance propertyInstance);
    int getId();

    EntityDefinition getEntityDefinition();
}