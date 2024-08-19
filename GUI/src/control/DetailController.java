package control;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import logic.dto.*;

import java.util.List;

public class DetailController {

    private WorldDefinitionDTO worldDefinitionDTO;
    @FXML
    private TreeView<String> treeOfDetails;
   
    @FXML
    private TreeView<String> rightTree;

    public void setWorldDefinitionDTO(WorldDefinitionDTO worldDefinitionDTO) {
        this.worldDefinitionDTO = worldDefinitionDTO;
        showDetails();

    }

    public void showDetails() {
        TreeItem<String> root = treeOfDetails.getRoot();
        if (root == null) {
            treeOfDetails.setRoot(new TreeItem<>("World details"));
            treeOfDetails.getRoot().getChildren().add(new TreeItem<>("grid size: row: "
            + worldDefinitionDTO.getRows() + " col: " + worldDefinitionDTO.getCols()));

        } else {
            root.getChildren().clear();
        }
        root = treeOfDetails.getRoot();
        root.getChildren().add(new TreeItem<>("Environment Variables"));
        for (PropertyDefinitionDTO envVariable : worldDefinitionDTO.getEnvironmentsVariablesDTO().getDefinitionDTOS()) {
            createButtonForDetails(envVariable, root.getChildren().get(1));
        }
        root.getChildren().add(new TreeItem<>("Entities"));
        for (EntityDefinitionDTO entityDefinitionDTO : worldDefinitionDTO.getEntityDefinitionDTOS()) {
            createButtonForDetails(entityDefinitionDTO, root.getChildren().get(2));
        }
        root.getChildren().add(new TreeItem<>("Rules"));
        for (RuleDTO rule : worldDefinitionDTO.getRules()) {
            createButtonForDetails(rule, root.getChildren().get(3));
        }

        root.getChildren().add(new TreeItem<>("Termination Conditions"));
        for (TerminateConditionDTO terminateConditionDTO : worldDefinitionDTO.getTerminateConditions()) {
            createButtonForDetails(terminateConditionDTO, root.getChildren().get(4));
        }

    }

    private void createButtonForDetails(Object object, TreeItem<String> root) {
        TreeItem<String> item = new TreeItem<>("");

        Button button = new Button(getName(object));
        button.setOnAction(event -> {
            if (object instanceof EntityDefinitionDTO) {
                showEntities((EntityDefinitionDTO) object);
            } else if (object instanceof RuleDTO) {
                showRules((RuleDTO) object);
            } else if (object instanceof PropertyDefinitionDTO) {
                showEnvVariable((PropertyDefinitionDTO) object);
            } else if (object instanceof TerminateConditionDTO) {
                showTermination((TerminateConditionDTO) object);
            }
        });
        item.setGraphic(button);
        root.getChildren().add(item);
    }

    private String getName(Object object) {
        if (object instanceof EntityDefinitionDTO) {
            return ((EntityDefinitionDTO) object).getName();
        } else if (object instanceof RuleDTO) {
            return ((RuleDTO) object).getName();
        } else if (object instanceof PropertyDefinitionDTO) {
            return ((PropertyDefinitionDTO) object).getName();
        } else if (object instanceof TerminateConditionDTO) {
            return "Terminate Condition";
        }
        return "";
    }

    private void showTermination(TerminateConditionDTO object) {
        TreeItem<String> root = rightTree.getRoot();
        rightTree.setRoot(new TreeItem<>("Termination Conditions:"));
        if (root != null) {
            root.getChildren().clear();
        }
        root = rightTree.getRoot();
        if (object.getTicks() != null) {
            root.getChildren().add(new TreeItem<>("Ticks: " + object.getTicks()));
        }
        if (object.getSeconds() != null) {
            root.getChildren().add(new TreeItem<>("Seconds: " + object.getSeconds()));
        }
        if(object.getTicks() == null && object.getSeconds() == null){
            root.getChildren().add(new TreeItem<>("End by user"));
        }

    }

    private void showEnvVariable(PropertyDefinitionDTO envVariable) {
        TreeItem<String> root = rightTree.getRoot();
        rightTree.setRoot(new TreeItem<>(envVariable.getName()+" Details:"));
        if (root != null) {
            root.getChildren().clear();
        }
        root = rightTree.getRoot();
        root.getChildren().add(new TreeItem<>("Name: " + envVariable.getName()));
        root.getChildren().add(new TreeItem<>("Type: " + envVariable.getType()));
        if (envVariable.getFrom() != null) {
            root.getChildren().add(new TreeItem<>("Range:"));
            root.getChildren().add(new TreeItem<>("From: " + envVariable.getFrom()));
            root.getChildren().add(new TreeItem<>("To: " + envVariable.getTo()));
        }
    }

