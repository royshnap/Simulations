package logic.rule.helperFunction.api;

import logic.Expression.api.Expression;
import logic.execution.context.Context;

public abstract class HelperFunction {

    protected HelperFunctionType helperFunctionType;

    protected Expression expression;
    protected Expression expressionTwo;
    protected String entityName;
    protected String propertyName;
    public HelperFunction(HelperFunctionType helperFunctionType,
                          Expression expression){
        this.helperFunctionType = helperFunctionType;
        this.expression = expression;
    }

    public HelperFunction(HelperFunctionType helperFunctionType, Expression expression, Expression expressionTwo){
        this.helperFunctionType = helperFunctionType;
        this.expression = expression;
        this.expressionTwo = expressionTwo;
    }

    public HelperFunction(HelperFunctionType helperFunctionType, String entityName, String propertyName){
        this.helperFunctionType = helperFunctionType;
        this.entityName = entityName;
        this.propertyName = propertyName;
    }

    public Expression getExpression() {
        return expression;
    }

    public abstract Object run(Context context);

}
