package logic.definition.context;

import logic.definition.entity.api.EntityDefinition;
import logic.rule.action.impl.condition.Condition;

public interface ContextDefinition {
    Condition getConditionOfSecondary();
    EntityDefinition getPrimaryEntity();
    EntityDefinition getSecondaryEntity();
    String getAmountOfSecondaryEntities();

}
