package logic.rule.action.impl.condition;

public enum Logical {
    OR("or", "||"),
    AND("and", "&&");

    private String name;
    private String symbol;

    Logical(String name, String sym){
        this.name = name;
        this.symbol = sym;

    }

    public static Logical evaluate(String op){
        switch (op){
            case  "or":
                return Logical.OR;
            case "and":
                return Logical.AND;
            default:
                throw new IllegalArgumentException("the logical "+ op + "isn't 'or'/'and'");
        }

    }

    @Override
    public String toString() {
        return name;
    }

}
