package logic.rule.helperFunction.impl;

import logic.Expression.api.Expression;
import logic.Expression.impl.FloatExpression;
import logic.Expression.impl.IntegerExpression;
import logic.Expression.impl.PropertyNameExpression;
import logic.execution.context.Context;
import logic.execution.instance.property.api.PropertyInstance;
import logic.rule.helperFunction.api.HelperFunction;
import logic.rule.helperFunction.api.HelperFunctionType;

import java.util.List;
import java.util.Random;

public class EvaluateFunction extends HelperFunction {
    public EvaluateFunction(String entity, String property){
        super(HelperFunctionType.EVALUATE, entity, property);
    }

    @Override
    public Object run(Context context) throws IllegalArgumentException{
        if(entityName.equals(context.getPrimaryEntityInstance().getEntityDefinition().getName())){
            PropertyInstance propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(propertyName);

            return propertyInstance.getValue();

        }
        else if(entityName.equals(context.getSeconderyEntityInstance().getEntityDefinition().getName())){
            PropertyInstance propertyInstance = context.getSeconderyEntityInstance().getPropertyByName(propertyName);

                return propertyInstance.getValue();
            }

        else{
            throw new IllegalArgumentException("This evaluate function" + "doesn't have property name expression");
        }
    }
}
