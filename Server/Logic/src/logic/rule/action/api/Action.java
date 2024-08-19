package logic.rule.action.api;

import logic.definition.context.ContextDefinition;
import logic.definition.entity.api.EntityDefinition;
import logic.execution.context.Context;

public interface Action {
    void invoke(Context context);
    ActionType getActionType();
    ContextDefinition getContextDefinition();

}
