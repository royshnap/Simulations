package logic.rule.action.impl.calculation;

import logic.Expression.api.AbstractExpression;
import logic.Expression.impl.FreeValueExpression;
import logic.Expression.impl.HelperFunctionExpression;
import logic.Expression.impl.PropertyNameExpression;
import logic.definition.context.ContextDefinition;
import logic.definition.entity.api.EntityDefinition;
import logic.definition.property.api.PropertyType;
import logic.definition.property.impl.FloatPropertyDefinition;
import logic.definition.property.impl.IntegerPropertyDefinition;
import logic.execution.context.Context;
import logic.execution.instance.property.api.PropertyInstance;
import logic.Expression.api.Expression;
import logic.rule.action.api.ActionType;
import logic.rule.helperFunction.api.HelperFunction;

public class Multiply extends Calculation{

    public Multiply(ContextDefinition contextDefinition, String arg1, String arg2,
                    String entity,String propertyName){
        super(ActionType.MULTIPLY,contextDefinition,arg1, arg2,entity, propertyName);
    }

    private Object valueExpression(Context context,Expression expression){
        if(expression instanceof PropertyNameExpression) {
            PropertyInstance propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(((PropertyNameExpression) expression).evaluate());
            return propertyInstance.getValue();
        }

        else if(expression instanceof FreeValueExpression){
            return expression.evaluate();
        }
        else if(expression instanceof HelperFunctionExpression){
            HelperFunction helperFunction = (HelperFunction) expression.evaluate();
            return  helperFunction.run(context);

        }
        return null;

    }




    @Override
    public void invoke(Context context) {
        PropertyInstance propertyInstance;
        if(isSecondary){
            propertyInstance = context.getSeconderyEntityInstance().getPropertyByName(propertyName);

        }
        else{
            propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(propertyName);
        }
        Expression expr1 = AbstractExpression.valueExpressionByString(argument1, context.getPrimaryEntityInstance().getEntityDefinition());
        Expression expr2 = AbstractExpression.valueExpressionByString(argument2, context.getPrimaryEntityInstance().getEntityDefinition());
        float exp1Value = PropertyType.FLOAT.convert(valueExpression(context,expr1));
        float exp2Value = PropertyType.FLOAT.convert(valueExpression(context,expr2));
        if (propertyInstance.getPropertyDefinition() instanceof IntegerPropertyDefinition) {
            propertyInstance.setValue(PropertyType.DECIMAL.convert(exp1Value * exp2Value));
            propertyInstance.setTicks(context.getCurrentTicks());
        }
        else if (propertyInstance.getPropertyDefinition() instanceof FloatPropertyDefinition) {

            propertyInstance.setValue(PropertyType.FLOAT.convert(exp1Value * exp2Value));
            propertyInstance.setTicks(context.getCurrentTicks());
            if(context.getIsTheFirstSecondary() == 1){
                propertyInstance.setSumOfUpdateTicks(propertyInstance.getUpdateTicks() + 1);
            }
        }


        else{
            throw new IllegalArgumentException("the property " + propertyName + " isn't a Float or Integer");
        }

    }
}
