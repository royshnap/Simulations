package logic.rule.action.impl.condition;

import logic.Expression.api.AbstractExpression;
import logic.Expression.impl.FreeValueExpression;
import logic.definition.entity.api.EntityDefinition;
import logic.definition.property.impl.FloatPropertyDefinition;
import logic.definition.property.impl.IntegerPropertyDefinition;
import logic.execution.context.Context;
import logic.execution.instance.property.api.PropertyInstance;
import logic.Expression.api.Expression;
import logic.Expression.impl.HelperFunctionExpression;
import logic.Expression.impl.PropertyNameExpression;
import logic.rule.helperFunction.api.HelperFunction;

public class SingleCondition implements Condition {

    private Operation operator;

    private String value;
    private String expression;

    public SingleCondition(EntityDefinition entityDefinition, Operation op, String expression,
                           String value){
        operator = op;

        this.expression = expression;
        this.value = value;

    }

    public Operation getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }

    public String getExpression(){
        return expression;
    }

    @Override
    public boolean evaluate(Context context) {
        Expression expressionProperty = AbstractExpression.valueExpressionByString(expression, context.getPrimaryEntityInstance().getEntityDefinition());
        Expression expressionOfValue = AbstractExpression.valueExpressionByString(value ,context.getPrimaryEntityInstance().getEntityDefinition());
        String operatorString = operator.toString();
        switch (operatorString) {
            case "equals":
                return valueExpression(context, expressionProperty).equals(valueExpression(context, expressionOfValue));

            case "not equals":
                return !valueExpression(context, expressionProperty).equals(valueExpression(context, expressionOfValue));

            case "bigger than":
                if(valueExpression(context, expressionProperty) instanceof Float){
                    Object res = valueExpression(context, expressionOfValue);
                    return (Float)valueExpression(context, expressionProperty)  > (Float)(res instanceof Integer? ((Integer)res).floatValue() : res);
                }
                else if(valueExpression(context, expressionProperty) instanceof Integer){
                    return (Integer)valueExpression(context, expressionProperty) > (Integer) valueExpression(context, expressionOfValue);
                }

            case "less than":
                if(valueExpression(context, expressionProperty) instanceof Float){
                    Object res = valueExpression(context, expressionOfValue);
                    return (Float)valueExpression(context, expressionProperty)  < (Float)(res instanceof Integer? ((Integer)res).floatValue() : res);
                }
                else if(valueExpression(context, expressionProperty) instanceof Integer){
                    return (Integer)valueExpression(context, expressionProperty) < (Integer) valueExpression(context, expressionOfValue);
                }
                break;
        }

        throw new IllegalArgumentException("Invalid operator: " + operatorString);
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
}
