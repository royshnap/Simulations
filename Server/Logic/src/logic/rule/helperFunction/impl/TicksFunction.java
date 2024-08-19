package logic.rule.helperFunction.impl;

import logic.execution.context.Context;
import logic.execution.instance.property.api.PropertyInstance;
import logic.rule.helperFunction.api.HelperFunction;
import logic.rule.helperFunction.api.HelperFunctionType;

public class TicksFunction extends HelperFunction {
    public TicksFunction(String entityName, String propertyName) {
        super(HelperFunctionType.TICKS, entityName, propertyName);
    }

    @Override
    public Object run(Context context) throws IllegalArgumentException{
        if(entityName.equals(context.getPrimaryEntityInstance().getEntityDefinition().getName())){
            PropertyInstance propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(propertyName);
            return context.getCurrentTicks() - propertyInstance.getUpdateTicks();

        }
        else if(entityName.equals(context.getSeconderyEntityInstance().getEntityDefinition().getName())){
            PropertyInstance propertyInstance = context.getSeconderyEntityInstance().getPropertyByName(propertyName);
            return context.getCurrentTicks() - propertyInstance.getUpdateTicks();
        }
        else{
            throw new IllegalArgumentException("For the entities: " + context.getPrimaryEntityInstance()+ ", "
            + context.getSeconderyEntityInstance() + " there isn't property " + propertyName);
        }
    }
}
