package logic.execution.instance.entity.manager.impl;
import logic.definition.entity.api.EntityDefinition;
import logic.definition.property.api.PropertyDefinition;
import logic.execution.instance.entity.api.EntityInstance;
import logic.execution.instance.entity.impl.EntityInstanceImpl;

import logic.execution.instance.entity.manager.api.EntityInstanceManager;
import logic.execution.instance.property.api.PropertyInstance;
import logic.execution.instance.property.impl.PropertyInstanceImpl;
import logic.world.WorldInstance;
import logic.world.entitiesMatrix.Cell;
import logic.world.entitiesMatrix.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityInstanceManagerImpl implements EntityInstanceManager {
    private int count;
    private List<EntityInstance> instances;

    public EntityInstanceManagerImpl() {
        count = 0;
        instances = new ArrayList<>();
    }
    @Override
    public void replaceDerived(EntityDefinition newEntity, EntityInstance prevEntityInstance, Matrix matrix){
        EntityInstance newEntityInstance = new EntityInstanceImpl(newEntity, prevEntityInstance.getId());
        Cell cellOfPrev = matrix.findEntityInstanceCell(prevEntityInstance);
        //instances.remove(prevEntityInstance);
        for(PropertyInstance propertyInstance : prevEntityInstance.getProps()){
            try{
                PropertyDefinition newPropertyDefinition = newEntityInstance.getEntityDefinition().getPropertyDefinitionByName(propertyInstance.getPropertyDefinition().getName());
                newEntityInstance.addPropertyInstance(new PropertyInstanceImpl(newPropertyDefinition, propertyInstance.getValue()));

            }
            catch (Exception e){
                continue;
            }
        }
        for(PropertyDefinition propertyDefinition : newEntity.getProps()){
            try{
                PropertyInstance newPropertyDefinition = newEntityInstance.getPropertyByName(propertyDefinition.getName());
            }//maybe propertyDef?
            catch (Exception e){
                newEntityInstance.addPropertyInstance(new PropertyInstanceImpl(propertyDefinition, propertyDefinition.generateValue()));
            }
        }
        instances.add(newEntityInstance);
        newEntityInstance.getEntityDefinition().setEndPopulation(newEntityInstance.getEntityDefinition().getEndPopulation()+1);
        matrix.placeEntity(newEntityInstance, cellOfPrev.getRow(), cellOfPrev.getCol());
    }

    @Override
    public EntityInstance create(EntityDefinition entityDefinition) {

        count++;
        EntityInstance newEntityInstance = new EntityInstanceImpl(entityDefinition, count);
        instances.add(newEntityInstance);


        for (PropertyDefinition propertyDefinition : entityDefinition.getProps()) {
            Object value = propertyDefinition.generateValue();
            PropertyInstance newPropertyInstance = new PropertyInstanceImpl(propertyDefinition, value);
            newEntityInstance.addPropertyInstance(newPropertyInstance);
        }

        return newEntityInstance;
    }

    @Override
    public List<EntityInstance> getInstances() {
        return instances;
    }

    @Override
    public void killEntity(int id) {
        List<EntityInstance> copyOfList = new ArrayList<>(instances);
        for(EntityInstance entityInstance : copyOfList){
            if(entityInstance.getId() == id){
                instances.remove(entityInstance);
                entityInstance.getEntityDefinition().setEndPopulation(
                        entityInstance.getEntityDefinition().getEndPopulation()-1
                );
            }
        }

    }
}
