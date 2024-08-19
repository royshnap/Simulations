package logic.rule.helperFunction.impl;

import logic.Expression.api.Expression;
import logic.Expression.impl.FloatExpression;
import logic.Expression.impl.IntegerExpression;
import logic.Expression.impl.NumberExpression;
import logic.execution.context.Context;
import logic.rule.helperFunction.api.HelperFunction;
import logic.rule.helperFunction.api.HelperFunctionType;

import java.util.Random;

public class RandomFunction extends HelperFunction {
    public RandomFunction(Expression expression) {
        super(HelperFunctionType.RANDOM,
                expression);
    }

    @Override
    public Object run(Context context) throws IllegalArgumentException{
        if(expression instanceof IntegerExpression){
            Integer exp = (Integer) expression.evaluate();
            Random random = new Random();
            return random.nextInt(exp + 1);
        }
        else if(expression instanceof FloatExpression){
            Float exp = (Float) expression.evaluate();
            Random random = new Random();
            return random.nextFloat() * (exp + 1.0f);

        }
        else{
            throw new IllegalArgumentException("For random function the " +
                    "argument is a number");
        }
    }
}
