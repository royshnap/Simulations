package logic.rule.action.api;

import logic.definition.context.ContextDefinition;
import logic.definition.entity.api.EntityDefinition;

public abstract class AbstractActionWithProperty extends AbstractAction{

   protected String propertyName;
    public AbstractActionWithProperty(ActionType actionType, ContextDefinition contextDefinition,
                                      String propertyName) {
        super(actionType, contextDefinition);
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
