package logic.rule.action.impl;

import logic.Expression.api.AbstractExpression;
import logic.Expression.api.Expression;
import logic.Expression.impl.FreeValueExpression;
import logic.Expression.impl.HelperFunctionExpression;
import logic.Expression.impl.PropertyNameExpression;
import logic.definition.context.ContextDefinition;
import logic.definition.entity.api.EntityDefinition;
import logic.execution.context.Context;
import logic.execution.instance.entity.api.EntityInstance;
import logic.execution.instance.property.api.PropertyInstance;
import logic.rule.action.api.AbstractAction;
import logic.rule.action.api.Action;
import logic.rule.action.api.ActionType;
import logic.rule.helperFunction.api.HelperFunction;

import java.util.List;

public class Proximity extends AbstractAction {
    private ContextDefinition contextDefinition;
    private EntityDefinition sourceEntityDef;
    private EntityDefinition targetEntityDef;
    private String of;
    private List<Action> actionsList;//was AbstractAction maybe need to change

    public Proximity(ContextDefinition contextDefinition, String of, List<Action> actionsList){
        super(ActionType.PROXIMITY, contextDefinition);
        this.targetEntityDef = contextDefinition.getSecondaryEntity();
        this.sourceEntityDef = contextDefinition.getPrimaryEntity();
        this.of = of;
        this.actionsList = actionsList;
    }

    @Override
    public void invoke(Context context){
        EntityInstance sourceEnt = context.getPrimaryEntityInstance();

        Expression ofValue =AbstractExpression.valueExpressionByString(of ,context.getPrimaryEntityInstance().getEntityDefinition());
        Object rank = valueExpression(context,ofValue);
        if(rank instanceof Integer){
            boolean isSurrounding = context.getGrid().isEntitySurrounding(sourceEnt, targetEntityDef,(Integer) rank);
            if(isSurrounding){
                for (Action action : actionsList){
                    if(action.getActionType().equals(ActionType.REPLACE) && context.getIsTheFirstSecondary() == 1){
                        action.invoke(context);
                    }
                    else if(!action.getActionType().equals(ActionType.REPLACE)){
                        action.invoke(context);
                    }
                }
            }
        }
        else if(rank instanceof Float){
            int intValue = (int) Math.floor((Float) rank);
            boolean isSurrounding = context.getGrid().isEntitySurrounding(sourceEnt, targetEntityDef,intValue);
            if(isSurrounding){
                for (Action action : actionsList){
                    if(action.getActionType().equals(ActionType.REPLACE) && context.getIsTheFirstSecondary() == 1){
                        action.invoke(context);
                    }
                    else if(!action.getActionType().equals(ActionType.REPLACE)){
                        action.invoke(context);
                    }
                }
            }
        }
        else{
            throw new IllegalArgumentException("the value " + of +" isn't an integer");
        }

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

    public String getOf() {
        return of;
    }
    public int getSizeOfAction(){
        return actionsList.size();
    }
}
