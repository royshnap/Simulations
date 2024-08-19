package logic.rule.action.impl;

import logic.Expression.api.AbstractExpression;
import logic.Expression.impl.FreeValueExpression;
import logic.Expression.impl.HelperFunctionExpression;
import logic.Expression.impl.PropertyNameExpression;
import logic.definition.context.ContextDefinition;
import logic.definition.property.api.PropertyType;
import logic.definition.property.impl.FloatPropertyDefinition;
import logic.definition.property.impl.IntegerPropertyDefinition;
import logic.execution.context.Context;
import logic.execution.instance.property.api.PropertyInstance;
import logic.Expression.api.Expression;
import logic.rule.action.api.AbstractActionWithProperty;
import logic.rule.action.api.ActionType;
import logic.rule.helperFunction.api.HelperFunction;

public class Set extends AbstractActionWithProperty {
    private String newValue;
    private boolean isSecondary;

    public Set(ContextDefinition contextDefinition,String entity,
               String propertyName, String newValue) {
        super(ActionType.SET, contextDefinition,propertyName);
        this.newValue = newValue;
        isSecondary = contextDefinition.getSecondaryEntity() != null &&
                contextDefinition.getSecondaryEntity().getName().equals(entity);
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
        PropertyInstance specificProperty;
        if(isSecondary){
            specificProperty = context.getSeconderyEntityInstance().getPropertyByName(propertyName);

        }
        else{
            specificProperty = context.getPrimaryEntityInstance().getPropertyByName(propertyName);
        }
        Expression expression = AbstractExpression.valueExpressionByString(newValue ,context.getPrimaryEntityInstance().getEntityDefinition());
        if(specificProperty.getPropertyDefinition() instanceof IntegerPropertyDefinition){
            Integer exp = PropertyType.DECIMAL.convert(valueExpression(context,expression));
            if(((IntegerPropertyDefinition)specificProperty.getPropertyDefinition()).getTo() >= exp
                    && ((IntegerPropertyDefinition)specificProperty.getPropertyDefinition()).getFrom() <= exp){
                specificProperty.setValue(exp);
                return;
            }
            else{
                throw new IllegalArgumentException("The value isn't in the range");
            }
        }
        if(specificProperty.getPropertyDefinition() instanceof FloatPropertyDefinition){
            Float exp = PropertyType.FLOAT.convert(valueExpression(context,expression));
            if(((FloatPropertyDefinition)specificProperty.getPropertyDefinition()).getTo() >= exp
                    && ((FloatPropertyDefinition)specificProperty.getPropertyDefinition()).getFrom() <= exp){
                specificProperty.setValue(exp);

                return;
            }
            else{
                throw new IllegalArgumentException("The value isn't in the range");
            }
        }

        specificProperty.setValue(expression.evaluate());

        specificProperty.setTicks(context.getCurrentTicks());
        if(context.getIsTheFirstSecondary() == 1){
            specificProperty.setSumOfUpdateTicks(specificProperty.getUpdateTicks() + 1);
        }

    }

    public String getNewValue() {
        return newValue;
    }
}
