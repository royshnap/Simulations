package control;

import facade.Facade;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import logic.dto.QueueManagmentDTO;
import logic.dto.SimulationHistory;
import logic.dto.WorldDefinitionDTO;
import logic.simulation.Simulation;
import logic.world.WorldDefinition;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController {
    @FXML private TextField filePathTextField;
    @FXML private HBox hboxScene;
    @FXML private HBox hboxOf3Buttons;
    private final StringProperty loadedFilePath = new SimpleStringProperty();
    private boolean fileLoadedSuccessfully = false;
    private boolean buttonsCreated = false;
    private Facade facade = new Facade();
    private WorldDefinitionDTO worldDefinitionDTO;
    private WorldDefinition worldDefinition;
    private static String file;
    private static Stack<String> files = new Stack<>();
    @FXML
    private VBox queueManagmentVbox;
    @FXML private Label waitingLabel;
    @FXML private Label runningLabel;
    @FXML private Label finishedLabel;
    private SimulationHistory simulationHistory;
    private Simulation simulationEnding;
    ScheduledExecutorService queueManagement = Executors.newSingleThreadScheduledExecutor();

    public void initialize() {
        filePathTextField.textProperty().bind(loadedFilePath);

        loadedFilePath.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                try {
                    facade = new Facade();
                    worldDefinitionDTO = facade.generatorOperation(newValue);
                    facade.createThreadPool(worldDefinitionDTO.getNumberOfThreads());
                    files.add(newValue);
                    worldDefinition = facade.getWorldDefinition();
                    if (!fileLoadedSuccessfully) {
                        fileLoadedSuccessfully = true;
                        setButtonsVisibility(true);
                    }

                } catch (JAXBException | IllegalArgumentException e) {
                    showExceptionAlert("Error", "An exception occurred", e.getMessage());
                    if (!fileLoadedSuccessfully) {
                        setButtonsVisibility(false);
                    }
                }
            }
        });
        queueManagement.scheduleAtFixedRate(() -> {
            QueueManagmentDTO queueManagmentDTO = facade.getQueueManagmentDTO();
            Platform.runLater(() -> {
                waitingLabel.setText("Waiting simulations: " + queueManagmentDTO.getWaitingSimulations());
                runningLabel.setText("Running simulations: " + queueManagmentDTO.getRunningSimulations());
                finishedLabel.setText("Finished simulations: " + queueManagmentDTO.getFinishedSimulations());
            });
        }, 0, 1, TimeUnit.SECONDS);


    }
    private void showExceptionAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }
        @FXML
        private void loadFileButtonClicked(ActionEvent event) {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                String selectedFilePath = selectedFile.getAbsolutePath();
                loadedFilePath.set(selectedFilePath);
                if (!buttonsCreated) {
                    createButtons();
                    buttonsCreated = true;
                }

            }
        }
    private void createButtons() {
        Button detailsButton = new Button("Details");
        Button newExecutionButton = new Button("New Execution");
        Button resultsButton = new Button("Results");

        detailsButton.setOnAction(e -> loadDetailsScene("/screens/DetailsScene.fxml"));
        newExecutionButton.setOnAction(e -> loadNewExecutionScene("/screens/NewExecutionScene.fxml"));
        resultsButton.setOnAction(e -> loadResultScene("/screens/ResultScene.fxml"));

        hboxOf3Buttons.getChildren().addAll(detailsButton, newExecutionButton, resultsButton);
    }

    private void loadDetailsScene(String scenePath) {
        try {
            if(worldDefinitionDTO != null){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(scenePath));
                Parent newSceneRoot = fxmlLoader.load();
                DetailController detailsController = fxmlLoader.getController();
                detailsController.setWorldDefinitionDTO(worldDefinitionDTO);

                hboxScene.getChildren().clear();
                hboxScene.getChildren().add(newSceneRoot);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadNewExecutionSceneFromResultScene(String scenePath) {
            try {
                try {
                    file = files.peek();
                    worldDefinitionDTO = facade.generatorOperation(file);
                    worldDefinition = facade.getWorldDefinition();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                if (worldDefinitionDTO != null) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(scenePath));
                    Parent newSceneRoot = fxmlLoader.load();
                    NewExecutionController newExecutionController = fxmlLoader.getController();
                    newExecutionController.setSimulationForHistory(simulationEnding);
                    newExecutionController.setIsFromResultScene(true);
                    newExecutionController.setFacade(facade);
                    newExecutionController.setWorldDefinitionDTO(worldDefinitionDTO, worldDefinition);
                    newExecutionController.setHboxScene(hboxScene);
                    newExecutionController.setMainController(this);


                    hboxScene.getChildren().clear();
                    hboxScene.getChildren().add(newSceneRoot);
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }

        }


    public void loadNewExecutionScene(String scenePath) {
        try {
            try {
                file = files.peek();
                worldDefinitionDTO  = facade.generatorOperation(file);
                worldDefinition = facade.getWorldDefinition();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            if(worldDefinitionDTO != null){

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(scenePath));
                Parent newSceneRoot = fxmlLoader.load();
                NewExecutionController newExecutionController = fxmlLoader.getController();
                newExecutionController.setIsFromResultScene(false);
                newExecutionController.setFacade(facade);
                newExecutionController.setWorldDefinitionDTO(worldDefinitionDTO, worldDefinition);
                newExecutionController.setHboxScene(hboxScene);
                newExecutionController.setMainController(this);
                simulationHistory = newExecutionController.getSimulationHistory();


                hboxScene.getChildren().clear();
                hboxScene.getChildren().add(newSceneRoot);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadResultScene(String scenePath) {
        try {
            if(worldDefinitionDTO != null && facade.getSimulationManagerDTO()!=null){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(scenePath));
                Parent resultSceneRoot = fxmlLoader.load();
                ResultController resultController = fxmlLoader.getController();
                resultController.setFacade(facade);
                resultController.setSimulationManagerDTO(facade.getSimulationManagerDTO());
                resultController.setHboxScene(hboxScene);
                resultController.setMainController(this);

                hboxScene.getChildren().clear();
                hboxScene.getChildren().add(resultSceneRoot);
            }
             // Add new scene contents to HBox
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setButtonsVisibility(boolean isVisible) {
        hboxOf3Buttons.setVisible(isVisible);
        hboxOf3Buttons.setManaged(isVisible);
    }


    public void setSimulationHistory(Simulation simulationEnding) {
        this.simulationEnding = simulationEnding;
    }
}
