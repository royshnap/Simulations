package logic.execution.instance.entity.manager.api;
import logic.definition.entity.api.EntityDefinition;
import logic.execution.instance.entity.api.EntityInstance;
import logic.world.entitiesMatrix.Matrix;

import java.util.List;
public interface EntityInstanceManager {
    EntityInstance create(EntityDefinition entityDefinition);
    List<EntityInstance> getInstances();
    void killEntity(int id);
    void replaceDerived(EntityDefinition newEntityDefinition, EntityInstance prevEntityInstance, Matrix matrix);
}
