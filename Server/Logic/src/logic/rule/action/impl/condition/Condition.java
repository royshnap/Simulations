package logic.rule.action.impl.condition;

import logic.execution.context.Context;

public interface Condition {
    boolean evaluate(Context context);
}
