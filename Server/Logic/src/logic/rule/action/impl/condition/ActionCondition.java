package logic.rule.action.impl.condition;

import logic.definition.context.ContextDefinition;
import logic.definition.entity.api.EntityDefinition;
import logic.execution.context.Context;
import logic.rule.action.api.AbstractAction;
import logic.rule.action.api.Action;
import logic.rule.action.api.ActionType;

import java.util.ArrayList;
import java.util.List;

public class ActionCondition extends AbstractAction {

    private Condition condition;
    private List<Action> listThen = new ArrayList<>();
    private List<Action> listElse = new ArrayList<>();

    public Condition getCondition() {
        return condition;
    }

    public List<Action> getListElse() {
        return listElse;
    }

    public List<Action> getListThen() {
        return listThen;
    }

    public ActionCondition(ContextDefinition contextDefinition, Condition condition, List<Action> then,
                           List<Action> listElse) {
        super(ActionType.CONDITION, contextDefinition);
        this.condition = condition;
        this.listThen = then;
        this.listElse = listElse;
    }

    public void setCondition(Condition condition){
        this.condition = condition;
    }

    public void addThenAction(Action action){
        listThen.add(action);
    }

    public void addElseAction(Action action){
        listElse.add(action);
    }

    @Override
    public void invoke(Context context) {

        if(condition.evaluate(context)){
            for(Action action : listThen){

                    action.invoke(context);

            }
        }
        else{
            if(listElse != null){
                for(Action action : listElse){

                        action.invoke(context);

                }
            }
        }

    }
}
