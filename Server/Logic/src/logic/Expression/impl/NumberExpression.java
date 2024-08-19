package logic.Expression.impl;

public abstract class NumberExpression<T> extends FreeValueExpression<T>{
    public NumberExpression(T someFreeExpression) {
        super(someFreeExpression);
    }

    @Override
    public abstract T evaluate();
}
