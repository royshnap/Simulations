package logic.rule.helperFunction.impl;

import logic.Expression.api.Expression;
import logic.Expression.impl.FloatExpression;
import logic.Expression.impl.HelperFunctionExpression;
import logic.execution.context.Context;
import logic.rule.helperFunction.api.HelperFunction;
import logic.rule.helperFunction.api.HelperFunctionType;

public class PercentFunction extends HelperFunction {
    public PercentFunction(Expression expression1, Expression expression2) {
        super(HelperFunctionType.PERCENT, expression1, expression2);
    }

    @Override
    public Object run(Context context){
        float amount = (float)evaluateExpressionOne(context);
        float percent = (float)evaluateExpressionTwo(context);
        float result = (percent / 100) * amount;
        return result;
    }
    private Object evaluateExpressionOne(Context context){
        if(expression instanceof FloatExpression){
            return ((FloatExpression) expression).evaluate();
        }
        else if(expression instanceof HelperFunctionExpression){
            return ((HelperFunction) expression.evaluate()).run(context);
        }
        return null;
    }
    private Object evaluateExpressionTwo(Context context){
        if(expressionTwo instanceof FloatExpression){
            return ((FloatExpression) expressionTwo).evaluate();
        }
        else if(expressionTwo instanceof HelperFunctionExpression){
            return ((HelperFunction) expressionTwo.evaluate()).run(context);
        }
        return null;
    }
}
