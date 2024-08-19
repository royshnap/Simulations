package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
import facade.Facade;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static controllers.LoginController.BASE_URL;
import static controllers.LoginController.HTTP_CLIENT;

public class ExecutionController {
    @FXML
    private HBox hboxScene;

    @FXML
    private Button startSimulationButton;

    @FXML
    private VBox environmentVbox;
    @FXML
    private VBox entitiesVbox;
    private WorldDefinitionDTO worldDefinitionDTO;
    //private WorldDefinition worldDefinition;
    private Facade facade;
    private ActiveEnvironmentDTO environmentVariablesManager;
    List<PropertyInstanceDTO> propertyInstanceDTOS = new ArrayList<>();
    private AtomicBoolean shouldContinue = new AtomicBoolean(true);
    private Thread histogramThread;
    private SimulationHistoryDTO simulationHistoryDTO;
    private boolean isFromResultScene;
    @FXML
    private Button clearButton;
    private Integer simulationForHistoryID;

    private MainController mainController;
    //private Simulation simulationForHistory;

    public  void setSimulationForHistoryID(Integer simulationEndingID) {
        this.simulationForHistoryID =simulationEndingID;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    public void setIsFromResultScene(boolean isFromResultScene) {
        this.isFromResultScene = isFromResultScene;
    }


    public void setWorldDefinitionDTO(WorldDefinitionDTO worldDefinitionDTO) {
        this.worldDefinitionDTO = worldDefinitionDTO;
        //this.worldDefinition = worldDefinition;
        if(isFromResultScene == true){
            clearButton.setVisible(false);
        }
        displayEnvVariables();
        displayEntities();


    }
    /**Will create history in the facde from the server request*/
    private void createHistory(){
        String RESOURCE = "/Server_Web_exploded/create-history";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String environmentVariablesManagerJson = gson.toJson(environmentVariablesManager);
        String propertyInstancesJson = gson.toJson(propertyInstanceDTOS);
        String worldDefinitionJson = gson.toJson(worldDefinitionDTO);
        RequestBody formBody = new FormBody.Builder()
                .add("worldDefinition", worldDefinitionJson)
                .add("environmentManager", environmentVariablesManagerJson)
                .add("propertyInstances", propertyInstancesJson)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .post(formBody)
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure here
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){

                }
                response.close();

            }
        });

    }

    public void setHboxScene(HBox hboxScene) {
        this.hboxScene = hboxScene;
    }

    public void setFacade(Facade facade) {
        this.facade = facade;
    }

    public void initialize(){


    }
    private void requestForDisplayEntities(){

    }

    private void displayEntities() {

        int rowIndex = 0;
        if(isFromResultScene == false){
            for(EntityDefinitionDTO entityDefinitionDTO : worldDefinitionDTO.getEntityDefinitionDTOS()){
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
                            //i put the value in the dto and then send the world definition dto to server
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
                //in the create history i send the world definition dto with the values of populations
                createHistory();});

        }
        else{
            for(EntityDefinitionDTO entityDefinition : requestForSimluationHistory().getEntityDefinitionsDTOS()){
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

    public SimulationHistoryDTO getSimulationHistoryDTO() {
        return simulationHistoryDTO;
    }
    private void requestStartSimulation(){
        String RESOURCE = "/Server_Web_exploded/start-simulation";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String environmentVariablesManagerJson = gson.toJson(environmentVariablesManager);
        RequestBody formBody = new FormBody.Builder()
                .add("environmentManager", environmentVariablesManagerJson)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .post(formBody)
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure here
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Start simulation response");
                        alert.setHeaderText(null);
                        try {
                            alert.setContentText(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        alert.showAndWait();
                    });
                }

            }
        });
    }

    @FXML
    public void loadStartSimulation() throws IOException {
        if(isFromResultScene == false){
            if(environmentVariablesManager != null && !shouldContinue.get()){
                histogramThread = new Thread(() -> {
                    //TODO: requests for start simulation to the sever
                    requestStartSimulation();

                    Platform.runLater(() -> {
                        try {
                            mainController.loadResultScene();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
                histogramThread.start();
            }
        }
        else{
            histogramThread = new Thread(() -> {
                    //TODO: requests the server do it - just send the id of the simulation history
                   requestStartSimulationInHistory();

                Platform.runLater(() -> {
                    try {
                        mainController.loadResultScene();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            histogramThread.start();
        }
    }
    private void requestStartSimulationInHistory(){
        String RESOURCE = "/Server_Web_exploded/start-simulation-history";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String simulationHistoryID = gson.toJson(simulationForHistoryID);
        RequestBody formBody = new FormBody.Builder()
                .add("simulationID", simulationHistoryID)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .post(formBody)
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure here
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Start simulation response");
                        alert.setHeaderText(null);
                        try {
                            alert.setContentText(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        alert.showAndWait();
                    });
                }
                //response.close();

            }
        });
    }
    @FXML
    public void loadClear(){
        propertyInstanceDTOS = new ArrayList<>();
        //TODO : do it as requests to sever
        if(worldDefinitionDTO != null){
            for(EntityDefinitionDTO entityDefinition : worldDefinitionDTO.getEntityDefinitionDTOS()){
                entityDefinition.setStartPopulation(0);
                entityDefinition.setEndPopulation(0);
            }
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
                            //TODO : requests for the server to do it (line 265)
                            PropertyInstanceDTO propertyInstance = requestCreatePropertyInstnace(envVariable, choice);
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
                        //TODO : requests for the server to do it (line 288)
                        PropertyInstanceDTO propertyInstance = requestCreatePropertyInstnace(propertyDefinitionDTO, null);
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
            //TODO : requests for the server to do it (304)
            for(PropertyInstanceDTO envVariable : requestForSimluationHistory().getPropertyInstanceDTOS()){
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

    private PropertyInstanceDTO requestCreatePropertyInstnace(PropertyDefinitionDTO propertyDefinition,
                                               Object value){
        String RESOURCE = "/Server_Web_exploded/create-property-instance";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String propertyDefinitionDTO = gson.toJson(propertyDefinition);
        String valueDTO = gson.toJson(value);
        RequestBody formBody = new FormBody.Builder()
                .add("propertyDefinition", propertyDefinitionDTO)
                .add("value", valueDTO)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .post(formBody)
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        PropertyInstanceDTO result = null;
        try(Response response = call.execute()){
            if(response.isSuccessful()){
                result = gson.fromJson(response.body().string(), PropertyInstanceDTO.class);
            }
            response.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }
    private SimulationHistoryDTO requestForSimluationHistory() {
        String RESOURCE = "/Server_Web_exploded/create-history";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String simulationHistoryID = gson.toJson(simulationForHistoryID);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + RESOURCE)
                .newBuilder()
                .addQueryParameter("simulationID", simulationHistoryID);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        SimulationHistoryDTO simulationHistoryDTO = null;
        try(Response response = call.execute()) {
            if (response.isSuccessful()) {
                simulationHistoryDTO = gson.fromJson(response.body().string(), SimulationHistoryDTO.class);
            }
            response.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return simulationHistoryDTO;
    }

    private Object processUserChoiceForEnv(PropertyDefinitionDTO chosenEnvVariable,String userInput) {
        if (chosenEnvVariable.getType().equals("DECIMAL")) {
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

        } else if (chosenEnvVariable.getType().equals("FLOAT")) {
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

        } else if (chosenEnvVariable.getType().equals("BOOLEAN")) {
            if(!(userInput.equalsIgnoreCase("true") || userInput.equalsIgnoreCase("false"))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The value you entered should be only true or false");
                alert.showAndWait();
                return null;

            }
            return Boolean.getBoolean(userInput.toLowerCase());

        } else if (chosenEnvVariable.getType().equals("STRING")) {
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
                    + (worldDefinitionDTO.getRows() * worldDefinitionDTO.getCols()) + " (amount of world):");
            alert.showAndWait();
            return null;

        }
        else{
            int numberPopulation = Integer.parseInt(choice);
            int sumOfPopulation = 0;
            for(EntityDefinitionDTO entityDefinition : worldDefinitionDTO.getEntityDefinitionDTOS()){
                sumOfPopulation += entityDefinition.getStartPopulation();
            }
            if((worldDefinitionDTO.getRows() * worldDefinitionDTO.getCols()) < sumOfPopulation+numberPopulation){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The sum of all population after entering this value is bigger than world amount (" +
                        + (worldDefinitionDTO.getRows() * worldDefinitionDTO.getCols()) + ")" + " Please enter different value");
                alert.showAndWait();
                return null;
            }
            if((worldDefinitionDTO.getRows() * worldDefinitionDTO.getCols())< numberPopulation){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The value you entered is incorrect. Please enter a DECIMAL number that lower than " +
                        + (worldDefinitionDTO.getRows() * worldDefinitionDTO.getCols()) + " (amount of world):");
                alert.showAndWait();
                return null;
            }
        }

        return Integer.parseInt(choice);

    }
}
