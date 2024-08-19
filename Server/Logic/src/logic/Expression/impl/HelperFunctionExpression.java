package logic.Expression.impl;

import logic.Expression.api.AbstractExpression;
import logic.Expression.api.ExpressionType;
import logic.rule.helperFunction.api.HelperFunction;

public class HelperFunctionExpression extends AbstractExpression<HelperFunction> {


    public HelperFunctionExpression(HelperFunction helperFunction){
        super(helperFunction, ExpressionType.HELPER_FUNCTION);
    }

    @Override
    public HelperFunction evaluate() {
        return expression;
    }
}
