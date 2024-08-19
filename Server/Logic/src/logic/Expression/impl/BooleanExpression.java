package logic.Expression.impl;

public class BooleanExpression extends FreeValueExpression<Boolean>{

    public BooleanExpression(Boolean someFreeExpression) {
        super(someFreeExpression);
    }

    @Override
    public Boolean evaluate() {
        return expression;
    }
}