    private void showRules(RuleDTO rule) {
        TreeItem<String> root = rightTree.getRoot();
        rightTree.setRoot(new TreeItem<>(rule.getName()+" Details:"));
        if (root != null) {
            root.getChildren().clear();
        }
        root = rightTree.getRoot();
        root.getChildren().add(new TreeItem<>("Name: " + rule.getName()));
        root.getChildren().add(new TreeItem<>("Activation Condition: "));
        root.getChildren().get(1).getChildren().add(new TreeItem<>("Ticks: " + rule.getActivation().getTicks()));
        root.getChildren().get(1).getChildren().add(new TreeItem<>("Probability: " + rule.getActivation().getProbability()));
        root.getChildren().add(new TreeItem<>("Number of Actions: " + rule.getActions().size()));
        root.getChildren().add(new TreeItem<>("Actions: ")); //3
        int  i =0;
        for (ActionDTO actionDTO : rule.getActions()) {
            showActions(root, actionDTO,i);
            i++;

        }
    }
    private void showActions(TreeItem<String> root, ActionDTO actionDTO, int i) {
        switch (actionDTO.getType()){
            case "DECREASE":
                if(actionDTO.getSecondaryEntityDefinitionName()!=null){
                    root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                    root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                            "Main entity " + actionDTO.getEntityDefinitionName() +
                                    "\nSecondary entity " + actionDTO.getSecondaryEntityDefinitionName()
                                    + "\nArgument 1: " + actionDTO.getArgument1() + " Argument 2: " + actionDTO.getArgument2()));


                }
                else{
                    root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                    root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                            "Main entity " + actionDTO.getEntityDefinitionName() +
                                     "\nArgument 1: " + actionDTO.getArgument1() + " Argument 2: " + actionDTO.getArgument2()));
                }
                break;
            case "INCREASE":
                if(actionDTO.getSecondaryEntityDefinitionName()!=null){
                    root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                    root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                            "Main entity " + actionDTO.getEntityDefinitionName() +
                                    "\nSecondary entity " + actionDTO.getSecondaryEntityDefinitionName()
                                    + "\nArgument 1: " + actionDTO.getArgument1() + " Argument 2: " + actionDTO.getArgument2()));


                }
                else{
                    root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                    root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                            "Main entity " + actionDTO.getEntityDefinitionName() +
                                    "\nArgument 1: " + actionDTO.getArgument1() + " Argument 2: " + actionDTO.getArgument2()));
                }
                break;
            case "KILL":
                if(actionDTO.getSecondaryEntityDefinitionName()!=null){
                    root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                    root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                            "\nMain entity " + actionDTO.getEntityDefinitionName() +
                                    "\nSecondary entity " + actionDTO.getSecondaryEntityDefinitionName()));

                }
                else{
                        root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                        root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                                "\nMain entity " + actionDTO.getEntityDefinitionName()));


                }
                break;
            case "PROXIMITY":
                root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                        "\nSource entity " + actionDTO.getEntityDefinitionName() +
                                "\nTarget entity " + actionDTO.getSecondaryEntityDefinitionName() +
                                "\nDepth " + actionDTO.getArgument1() +
                                "\nNumber of actions to perform " + actionDTO.getArgument2()));


                break;
            case "REPLACE":
                root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                        "\nKill entity " + actionDTO.getEntityDefinitionName() +
                                "\nCreate entity " + actionDTO.getSecondaryEntityDefinitionName()));

                break;
            case "SET":
                if(actionDTO.getSecondaryEntityDefinitionName()!=null){
                    root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                    root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                            "\nMain entity " + actionDTO.getEntityDefinitionName() +
                                    "\nSecondary entity " + actionDTO.getSecondaryEntityDefinitionName()
                                    + "\nProperty: " + actionDTO.getArgument1() + " New value: " + actionDTO.getArgument2()));


                }
                else{
                    root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                    root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                            "\nMain entity " + actionDTO.getEntityDefinitionName()
                                    + "\nProperty : " + actionDTO.getArgument1() + " New value: " + actionDTO.getArgument2()));


                }
                break;
            case "MULTIPLY":
                if(actionDTO.getSecondaryEntityDefinitionName()!=null){
                    root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                    root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                            "\nMain entity " + actionDTO.getEntityDefinitionName() +
                                    "\nSecondary entity " + actionDTO.getSecondaryEntityDefinitionName()
                                    + "\nArgument 1: " + actionDTO.getArgument1() + " Argument 2: " + actionDTO.getArgument2()));


                }
                else{
                    root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                    root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                            "\nMain entity " + actionDTO.getEntityDefinitionName()
                                    + "\nArgument 1: " + actionDTO.getArgument1() + " Argument 2: " + actionDTO.getArgument2()));

                }
                break;
            case "DIVIDE":
                if(actionDTO.getSecondaryEntityDefinitionName()!=null){
                    root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                    root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                            "\nMain entity " + actionDTO.getEntityDefinitionName() +
                                    "\nSecondary entity " + actionDTO.getSecondaryEntityDefinitionName()
                                    + "\nArgument 1: " + actionDTO.getArgument1() + " Argument 2: " + actionDTO.getArgument2()));

                }
                else{
                    root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                    root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                            "\nMain entity " + actionDTO.getEntityDefinitionName()
                                    + "\nArgument 1: " + actionDTO.getArgument1() + " Argument 2: " + actionDTO.getArgument2()));


                }
                break;
            case "CONDITION":
                if(actionDTO.getSecondaryEntityDefinitionName()!=null){
                    if(actionDTO.getArgument2()!=null){
                        root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                        root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                                "\nMain entity " + actionDTO.getEntityDefinitionName() +
                                        "\nSecondary entity " + actionDTO.getSecondaryEntityDefinitionName()
                                        + "\nArgument 1: " + actionDTO.getArgument1() + " Argument 2: " + actionDTO.getArgument2()
                                        + "\nOperator : " + actionDTO.getOperator() +
                                        "\nNumber of actions in then " +
                                        actionDTO.getNumberOfActions() + "\nNumber of actions in else " +
                                        actionDTO.getElseNumberActions()));


                    }
                    else{
                        root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                        root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                                "\nMain entity " + actionDTO.getEntityDefinitionName() +
                                        "\nSecondary entity " + actionDTO.getSecondaryEntityDefinitionName()
                                        + "\nLogical : " + actionDTO.getOperator() +
                                        "\nNumber of conditions " + actionDTO.getArgument1() +
                                        "\nNumber of actions in then " +
                                        actionDTO.getNumberOfActions() + "\nNumber of actions in else " +
                                        actionDTO.getElseNumberActions()));


                    }

                }
                else{
                    if(actionDTO.getArgument2()!=null){
                        root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                        root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                                "\nMain entity " + actionDTO.getEntityDefinitionName() +
                                        "\nArgument 1: " + actionDTO.getArgument1() + " Argument 2: " + actionDTO.getArgument2()
                                        + "\nOperator : " + actionDTO.getOperator() +
                                        "\nNumber of actions in then " +
                                        actionDTO.getNumberOfActions() + "\nNumber of actions in else " +
                                        actionDTO.getElseNumberActions()));


                    }
                    else{
                        root.getChildren().get(3).getChildren().add(new TreeItem<>(actionDTO.getType()));
                        root.getChildren().get(3).getChildren().get(i).getChildren().add(new TreeItem<>(
                                "\nMain entity " + actionDTO.getEntityDefinitionName() +
                                        "\nLogical : " + actionDTO.getOperator() +
                                        "\nNumber of conditions " + actionDTO.getArgument1() +
                                        "\nNumber of actions in then " +
                                        actionDTO.getNumberOfActions() + "\nNumber of actions in else " +
                                        actionDTO.getElseNumberActions()));

                    }
                }
                break;







        }
    }

    private void showEntities(EntityDefinitionDTO entityDefinitionDTO) {
        TreeItem<String> root = rightTree.getRoot();
        rightTree.setRoot(new TreeItem<>(entityDefinitionDTO.getName()+" Details:"));
        if (root != null) {
            root.getChildren().clear();
        }
        root = rightTree.getRoot();
        root.getChildren().add(new TreeItem<>("Name: " + entityDefinitionDTO.getName()));
        root.getChildren().add(new TreeItem<>("Properties: "));
        List<PropertyDefinitionDTO> properties = entityDefinitionDTO.getProperties();
        int i = 0;
        for (PropertyDefinitionDTO propertyDefinitionDTO : properties) {

            root.getChildren().get(1).getChildren().add(new TreeItem<>(propertyDefinitionDTO.getName()));
            root.getChildren().get(1).getChildren().get(i).getChildren().add(new TreeItem<>("Type: " + propertyDefinitionDTO.getType()));
            if (propertyDefinitionDTO.getFrom() != null) {
                root.getChildren().get(1).getChildren().get(i).getChildren().add(new TreeItem<>("Range:"));
                root.getChildren().get(1).getChildren().get(i).getChildren().add(new TreeItem<>("From: " + propertyDefinitionDTO.getFrom()));
                root.getChildren().get(1).getChildren().get(i).getChildren().add(new TreeItem<>("To: " + propertyDefinitionDTO.getTo()));
            }
            root.getChildren().get(1).getChildren().get(i).getChildren().add(new TreeItem<>("Fixed or Random: " + propertyDefinitionDTO.getValueGenerator()));
            i++;

        }

    }

    public void initialize() {


    }
}




