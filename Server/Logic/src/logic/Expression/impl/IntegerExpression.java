package logic.Expression.impl;

public class IntegerExpression extends NumberExpression<Integer>{
    public IntegerExpression(Integer someFreeExpression) {
        super(someFreeExpression);
    }

    @Override
    public Integer evaluate() {
        return expression;
    }
}
