package logic.rule.action.api;

public enum ActionType {
    INCREASE, DECREASE,SET,KILL,CALCULATION,CONDITION,
    DIVIDE, MULTIPLY, PROXIMITY, REPLACE;

    public static ActionType getActionType(String type){

        switch (type){
            case "increase":
                return INCREASE;
            case "decrease":
                return DECREASE;
            case "set":
                return SET;
            case "kill":
                return KILL;
            case "calculation":
                return CALCULATION;
            case "condition":
                return CONDITION;
            case "proximity":
                return PROXIMITY;
            case "replace":
                return REPLACE;
        }
        throw new IllegalArgumentException("The type " + type + " is not exists");
    }
}
