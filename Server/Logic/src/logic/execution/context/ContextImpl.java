package logic.execution.context;

import logic.definition.entity.api.EntityDefinition;
import logic.execution.instance.entity.api.EntityInstance;
import logic.execution.instance.entity.manager.api.EntityInstanceManager;
import logic.execution.instance.environment.api.ActiveEnvironment;
import logic.execution.instance.property.api.PropertyInstance;
import logic.rule.action.impl.ReplaceType;
import logic.world.entitiesMatrix.Matrix;

public class ContextImpl implements Context{

    private EntityInstance primaryEntityInstance;
    private EntityInstanceManager entityInstanceManager;
    private ActiveEnvironment activeEnvironment;
    private Matrix grid;
    private EntityInstance seconderyEntityInstance;
    private int currentTicks;
    private int isTheFirstSecondary;

    public ContextImpl(EntityInstance primaryEntityInstance, EntityInstance seconderyEntityInstance, EntityInstanceManager entityInstanceManager, ActiveEnvironment activeEnvironment,
                       Matrix grid, int ticks) {
        this.primaryEntityInstance = primaryEntityInstance;
        this.entityInstanceManager = entityInstanceManager;
        this.activeEnvironment = activeEnvironment;
        this.grid = grid;
        this.currentTicks = ticks;
        this.seconderyEntityInstance = seconderyEntityInstance;
    }

    public ContextImpl(EntityInstance primaryEntityInstance, EntityInstance seconderyEntityInstance, EntityInstanceManager entityInstanceManager, ActiveEnvironment activeEnvironment,
                       Matrix grid, int ticks, int isTheFirstSecondary) {
        this.primaryEntityInstance = primaryEntityInstance;
        this.entityInstanceManager = entityInstanceManager;
        this.activeEnvironment = activeEnvironment;
        this.grid = grid;
        this.seconderyEntityInstance = seconderyEntityInstance;
        this.currentTicks = ticks;
        this.isTheFirstSecondary = isTheFirstSecondary;
    }

    @Override
    public int getCurrentTicks(){
        return currentTicks;

    }

    public int getIsTheFirstSecondary() {
        return isTheFirstSecondary;
    }

    @Override
    public Matrix getGrid() {
        return grid;
    }

    @Override
    public EntityInstance getSeconderyEntityInstance() {
        return seconderyEntityInstance;
    }

    @Override
    public EntityInstance getPrimaryEntityInstance() {
        return primaryEntityInstance;
    }

    @Override
    public void removeEntity(EntityInstance entityInstance) {
        entityInstanceManager.killEntity(entityInstance.getId());
    }
    @Override
    public void replaceEntity (EntityInstance killEntity,EntityDefinition createEntity, ReplaceType replaceType)
    {
        if (replaceType == ReplaceType.SCRATCH){
            entityInstanceManager.killEntity(killEntity.getId());
            EntityInstance newEntity = entityInstanceManager.create(createEntity);
            newEntity.getEntityDefinition().setEndPopulation(newEntity.getEntityDefinition().getEndPopulation()+1);
            grid.placeEntityRandomly(newEntity);
        }
        else if(replaceType == ReplaceType.DERIVED){
            entityInstanceManager.killEntity(killEntity.getId());
            entityInstanceManager.replaceDerived(createEntity, killEntity, grid);
        }
    }

    @Override
    public PropertyInstance getEnvironmentVariable(String name) {
        return activeEnvironment.getProperty(name);
    }
}
