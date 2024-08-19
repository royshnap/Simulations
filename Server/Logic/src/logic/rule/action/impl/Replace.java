package logic.rule.action.impl;

import logic.definition.context.ContextDefinition;
import logic.definition.entity.api.EntityDefinition;
import logic.execution.context.Context;
import logic.rule.action.api.AbstractAction;
import logic.rule.action.api.ActionType;

public class Replace extends AbstractAction {

    private ContextDefinition contextDefinition;
    private EntityDefinition createEntityDef;
    private EntityDefinition killEntityDef;
    private ReplaceType replaceType;

    public Replace(ContextDefinition contextDefinition, ReplaceType replaceType){
        super(ActionType.REPLACE, contextDefinition);
        this.createEntityDef = contextDefinition.getSecondaryEntity();
        this.killEntityDef = contextDefinition.getPrimaryEntity();
        this.replaceType = replaceType;
    }

    @Override
    public void invoke(Context context) {

        context.replaceEntity(context.getPrimaryEntityInstance(), createEntityDef, replaceType);

    }
}
