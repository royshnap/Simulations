package control;

import facade.Facade;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import logic.definition.entity.api.EntityDefinition;
import logic.definition.entity.impl.EntityDefinitionImpl;
import logic.definition.property.api.PropertyDefinition;
import logic.definition.property.api.PropertyType;
import logic.definition.property.impl.BooleanPropertyDefinition;
import logic.definition.property.impl.FloatPropertyDefinition;
import logic.definition.property.impl.StringPropertyDefinition;
import logic.dto.*;
import logic.simulation.Simulation;
import logic.world.WorldDefinition;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewExecutionController {
    @FXML
   private HBox hboxScene;

    @FXML
    private Button startSimulationButton;

    @FXML
    private VBox environmentVbox;
    @FXML
    private VBox entitiesVbox;
    private WorldDefinitionDTO worldDefinitionDTO;
    private WorldDefinition worldDefinition;
    private Facade facade;
    private ActiveEnvironmentDTO environmentVariablesManager;
    List<PropertyInstanceDTO> propertyInstanceDTOS = new ArrayList<>();
    private AtomicBoolean shouldContinue = new AtomicBoolean(true);
    private Thread histogramThread;
    private SimulationHistory simulationHistory;
    private boolean isFromResultScene;
    @FXML
    private Button clearButton;

    private MainController mainController;
    private Simulation simulationForHistory;

    public  void setSimulationForHistory(Simulation simulationEnding) {
        this.simulationForHistory =simulationEnding;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    public void setIsFromResultScene(boolean isFromResultScene) {
        this.isFromResultScene = isFromResultScene;
    }


    public void setWorldDefinitionDTO(WorldDefinitionDTO worldDefinitionDTO,
                                      WorldDefinition worldDefinition) {
        this.worldDefinitionDTO = worldDefinitionDTO;
        this.worldDefinition = worldDefinition;
        if(isFromResultScene == true){
            clearButton.setVisible(false);
        }
        displayEnvVariables();
        displayEntities();


    }
    private void createHistory(){
        List<EntityDefinition> population = new ArrayList<>();
        EntityDefinition newEntityDefinition;
        PropertyDefinition newPropertyDefinition = null;
        for(EntityDefinition entityDefinition : worldDefinition.getPopulation()){
            newEntityDefinition = new EntityDefinitionImpl(entityDefinition.getName());
            newEntityDefinition.setStartPopulation(entityDefinition.getStartPopulation());
            newEntityDefinition.setEndPopulation(entityDefinition.getEndPopulation());
            for(PropertyDefinition propertyDefinition : entityDefinition.getProps()){
                if(propertyDefinition instanceof BooleanPropertyDefinition){
                    newPropertyDefinition = new BooleanPropertyDefinition(propertyDefinition.getName(),
                            propertyDefinition.getType(), propertyDefinition.getValueGenerator());
                }
                else if(propertyDefinition instanceof StringPropertyDefinition){
                    newPropertyDefinition = new StringPropertyDefinition(propertyDefinition.getName(),
                            propertyDefinition.getType(), propertyDefinition.getValueGenerator());
                }
                else if(propertyDefinition instanceof FloatPropertyDefinition){
                    newPropertyDefinition = new FloatPropertyDefinition(propertyDefinition.getName(),
                            propertyDefinition.getType(), propertyDefinition.getValueGenerator(),
                            ((FloatPropertyDefinition)propertyDefinition).getFrom(), ((FloatPropertyDefinition)propertyDefinition).getTo());
                }

                newEntityDefinition.addProperty(newPropertyDefinition);
            }
            population.add(newEntityDefinition);
        }
        simulationHistory = new SimulationHistory(environmentVariablesManager,propertyInstanceDTOS, population);
        facade.setSimulationHistory(simulationHistory);
    }

    public void setHboxScene(HBox hboxScene) {
        this.hboxScene = hboxScene;
    }

    public void setFacade(Facade facade) {
        this.facade = facade;
    }

    public void initialize(){


    }

    private void displayEntities() {

        int rowIndex = 0;
        if(isFromResultScene == false){
            for(EntityDefinition entityDefinitionDTO : worldDefinition.getPopulation()){
                GridPane gridPane = new GridPane();
                gridPane.setVgap(10);
                Label labelEntity = new Label(entityDefinitionDTO.getName()+":\n");
                Button buttonEntity = new Button("Enter population size");

                //HBox entityBox = new HBox(10, labelEntity, buttonEntity, userChoiceTextField);
                buttonEntity.setOnAction(event -> {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Enter Value");
                    dialog.setHeaderText("Please enter a DECIMAL number in the range");
                    dialog.setContentText("Value:");

                    dialog.showAndWait().ifPresent(userChoice -> {
                        Integer choice = processUserChoiceForEntity(userChoice);
                        if(choice!=null){
                            entityDefinitionDTO.setStartPopulation(choice);
                            entityDefinitionDTO.setEndPopulation(choice);
                        }

                    });
                });

                gridPane.addRow(rowIndex, labelEntity,buttonEntity);
                rowIndex++;
                entitiesVbox.getChildren().add(gridPane);
            }
            Label labelFinish = new Label("Please press finish if you are done");
            Label labelPopulation = new Label("to put population size in the entities");
            Button finishButton = new Button("finish");
            entitiesVbox.getChildren().addAll(labelFinish,labelPopulation,finishButton);
            finishButton.setOnAction(event -> {shouldContinue.set(false);
                createHistory();});

        }
        else{
            for(EntityDefinition entityDefinition : facade.getSimulationHistory(simulationForHistory.getId()).getEntityDefinitionsDTOS()){
                GridPane gridPane = new GridPane();
                gridPane.setVgap(10);
                Label labelEntity = new Label(entityDefinition.getName()+":\n");
                Label labelPopulation = new Label("Population size: "+entityDefinition.getStartPopulation());
                gridPane.addRow(rowIndex, labelEntity,labelPopulation);
                rowIndex++;
                entitiesVbox.getChildren().add(gridPane);
            }
        }


    }

    public SimulationHistory getSimulationHistory() {
        return simulationHistory;
    }

    @FXML
    public void loadStartSimulation() throws IOException {
        if(isFromResultScene == false){
            if(environmentVariablesManager != null && !shouldContinue.get()){
                histogramThread = new Thread(() -> {
                    facade.startSimulation(environmentVariablesManager);

                    Platform.runLater(() -> {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/ResultScene.fxml"));
                        Parent resultSceneRoot = null;
                        try {
                            resultSceneRoot = fxmlLoader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        ResultController resultController = fxmlLoader.getController();

                        resultController.setHistogramThread(histogramThread);
                        resultController.setFacade(facade);
                        resultController.setSimulationManagerDTO(facade.getSimulationManagerDTO());
                        resultController.setHboxScene(hboxScene);
                        resultController.setMainController(mainController);


                        hboxScene.getChildren().clear();
                        hboxScene.getChildren().add(resultSceneRoot);
                    });
                });
                histogramThread.start();
            }
        }
        else{
            histogramThread = new Thread(() -> {
                try {
                    facade.startSimulationInHistory(facade.getSimulationHistory(simulationForHistory.getId()).getActiveEnvironmentDTO(),
                            facade.getSimulationHistory(simulationForHistory.getId()).getEntityDefinitionsDTOS());
                } catch (JAXBException e) {
                    throw new RuntimeException(e);
                }

                Platform.runLater(() -> {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/ResultScene.fxml"));
                    Parent resultSceneRoot = null;
                    try {
                        resultSceneRoot = fxmlLoader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    ResultController resultController = fxmlLoader.getController();

                    resultController.setHistogramThread(histogramThread);
                    resultController.setFacade(facade);
                    resultController.setSimulationManagerDTO(facade.getSimulationManagerDTO());
                    resultController.setHboxScene(hboxScene);
                    resultController.setMainController(mainController);


                    hboxScene.getChildren().clear();
                    hboxScene.getChildren().add(resultSceneRoot);
                });
            });
            histogramThread.start();
        }


    }
    @FXML
    public void loadClear(){
        propertyInstanceDTOS = new ArrayList<>();
        for(EntityDefinition entityDefinition : worldDefinition.getPopulation()){
            entityDefinition.setStartPopulation(0);
            entityDefinition.setEndPopulation(0);
        }

    }
    private void displayEnvVariables() {
        environmentVbox.getChildren().clear(); // Clear previous content
        propertyInstanceDTOS = new ArrayList<>();
        int i = 1;
        if(isFromResultScene == false){
            for (PropertyDefinitionDTO envVariable : worldDefinitionDTO.getEnvironmentsVariablesDTO().getDefinitionDTOS()) {
                String from = null;
                String to = null;
                if (envVariable.getFrom() != null) {
                    from = envVariable.getFrom();
                    to = envVariable.getTo();
                }
                Label labelNameEnvVariable = new Label(i + ". " + "name: " + envVariable.getName() + " ");
                Button buttonEnvVariable = new Button("Enter value if you want to");
                Label labelTypeEnvVariable = new Label(" type: " + envVariable.getType());
                Label labelRangeEnvVariable = null;
                if (envVariable.getType().equals("DECIMAL") || envVariable.getType().equals("FLOAT")) {
                    labelRangeEnvVariable = new Label("range " + from + "-" + to);
                }
                if (labelRangeEnvVariable != null) {
                    HBox hBoxDetails = new HBox(10, labelNameEnvVariable, labelTypeEnvVariable);
                    HBox hbox = new HBox(buttonEnvVariable);
                    environmentVbox.getChildren().addAll(hBoxDetails, labelRangeEnvVariable, hbox);
                } else {
                    HBox hBoxDetails = new HBox(10, labelNameEnvVariable,
                            labelTypeEnvVariable, buttonEnvVariable);
                    HBox hbox = new HBox(buttonEnvVariable);
                    environmentVbox.getChildren().addAll(hBoxDetails, hbox);
                }
                buttonEnvVariable.setOnAction(event -> {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Enter Value");
                    dialog.setHeaderText("Please enter a value");
                    dialog.setContentText("Value:");

                    dialog.showAndWait().ifPresent(userChoice -> {
                        Object choice = processUserChoiceForEnv(envVariable, userChoice);
                        if(choice!=null){
                            PropertyInstanceDTO propertyInstance = facade.createPropertyInstance(envVariable, choice);
                            propertyInstanceDTOS.add(propertyInstance);
                        }

                    });
                });
                i++;

            }
            Label labelFinish = new Label("Please press finish if you are done to put values");
            Label label = new Label("in the environment variables");
            Button finishButton = new Button("finish");
            finishButton.setOnAction(event -> {
                for (PropertyDefinitionDTO propertyDefinitionDTO : worldDefinitionDTO.getEnvironmentsVariablesDTO().getDefinitionDTOS()) {
                    boolean isFound = false;
                    for (PropertyInstanceDTO propertyInstanceDTO : propertyInstanceDTOS) {
                        if (propertyDefinitionDTO.getName().equals(propertyInstanceDTO.getPropertyDefinition().getName())) {
                            isFound = true;
                            break;
                        }
                    }
                    if (!isFound) {
                        PropertyInstanceDTO propertyInstance = facade.createPropertyInstance(propertyDefinitionDTO, null);
                        propertyInstanceDTOS.add(propertyInstance);
                    }
                }
                environmentVariablesManager = new ActiveEnvironmentDTO(propertyInstanceDTOS);
                createHistory();
            });

            environmentVbox.getChildren().addAll(labelFinish, label, finishButton);

        }
        else{
             i = 1;
            for(PropertyInstanceDTO envVariable : facade.getSimulationHistory(simulationForHistory.getId()).getPropertyInstanceDTOS()){
                String from = null;
                String to = null;
                if (envVariable.getPropertyDefinition().getFrom() != null) {
                    from = envVariable.getPropertyDefinition().getFrom();
                    to = envVariable.getPropertyDefinition().getTo();
                }
                Label labelNameEnvVariable = new Label(i + ". " + "name: " + envVariable.getPropertyDefinition().getName() + " ");
                Label labelTypeEnvVariable = new Label(" type: " + envVariable.getPropertyDefinition().getType());
                Label labelRangeEnvVariable = null;
                Label labelValueEnvVariable = new Label("value: " + envVariable.getValue());
                if (envVariable.getPropertyDefinition().getType().equals("DECIMAL") || envVariable.getPropertyDefinition().getType().equals("FLOAT")) {
                    labelRangeEnvVariable = new Label("range " + from + "-" + to);
                }
                if (labelRangeEnvVariable != null) {
                    HBox hBoxDetails = new HBox(10, labelNameEnvVariable, labelTypeEnvVariable, labelValueEnvVariable);
                    environmentVbox.getChildren().addAll(hBoxDetails, labelRangeEnvVariable);
                } else {
                    HBox hBoxDetails = new HBox(10, labelNameEnvVariable,
                            labelTypeEnvVariable, labelValueEnvVariable);
                    environmentVbox.getChildren().addAll(hBoxDetails);
                }
                i++;
            }
        }

    }
    private Object processUserChoiceForEnv(PropertyDefinitionDTO chosenEnvVariable,String userInput) {
        if (chosenEnvVariable.getType().equals(PropertyType.DECIMAL.name())) {
            Object from = null;
            Object to = null;
            if(chosenEnvVariable.getFrom() != null){
                from = chosenEnvVariable.getFrom();
                to = chosenEnvVariable.getTo();
            }
            if (!integerCheck(userInput) || !integerInRange(userInput, from, to)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The value you entered is incorrect. Please enter a DECIMAL number in the range " + from + " to " + to + ":");
                alert.showAndWait();
                return null;

            }

            return Float.parseFloat(userInput);

        } else if (chosenEnvVariable.getType().equals(PropertyType.FLOAT.name())) {
            Object from = null;
            Object to = null;
            if(chosenEnvVariable.getFrom() != null){
                from = chosenEnvVariable.getFrom();
                to = chosenEnvVariable.getTo();
            }
            if (!floatCheck(userInput) || !floatInRange(userInput, from, to)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The value you entered is incorrect. Please enter a FLOAT number in the range " + from + " to " + to + ":");
                alert.showAndWait();
                return null;
            }
            return Float.parseFloat(userInput);

        } else if (chosenEnvVariable.getType().equals(PropertyType.BOOLEAN.name())) {
            if(!(userInput.equalsIgnoreCase("true") || userInput.equalsIgnoreCase("false"))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The value you entered should be only true or false");
                alert.showAndWait();
                return null;

            }
            return Boolean.getBoolean(userInput.toLowerCase());

        } else if (chosenEnvVariable.getType().equals(PropertyType.STRING.name())) {
           return userInput;

        }
        return null;

    }

    private static boolean integerCheck(String input) {
        try {
            float integerValue = Integer.parseInt(input);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean floatCheck(String input) {
        try {
            float floatValue = Float.parseFloat(input);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean integerInRange(String input, Object from, Object to) {
        try {
            int value = Integer.parseInt(input);
            if(from  instanceof Integer){
                return value >= (Integer)  from && value <= (Integer) to;
            }
            else {
                return value >= Integer.parseInt((String) from) &&
                        value <=Integer.parseInt((String) to);
            }

        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean floatInRange(String input, Object from, Object to) {
        try {
            float value = Float.parseFloat(input);
            if(from  instanceof Float){
                return value >= (Float) from && value <= (Float) to;
            }
            else {
                return value >= Float.parseFloat((String) from) &&
                        value <=Float.parseFloat((String) to);
            }

        } catch (NumberFormatException e) {
            return false;
        }
    }
    private Integer processUserChoiceForEntity(String choice) {
        if (!integerCheck(choice)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("The value you entered is incorrect. Please enter a DECIMAL number that lower than " +
                    + (worldDefinition.getRows() * worldDefinition.getColumns()) + " (amount of world):");
            alert.showAndWait();
            return null;

        }
        else{
            int numberPopulation = Integer.parseInt(choice);
            int sumOfPopulation = 0;
            for(EntityDefinition entityDefinition : worldDefinition.getPopulation()){
                sumOfPopulation += entityDefinition.getStartPopulation();
            }
            if((worldDefinition.getRows() * worldDefinition.getColumns()) < sumOfPopulation+numberPopulation){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The sum of all population after entering this value is bigger than world amount (" +
                        + (worldDefinition.getRows() * worldDefinition.getColumns()) + ")" + " Please enter different value");
                alert.showAndWait();
                return null;
            }
            if((worldDefinition.getRows() * worldDefinition.getColumns())< numberPopulation){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The value you entered is incorrect. Please enter a DECIMAL number that lower than " +
                + (worldDefinition.getRows() * worldDefinition.getColumns()) + " (amount of world):");
                alert.showAndWait();
                return null;
            }
        }

        return Integer.parseInt(choice);

    }
}
