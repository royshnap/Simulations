package logic.rule.action.impl.condition;

import logic.execution.context.Context;

import java.util.ArrayList;
import java.util.List;

public class MultipleCondition implements Condition {
    private Logical logical;
    private List<Condition> conditions;

    public Logical getLogical() {
        return logical;
    }

    public MultipleCondition(Logical logical, List<Condition> conditions) {
        this.conditions = conditions;
        this.logical = logical;
    }

    public void addCondition(Condition condition){
        conditions.add(condition);
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    @Override
    public boolean evaluate(Context context) {
        if(logical.toString().equals("or")){
            for(Condition condition : conditions){
                if(condition.evaluate(context)){
                    return true;
                }
            }
            return false;
        } else if (logical.toString().equals("and")) {
            for(Condition condition : conditions){
                if(!condition.evaluate(context)){
                    return false;
                }
            }
            return true;
            }


        throw new IllegalArgumentException("the logical " + logical.toString() + " isn't 'and'/'or'");

    }
}
