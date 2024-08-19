package logic.Expression.impl;

import logic.Expression.api.AbstractExpression;
import logic.Expression.api.ExpressionType;

public class PropertyNameExpression extends AbstractExpression<String> {


    public PropertyNameExpression(String propertyName) {
        super(propertyName, ExpressionType.PROPERTY_NAME);

    }

    @Override
    public String evaluate() {
        return expression;
    }

}
