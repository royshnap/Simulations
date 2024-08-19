package logic.Expression.impl;

public class FloatExpression extends NumberExpression<Float>{
    public FloatExpression(Float someFreeExpression) {
        super(someFreeExpression);
    }

    @Override
    public Float evaluate() {
        return expression;
    }
}
