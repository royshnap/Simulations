package logic.execution.instance.entity.impl;

import logic.definition.entity.api.EntityDefinition;
import logic.execution.instance.entity.api.EntityInstance;
import logic.execution.instance.property.api.PropertyInstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EntityInstanceImpl implements EntityInstance {


    private final EntityDefinition entityDefinition;
    private Map<String, PropertyInstance> properties;
    private int id;

    public EntityInstanceImpl(EntityDefinition entityDefinition, int id) {
        this.entityDefinition = entityDefinition;
        properties = new HashMap<>();
        this.id = id;
    }

    @Override
    public EntityDefinition getEntityDefinition() {
        return entityDefinition;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Collection<PropertyInstance> getProps() {
        return properties.values();
    }

    @Override
    public PropertyInstance getPropertyByName(String name) {
        if (!properties.containsKey(name)) {
            throw new IllegalArgumentException("for entity of type " + entityDefinition.getName() + " has no property named " + name);
        }

        return properties.get(name);
    }

    @Override
    public void addPropertyInstance(PropertyInstance propertyInstance) {
        properties.put(propertyInstance.getPropertyDefinition().getName(), propertyInstance);
    }
}
