package logic.Expression.impl;

import logic.Expression.api.AbstractExpression;
import logic.Expression.api.ExpressionType;

public abstract class FreeValueExpression<T> extends AbstractExpression<T> {


    public FreeValueExpression(T someFreeExpression) {
        super(someFreeExpression, ExpressionType.FREE_VALUE);
    }


}
