package logic.Expression.impl;

public class StringExpression extends FreeValueExpression<String>{
    public StringExpression(String someFreeExpression) {
        super(someFreeExpression);
    }

    @Override
    public String evaluate() {
        return expression;
    }
}
