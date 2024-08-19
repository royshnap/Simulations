package logic.rule.action.impl.condition;

public enum Operation {
    EQUAL("equals", "="),
    NOTEQUAL("not equals", "!="),
    BT("bigger than", ">"),
    LT("less than", "<");

    private String name;
    private String symbol;

    Operation(String name, String sym){
        this.name = name;
        this.symbol = sym;

    }

    public static Operation evaluate(String op){
        switch (op){
            case  "=":
                return Operation.EQUAL;
            case "!=":
                return Operation.NOTEQUAL;
            case "bt":
                return Operation.BT;
            case "lt":
                return Operation.LT;
            default:
                return  null;
        }

    }

    @Override
    public String toString() {
        return name;
    }
}
