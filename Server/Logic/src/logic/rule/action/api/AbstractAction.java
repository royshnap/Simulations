package logic.rule.action.api;

import logic.definition.context.ContextDefinition;
import logic.definition.entity.api.EntityDefinition;

public abstract class AbstractAction implements Action{
    private ActionType actionType;
    private ContextDefinition contextDefinition;

    protected AbstractAction(ActionType actionType, ContextDefinition contextDefinition) {
        this.actionType = actionType;
        this.contextDefinition = contextDefinition;
    }

    @Override
    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public ContextDefinition getContextDefinition() {
        return contextDefinition;
    }
}
