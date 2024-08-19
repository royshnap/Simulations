package logic.rule;

import logic.rule.action.api.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RuleImpl implements Rule{

    private String name;
    private List<Action> actions;
    private Activation activation;

    public RuleImpl(String name, int ticks, double prob){
        boolean containsSpaces = name.contains(" ");
        this.name = name;

        actions = new ArrayList<>();
        activation = new Activation(ticks,prob);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleImpl rule = (RuleImpl) o;
        return Objects.equals(name, rule.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String getName(){
      return name;
    }

    @Override
    public Activation getActivation(){
        return activation;

    }

    @Override
    public List<Action> getActionsToPerform(){
        return actions;

    }
    @Override
    public void addAction(Action action){
        actions.add(action);

    }


}
