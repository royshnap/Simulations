package logic.definition.entity.impl;

import logic.definition.entity.api.EntityDefinition;
import logic.definition.property.api.PropertyDefinition;
import java.util.*;

public class EntityDefinitionImpl implements EntityDefinition {
    private List<PropertyDefinition> properties;

    private int endPopulation;
    private int startPopulation;
    private String name;

    public EntityDefinitionImpl(String name){
        properties = new ArrayList<>();
        startPopulation = 0;
        endPopulation = 0;
        boolean containsSpaces = name.contains(" ");
        if(!containsSpaces){
            this.name = name;
        }
        else{
            throw new IllegalArgumentException("The name " + name + " contains spaces");
        }

    }

    @Override
    public void setStartPopulation(int startPopulation) {
        this.startPopulation = startPopulation;
    }

    @Override
    public PropertyDefinition getPropertyDefinitionByName(String propertyName) {
        for(PropertyDefinition propertyDefinition : properties){
            if(propertyDefinition.getName().equals(propertyName)){
                return propertyDefinition;
            }
        }
        return null;
    }

    @Override
    public int getEndPopulation() {
        return endPopulation;
    }

    @Override
    public void setEndPopulation(int endPopulation) {
        this.endPopulation = endPopulation;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addProperty(PropertyDefinition property){
        properties.add(property);
    }

    @Override
    public PropertyDefinition findProperty(String propertyName) throws NoSuchElementException {
        for(PropertyDefinition p : properties){
            if(p.getName().equals(propertyName)){
                return p;
            }
        }
        throw new NoSuchElementException("The property name " + propertyName + " doesn't exists");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityDefinitionImpl entity = (EntityDefinitionImpl) o;
        return Objects.equals(name, entity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int getStartPopulation(){
        return startPopulation;
    }

    @Override
    public List<PropertyDefinition> getProps() {
        return properties;
    }
}
