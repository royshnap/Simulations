package logic.rule.action.impl;

import logic.definition.context.ContextDefinition;
import logic.execution.context.Context;
import logic.execution.instance.entity.api.EntityInstance;
import logic.rule.action.api.AbstractAction;
import logic.rule.action.api.ActionType;

public class Kill extends AbstractAction {
    private boolean isSecondary;
    public Kill(ContextDefinition contextDefinition, String entity) {

        super(ActionType.KILL, contextDefinition);
        isSecondary = contextDefinition.getSecondaryEntity() != null &&
                contextDefinition.getSecondaryEntity().getName().equals(entity);
    }

    @Override
    public void invoke(Context context) {
        EntityInstance entityInstance;
        if(isSecondary){
            entityInstance = context.getSeconderyEntityInstance();

        }
        else{
            entityInstance = context.getPrimaryEntityInstance();
        }

        context.removeEntity(entityInstance);
        context.getGrid().removeEntityFromMatrix(entityInstance);
    }
}
