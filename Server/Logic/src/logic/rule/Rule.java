package logic.rule;
import logic.rule.action.api.Action;

import java.util.List;

public interface Rule {
    String getName();
    Activation getActivation();
    List<Action> getActionsToPerform();
    void addAction(Action action);
    boolean equals(Object o);
    int hashCode();
}
