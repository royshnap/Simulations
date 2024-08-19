package logic.rule.action.impl;

import logic.Expression.api.AbstractExpression;
import logic.Expression.impl.FreeValueExpression;
import logic.definition.context.ContextDefinition;
import logic.definition.property.api.PropertyType;
import logic.definition.property.impl.FloatPropertyDefinition;
import logic.definition.property.impl.IntegerPropertyDefinition;
import logic.execution.context.Context;
import logic.execution.instance.property.api.PropertyInstance;
import logic.Expression.api.Expression;
import logic.Expression.impl.HelperFunctionExpression;
import logic.Expression.impl.PropertyNameExpression;
import logic.rule.action.api.AbstractActionWithProperty;
import logic.rule.action.api.ActionType;
import logic.rule.helperFunction.api.HelperFunction;

public class Increase extends AbstractActionWithProperty {

    private String byExpression;
    private boolean isSecondary;

    public Increase(ContextDefinition contextDefinition, String propertyName, String entity
            , String expression) {
        super(ActionType.INCREASE, contextDefinition,propertyName);
        byExpression = expression;
        isSecondary = contextDefinition.getSecondaryEntity() != null &&
                contextDefinition.getSecondaryEntity().getName().equals(entity);

    }

    public String getByExpression() {
        return byExpression;
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
        Object result = null;
        PropertyInstance specificProperty;
        if(isSecondary){
            specificProperty = context.getSeconderyEntityInstance().getPropertyByName(propertyName);

        }
        else{
            specificProperty = context.getPrimaryEntityInstance().getPropertyByName(propertyName);
        }
        Expression expression = AbstractExpression.valueExpressionByString(byExpression ,context.getPrimaryEntityInstance().getEntityDefinition());
        if (!verifyNumericPropertyType(specificProperty)) {
            throw new IllegalArgumentException("the property " + propertyName +" isn't a Float or Integer");
        }
        if(specificProperty.getPropertyDefinition() instanceof FloatPropertyDefinition){
            Float val = PropertyType.FLOAT.convert(specificProperty.getValue());
            Float exp = PropertyType.FLOAT.convert(valueExpression(context,expression));
            result = val + exp;
        }
        else if(specificProperty.getPropertyDefinition() instanceof IntegerPropertyDefinition){
            Integer val = PropertyType.DECIMAL.convert(specificProperty.getValue());
            Integer exp = PropertyType.DECIMAL.convert(valueExpression(context,expression));
            result = val + exp;
        }

        if(specificProperty.getPropertyDefinition() instanceof FloatPropertyDefinition){
            if(((FloatPropertyDefinition)specificProperty.getPropertyDefinition()).getTo() >=(Float) result){
                specificProperty.setValue(result);
            }
        }
        else{
            if(((IntegerPropertyDefinition)specificProperty.getPropertyDefinition()).getTo() >= (Integer) result){
                specificProperty.setValue(result);
            }
        }
        specificProperty.setTicks(context.getCurrentTicks());
        if(context.getIsTheFirstSecondary() == 1){
            specificProperty.setSumOfUpdateTicks(specificProperty.getUpdateTicks() + 1);
        }



    }

    private boolean verifyNumericPropertyType(PropertyInstance propertyValue) {
        return
                PropertyType.DECIMAL.equals(propertyValue.getPropertyDefinition().getType()) || PropertyType.FLOAT.equals(propertyValue.getPropertyDefinition().getType());
    }
}
