package logic.Expression.api;

import logic.definition.entity.api.EntityDefinition;

public interface Expression <T>{
    T evaluate();

    ExpressionType getType();



}
