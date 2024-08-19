package dto;

public class ActionDTO {
    private final String type;
    private final String entityDefinitionName;
    private final String secondaryEntityDefinitionName;


    private String argument1;
    private String argument2;
    private String operator;
    private String numberOfActions;
    private String elseNumberActions;




    public ActionDTO(String type,String entityDefinitionDTO,
                     String secondaryEntityDefinitionDTO,String argument1, String argument2){
        this.type = type;
        this.entityDefinitionName = entityDefinitionDTO;
        this.secondaryEntityDefinitionName = secondaryEntityDefinitionDTO;
        this.argument1 = argument1;
        this.argument2 = argument2;

    }
    public ActionDTO(String type,String entityDefinitionDTO,
                     String secondaryEntityDefinitionDTO,String argument1, String argument2,
                     String operator, String numberOfActions, String elseNumberActions){
        this.type = type;
        this.entityDefinitionName = entityDefinitionDTO;
        this.secondaryEntityDefinitionName = secondaryEntityDefinitionDTO;
        this.argument1 = argument1;
        this.argument2 = argument2;
        this.operator = operator;
        this.numberOfActions = numberOfActions;
        this.elseNumberActions = elseNumberActions;

    }

    public String getArgument1() {
        return argument1;
    }

    public String getArgument2() {
        return argument2;
    }

    public String getOperator() {
        return operator;
    }

    public String getNumberOfActions() {
        return numberOfActions;
    }

    public String getElseNumberActions() {
        return elseNumberActions;
    }

    public String getSecondaryEntityDefinitionName() {
        return secondaryEntityDefinitionName;
    }


    public String getType() {
        return type;
    }

    public String getEntityDefinitionName() {
        return entityDefinitionName;
    }
}
