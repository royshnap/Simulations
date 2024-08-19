package logic.rule.action.impl.calculation;

import logic.Expression.impl.FreeValueExpression;
import logic.Expression.impl.HelperFunctionExpression;
import logic.Expression.impl.PropertyNameExpression;
import logic.definition.context.ContextDefinition;
import logic.execution.context.Context;
import logic.Expression.api.Expression;
import logic.execution.instance.property.api.PropertyInstance;
import logic.rule.action.api.AbstractActionWithProperty;
import logic.rule.action.api.ActionType;
import logic.rule.helperFunction.api.HelperFunction;

public abstract class Calculation extends AbstractActionWithProperty {
    protected String argument1;
    protected String argument2;
    protected ActionType actionType;
    protected boolean isSecondary;

    public Calculation(ActionType actionType, ContextDefinition contextDefinition, String arg1, String arg2,
                       String entity,String propertyName) {
        super(actionType, contextDefinition, propertyName);
        this.actionType = actionType;
        argument1 = arg1;
        argument2 = arg2;
        isSecondary = contextDefinition.getSecondaryEntity() != null &&
                contextDefinition.getSecondaryEntity().getName().equals(entity);
    }

    public String getArgument1() {
        return argument1;
    }

    public String getArgument2() {
        return argument2;
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
    public abstract void invoke(Context context);


}
