package logic.execution.context;

import logic.definition.entity.api.EntityDefinition;
import logic.execution.instance.entity.api.EntityInstance;
import logic.execution.instance.property.api.PropertyInstance;
import logic.rule.action.impl.ReplaceType;
import logic.world.entitiesMatrix.Matrix;

//argument for action - pass this object that aggregate the 3 arguments for the action
//instead of moving 3 big arguments
public interface Context {
    EntityInstance getPrimaryEntityInstance();
    void removeEntity(EntityInstance entityInstance);
    PropertyInstance getEnvironmentVariable(String name);
    Matrix getGrid();
    EntityInstance getSeconderyEntityInstance();
    void replaceEntity (EntityInstance killEntity, EntityDefinition entityDefinition, ReplaceType replaceType);
    int getCurrentTicks();
    int getIsTheFirstSecondary();

}
