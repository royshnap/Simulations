package logic.rule.helperFunction.impl;

import logic.execution.context.Context;
import logic.execution.instance.property.api.PropertyInstance;
import logic.Expression.api.Expression;
import logic.Expression.impl.PropertyNameExpression;
import logic.rule.helperFunction.api.HelperFunction;
import logic.rule.helperFunction.api.HelperFunctionType;
import logic.world.WorldInstance;

public class EnvironmentFunction extends HelperFunction {

    public EnvironmentFunction(Expression expression) {
        super(HelperFunctionType.ENVIRONMENT,
                expression);
    }

    @Override
    public Object run(Context context) throws IllegalArgumentException{
        if(expression instanceof PropertyNameExpression){
            PropertyInstance envVariable = context.getEnvironmentVariable((((PropertyNameExpression) expression).evaluate()));
            return envVariable.getValue();
        }
        else{
            throw new IllegalArgumentException("For environment function the " +
                    "argument is a name of environment property");
        }
    }
}
