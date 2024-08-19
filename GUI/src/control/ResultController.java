package control;

import facade.Facade;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import logic.definition.entity.api.EntityDefinition;
import logic.dto.*;
import logic.simulation.Simulation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ResultController {
    @FXML
    private ListView<String> executionListView;
    @FXML private TreeView<String> treeOfHistogram;
    @FXML private FlowPane executionResult;
    @FXML private Button pauseButton;
    @FXML private Label numberOfTick;
    @FXML private Label numberOfSeconds;
    @FXML private TableView tableOfEntities;
    @FXML private LineChart graphOfEntities;
    @FXML
    private HBox hboxScene;
    private SimulationManagerDTO  simulationManagerDTO;
    private ScheduledExecutorService currentTicksService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService updateEntitiesService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService updateGraphService = Executors.newSingleThreadScheduledExecutor();
    private Thread HistogramThread;
    private TableColumn<DataTable, String> nameColumn;
    private Thread rerunThread;
    private MainController mainController;
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    TableColumn<DataTable, Integer> amountColumn;
    ObservableList<DataTable> entityDataList;
    private Facade facade;
    private boolean alertShown = false;
    public void initialize() {
        ObservableList<String> items = FXCollections.observableArrayList();
        entityDataList = FXCollections.observableArrayList();
        nameColumn = new TableColumn<>("Entity");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("entityName"));
        amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("entityAmount"));
        tableOfEntities.getColumns().addAll(nameColumn, amountColumn);

        executionListView.setItems(items);
        currentTicksService.scheduleAtFixedRate(() -> {

            if (facade != null && simulationManagerDTO != null) {


                        Platform.runLater(() -> {
                            showCurrentTicks();
                            showCurrentSeconds();
                            //updateTableOfEntities();
                        });


        }}, 0, 1, TimeUnit.SECONDS);

        updateEntitiesService.scheduleAtFixedRate(() -> {

            if (facade != null && simulationManagerDTO != null) {


               updateTableOfEntities();
               Platform.runLater(() -> {
                   tableOfEntities.setItems(entityDataList);
               });


            }}, 0, 1, TimeUnit.SECONDS);

    }
    @FXML
    private void updateEntitiesGraph(){
        String selectedItem = executionListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            String[] split = selectedItem.split(" ");
            int id = Integer.parseInt(split[2]);
            Simulation simulationForDetails = null;
            for (Simulation simulation : facade.getSimulationsManager().getSimulationList()) {
                if (id == simulation.getId()) {
                    simulationForDetails = simulation;
                    break;
                }
            }
            if(simulationForDetails.getEndSimulation()){
                graphOfEntities.getData().clear();
                graphOfEntities.setTitle("Entity Amount for Simulation: " + simulationForDetails.getId());
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                SimulationCurrentDetailsDTO simulationCurrentDetailsDTO = simulationForDetails.getCurrentDetailsDTO();
                Map<Integer, List<Integer>> amoutOfEntities = simulationCurrentDetailsDTO.getAmoutOfEntitiesByTicks();
                for(Map.Entry<Integer, List<Integer>> entry : amoutOfEntities.entrySet()){
                    for(Integer amount : entry.getValue()){
                        series.getData().add(new XYChart.Data<>(entry.getKey(), amount));
                    }
                }

                graphOfEntities.getData().add(series);
            }


        }
    }
    @FXML
    private void updateTableOfEntities(){
        String selectedSimulation = executionListView.getSelectionModel().getSelectedItem();
        Simulation simulationForDetails = null;
        if (selectedSimulation != null) {
            entityDataList = FXCollections.observableArrayList();
            String[] split = selectedSimulation.split(" ");
            int id = Integer.parseInt(split[2]);
            for (Simulation simulation : facade.getSimulationsManager().getSimulationList()) {
                if (id == simulation.getId()) {
                    simulationForDetails = simulation;
                }
                //break;
            }
            for (EntityDefinition entity : simulationForDetails.getWorldDefinition().getPopulation()) {
                boolean found = false;
                for (DataTable existingData : entityDataList) {
                    if (existingData.getEntityName().equals(entity.getName())) {
                        existingData.setEntityAmount(simulationForDetails.getCurrentDetailsDTO()
                                .getAmoutOfEntities().get(entity.getName()));
                        found = true;
                        break;
                            }
                        }

                        if (!found) {
                            entityDataList.add(new DataTable(entity.getName(),
                                    simulationForDetails.getCurrentDetailsDTO()
                                            .getAmoutOfEntities().get(entity.getName())));
                        }
                    }


                }

            }

    @FXML
    private void onPauseButtonClicked(ActionEvent event) {
        String selectedSimulation = executionListView.getSelectionModel().getSelectedItem();
        if (selectedSimulation != null) {
            Thread thread = new Thread(() -> {
                String[] split = selectedSimulation.split(" ");
                int id = Integer.parseInt(split[2]);
                for(Simulation simulation : facade.getSimulationsManager().getSimulationList()){
                    if(simulation.getId() == id){
                        facade.pauseSimulation(simulation.getId());
                        break;
                    }
                }
            });
            thread.start();

        }
    }
    @FXML
    private void onResumeButtonClicked(ActionEvent event) {
        String selectedSimulation = executionListView.getSelectionModel().getSelectedItem();
        if (selectedSimulation != null) {
            Thread thread = new Thread(() -> {
                String[] split = selectedSimulation.split(" ");
                int id = Integer.parseInt(split[2]);
                for(Simulation simulation : facade.getSimulationsManager().getSimulationList()){
                    if(simulation.getId() == id){
                        facade.resumeSimulation(simulation.getId());
                        break;
                    }
                }
            });
            thread.start();
        }
    }
    @FXML
    private void onStopButtonClicked(ActionEvent event) {
        String selectedSimulation = executionListView.getSelectionModel().getSelectedItem();
        if (selectedSimulation != null) {
            Thread thread = new Thread(() -> {
                String[] split = selectedSimulation.split(" ");
                int id = Integer.parseInt(split[2]);
                for(Simulation simulation : facade.getSimulationsManager().getSimulationList()){
                    if(simulation.getId() == id){
                        facade.stopSimulation(simulation.getId());
                        /**Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Simulation Finished");
                            alert.setHeaderText(null);
                            alert.setContentText(simulation.getSimulationOutput().getReasonsOfEnding());
                            alert.showAndWait();
                        });*/
                        break;
                    }
                }
            });
            thread.start();
        }
    }
    @FXML
    private void onRerunButtonClicked(ActionEvent event) {
        String selectedSimulation = executionListView.getSelectionModel().getSelectedItem();
        if (selectedSimulation != null ) {
            Simulation simulationEnding;
            String[] split = selectedSimulation.split(" ");
            int id = Integer.parseInt(split[2]);
            simulationEnding = facade.getSimulationsManager().getSimulationList().stream().filter(simulation -> simulation.getId() == id).findFirst().orElse(null);
            rerunThread = new Thread(() -> {
                Platform.runLater(() -> {
                    if(simulationEnding.getEndSimulation()){
                        mainController.setSimulationHistory(simulationEnding);
                        mainController.loadNewExecutionSceneFromResultScene("/screens/NewExecutionScene.fxml");

                    }


                });
            });
            rerunThread.start();
        }

    }
    public void setHboxScene(HBox hboxScene) {
        this.hboxScene = hboxScene;
    }

    public void setFacade(Facade facade) {
        this.facade = facade;
    }

    public void setSimulationManagerDTO(SimulationManagerDTO simulationManagerDTO) {
        this.simulationManagerDTO = simulationManagerDTO;
        loadSimulationList();

    }



    private void loadSimulationList(){
        Simulation findSimulation = null;
        Thread labelOfRunStop;
        for(SimulationDTO simulationDTO : simulationManagerDTO.getSimulationList()){
            for(Simulation simulation : facade.getSimulationsManager().getSimulationList()){
                if(simulationDTO.getId() == simulation.getId()){
                    findSimulation = simulation;
                    break;
                }
            }
            Simulation finalFindSimulation = findSimulation;
            labelOfRunStop = new Thread(() -> {
                if (finalFindSimulation.getEndSimulation()) {
                    Platform.runLater(() -> {
                        executionListView.getItems().add("S simulation " + simulationDTO.getId() + " - date: " + simulationDTO.getDate());
                    });
                } else {
                    Platform.runLater(() -> {
                        if(finalFindSimulation.isRunning()){
                            executionListView.getItems().add("R simulation " + simulationDTO.getId() + " - date: " + simulationDTO.getDate());
                            //showCurrentTicks(simulationDTO.getId());
                        }

                            //showCurrentTicks(simulationDTO.getId());

                    });
                }

            });

            labelOfRunStop.start();
            ScheduledExecutorService someUpdateThread = Executors.newSingleThreadScheduledExecutor();
            someUpdateThread.scheduleAtFixedRate(() -> {
                if (finalFindSimulation.getEndSimulation()) {
                    Platform.runLater(() -> {
                        // Replace 'YourSimulationId' with the actual ID you're looking for
                        int targetSimulationId = finalFindSimulation.getId();

                        // Use the 'filtered' method to find the item by ID
                        Optional<String> item = executionListView.getItems().stream()
                                .filter(s -> s.contains("R simulation " + targetSimulationId))
                                .findFirst();

                        // Check if the item was found, and then do something with it
                        item.ifPresent(foundItem -> {
                            String updatedItem = foundItem.replace("R simulation", "S simulation");

                            // Update the item in the list view
                            int index = executionListView.getItems().indexOf(foundItem);
                            executionListView.getItems().set(index, updatedItem);
                        });
                    });
                }
            }, 0,1, TimeUnit.SECONDS);



        }

    }

    @FXML
    private void showCurrentTicks() {
        String selectedItem = executionListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String[] split = selectedItem.split(" ");
            int id = Integer.parseInt(split[2]);
            Simulation simulationForDetails = facade.getSimulationsManager().getSimulationList().stream().filter(simulation -> id == simulation.getId()).findFirst().orElse(null);

            numberOfTick.setText(
                    Integer.toString(simulationForDetails.getCurrentDetailsDTO().getCurrentTick()));
            if(simulationForDetails.getEndSimulation() && !alertShown){
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Simulation Finished");
                    alert.setHeaderText(null);
                    alert.setContentText(simulationForDetails.getSimulationOutput().getReasonsOfEnding());
                    alert.showAndWait();
                });
                alertShown = true;
            }

        }
    }
    @FXML
    private void showCurrentSeconds() {
        String selectedItem = executionListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String[] split = selectedItem.split(" ");
            int id = Integer.parseInt(split[2]);
            Simulation simulationForDetails = null;
            for (Simulation simulation : facade.getSimulationsManager().getSimulationList()) {
                if (id == simulation.getId()) {
                    simulationForDetails = simulation;
                    break;
                }
            }
            numberOfSeconds.setText(
                    Integer.toString(simulationForDetails.getCurrentDetailsDTO().getCurrentSecond()));

        }

    }

    @FXML
    public void showHistogram() {
        updateEntitiesGraph();

        String selectedItem = executionListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String[] split = selectedItem.split(" ");
            int id = Integer.parseInt(split[2]);
            SimulationDTO chosenSimulation = null;
            Simulation simulationForBuildHistogram = null;
            simulationManagerDTO = facade.getSimulationManagerDTO();
            for(SimulationDTO simulation : simulationManagerDTO.getSimulationList()){
                if(simulation.getId() == id){
                    chosenSimulation = simulation;
                    break;
                }
            }
            for(Simulation simulation : facade.getSimulationsManager().getSimulationList()){
                if(chosenSimulation.getId() == simulation.getId()){
                    simulationForBuildHistogram = simulation;
                    break;
                }
            }
            if(simulationForBuildHistogram.getEndSimulation()){
                showHistogramByEntity(simulationForBuildHistogram, chosenSimulation);
            }


        }
    }
    @FXML
    private void showHistogramByEntity(Simulation simulationForBuildHistogram, SimulationDTO chosenSimulation){
        int numberOfEntity = 0;
        if (treeOfHistogram.getRoot() == null) {
            treeOfHistogram.setRoot(new TreeItem<>("Histogram"));
        }
        else{
            treeOfHistogram.getRoot().getChildren().clear();
        }

        for (EntityDefinitionDTO entity : chosenSimulation.getWorldDefinitionDTO().getEntityDefinitionDTOS()) {


            treeOfHistogram.getRoot().getChildren().add(new TreeItem<>("Entity: " + entity.getName())); //0
            treeOfHistogram.getRoot().getChildren().get(numberOfEntity).getChildren().add(new TreeItem<>("Start number of entities: " + entity.getStartPopulation())); //0
            treeOfHistogram.getRoot().getChildren().get(numberOfEntity).getChildren().add(new TreeItem<>("End number of entities: " + entity.getEndPopulation())); //1
            //treeOfHistogram.getRoot().getChildren().add(new TreeItem<>("properties"));
            showHistogramByProp(entity, simulationForBuildHistogram, numberOfEntity);
            numberOfEntity++;
        }
    }
    @FXML
    private void showHistogramByProp(EntityDefinitionDTO chosenEntity, Simulation simulation, int numberOfEntity){
        int numberOfProperty = 2;
        for (PropertyDefinitionDTO property : chosenEntity.getProperties()) {
          treeOfHistogram.getRoot().getChildren().get(numberOfEntity).getChildren().add(new TreeItem<>(property.getName())); //2
           HistogramSimulationDTO histogramSimulationDTO =facade.createHistogramForSimulation(simulation.buildHistogramForSimulation(chosenEntity.getName(),
                    property.getName()));
            Map<Object, Integer> histogram = histogramSimulationDTO.getHistogram();
            if(!histogram.isEmpty()){
                treeOfHistogram.getRoot().getChildren().get(numberOfEntity).getChildren().get(numberOfProperty).getChildren().add(new TreeItem<>("histogram: "));
                for (Map.Entry<Object, Integer> entry : histogram.entrySet()) {
                    treeOfHistogram.getRoot().getChildren().get(numberOfEntity).getChildren().get(numberOfProperty).getChildren().add(new TreeItem<>("There are " + entry.getValue() + " entities that" +
                            " the " + property.getName() + " value is "+ entry.getKey()));
                }
                treeOfHistogram.getRoot().getChildren().get(numberOfEntity).getChildren().get(numberOfProperty).getChildren().
                        add(new TreeItem<>("The consistency of "+ property.getName() + " is: " + histogramSimulationDTO.getConsistency()));
                if(property.getType().equals("FLOAT")){
                    treeOfHistogram.getRoot().getChildren().get(numberOfEntity).getChildren().get(numberOfProperty).getChildren().
                            add(new TreeItem<>("The average value of "+ property.getName() + " is: " + histogramSimulationDTO.getAverage()));
                }


            }
            numberOfProperty++;
        }

    }


    public void setHistogramThread(Thread histogramThread) {
        this.HistogramThread = histogramThread;

    }
}