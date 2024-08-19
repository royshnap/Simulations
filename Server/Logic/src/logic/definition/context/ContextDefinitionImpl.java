package logic.definition.context;

import logic.definition.entity.api.EntityDefinition;
import logic.rule.action.impl.condition.Condition;

public class ContextDefinitionImpl implements ContextDefinition {
    private EntityDefinition primaryEntity;
    private EntityDefinition seconderyEntity;
    private String amountOfSeconderyEntites;
    private Condition conditionOfSecondery;

    public ContextDefinitionImpl(EntityDefinition primaryEntity, EntityDefinition seconderyEntity, String amountOfSeconderyEntites, Condition conditionOfSecondery) {
        this.primaryEntity = primaryEntity;
        this.seconderyEntity = seconderyEntity;
        this.amountOfSeconderyEntites = amountOfSeconderyEntites;
        this.conditionOfSecondery = conditionOfSecondery;
    }

    public Condition getConditionOfSecondary() {
        return conditionOfSecondery;
    }

    public EntityDefinition getPrimaryEntity() {
        return primaryEntity;
    }

    public EntityDefinition getSecondaryEntity() {
        return seconderyEntity;
    }

    public String getAmountOfSecondaryEntities() {
        return amountOfSeconderyEntites;
    }
}
