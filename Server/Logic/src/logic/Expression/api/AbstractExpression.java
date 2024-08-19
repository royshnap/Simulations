package logic.Expression.api;

import logic.Expression.impl.*;
import logic.definition.entity.api.EntityDefinition;
import logic.rule.helperFunction.api.HelperFunction;
import logic.rule.helperFunction.impl.*;

public abstract class AbstractExpression<T> implements Expression<T> {

    protected T expression;
    protected ExpressionType expressionType;

    public AbstractExpression(T exp, ExpressionType expressionType){
        expression = exp;
        this.expressionType = expressionType;
    }

    @Override
    public ExpressionType getType() {
        return expressionType;
    }


    public static Expression valueExpressionByString(String exp, EntityDefinition entityDefinition){
        if(entityDefinition.getPropertyDefinitionByName(exp) != null){
            return new PropertyNameExpression(exp);
        }
        else if(isHelperFunction(exp) != null){
            return isHelperFunction(exp);
        }
        else{
            if (exp.matches("-?\\d+")) {
                return new IntegerExpression(Integer.parseInt(exp));
            }
            else if (exp.matches("-?\\d+(\\.\\d+)?")) {
                return new FloatExpression(Float.parseFloat(exp));
            }

            else if (exp.matches("true|false")) {
                return new BooleanExpression(Boolean.parseBoolean(exp));
            }
            else {
                return new StringExpression(exp);
            }
        }
    }

    private static HelperFunctionExpression isHelperFunction(String exp){
        if(exp == null){
            System.out.println("");
        }
        else if(exp.contains("percent")){//must be first because of recursive
            int start = exp.indexOf('(') + 1;
            int end = exp.length() - 1;
            String substring = exp.substring(start, end);
            int commaIndex = substring.indexOf(',');
            String exp1 = substring.substring(0, commaIndex).trim();
            String exp2 = substring.substring(commaIndex + 1).trim();
            Expression obj1 = null;
            Expression obj2 = null;
            if (exp1.matches("-?\\d+(\\.\\d+)?")) { //if obj1 is free value
                obj1 = new FloatExpression(Float.parseFloat(exp1));
            }
            else if (exp1.contains("environment") || exp1.contains("random") || exp1.contains("evaluate")|| exp1.contains("percent") || exp1.contains("ticks")) {
                obj1 = isHelperFunction(exp1);
            }
            else{
                throw new IllegalArgumentException("The argument " + exp1 + "is'nt numeric");
            }
            if(exp2.matches("-?\\d+(\\.\\d+)?")){
                obj2 = new FloatExpression(Float.parseFloat(exp2));
            }
           else if (exp2.contains("environment") || exp2.contains("random") || exp2.contains("evaluate")|| exp2.contains("percent") || exp2.contains("ticks")) {
                obj2 = isHelperFunction(exp2);
            }
           else{
                throw new IllegalArgumentException("The argument " + exp2 + "is'nt numeric");
            }
            return new HelperFunctionExpression(new PercentFunction(obj1, obj2));
        }
        else if(exp.contains("environment")){
            int start = exp.indexOf('(') + 1;
            int end = exp.indexOf(')');
            String substring = exp.substring(start, end);
            return new HelperFunctionExpression(new EnvironmentFunction(new PropertyNameExpression(substring)));
        }
        else if(exp.contains("random")){
            int start = exp.indexOf('(') + 1;
            int end = exp.indexOf(')');
            String substring = exp.substring(start, end);
            if(isNumber(substring)){
                return new HelperFunctionExpression(
                        new RandomFunction(new IntegerExpression(Integer.parseInt(substring))));
            }
            else if(isFloat(substring)){
                return new HelperFunctionExpression
                        (new RandomFunction(new FloatExpression(Float.parseFloat(substring))));
            }
        }
        else if(exp.contains("evaluate")){
            int start = exp.indexOf('(') + 1;
            int end = exp.indexOf(')');
            String substring = exp.substring(start, end);
            String[] parts = substring.split("\\.");
            String entityName = parts[0].trim();
            String propertyName = parts[1].trim();
            return new HelperFunctionExpression(
                    new EvaluateFunction(entityName, propertyName));
        }
        else if(exp.contains("ticks")){
            int start = exp.indexOf('(') + 1;
            int end = exp.indexOf(')');
            String substring = exp.substring(start, end);
            String[] parts = substring.split("\\.");
            String entityName = parts[0].trim();
            String propertyName = parts[1].trim();
            return new HelperFunctionExpression(new TicksFunction(entityName, propertyName));
        }
        return null;
    }

    public static boolean isFloat(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean isNumber(String s) {
       return s.matches("-?\\d+");
    }




}
