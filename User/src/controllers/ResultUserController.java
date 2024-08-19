package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.*;
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
import okhttp3.*;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static controllers.LoginController.BASE_URL;
import static controllers.LoginController.HTTP_CLIENT;

public class ResultUserController {
    @FXML
    private ListView<String> executionListView;
    @FXML private TreeView<String> treeOfHistogram;
    @FXML private FlowPane executionResult;
    //    @FXML private Button pauseButton;
    @FXML private Label numberOfTick;
    @FXML private Label numberOfSeconds;
    @FXML private TableView tableOfEntities;
    @FXML private LineChart graphOfEntities;
    @FXML
    private HBox hboxScene;
    private SimulationManagerDTO simulationManagerDTO;
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
    private boolean alertShown = false;
    public void initialize() {
        requestForBuildSimulationUser();
        ScheduledExecutorService asksForManager = Executors.newSingleThreadScheduledExecutor();
        asksForManager.scheduleAtFixedRate(() ->{requestSimulationManager();}, 0, 1, TimeUnit.SECONDS);
        Platform.runLater(() -> {loadSimulationList();});
        ObservableList<String> items = FXCollections.observableArrayList();
        entityDataList = FXCollections.observableArrayList();
        nameColumn = new TableColumn<>("Entity");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("entityName"));
        amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("entityAmount"));
        tableOfEntities.getColumns().addAll(nameColumn, amountColumn);

        executionListView.setItems(items);
        currentTicksService.scheduleAtFixedRate(() -> {


            if (simulationManagerDTO != null) {


                Platform.runLater(() -> {
                    showCurrentTicks();
                    showCurrentSeconds();
                    //updateTableOfEntities();
                });


            }}, 0, 1, TimeUnit.SECONDS);

        updateEntitiesService.scheduleAtFixedRate(() -> {
            if (simulationManagerDTO != null) {


                updateTableOfEntities();
                Platform.runLater(() -> {
                    tableOfEntities.setItems(entityDataList);
                });


            }}, 0, 1, TimeUnit.SECONDS);

    }

    private void requestForBuildSimulationUser(){
        String RESOURCE = "/Server_Web_exploded/build-simulation-per-user";
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .get()
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {

                } else {
                    // Handle unsuccessful response here
                }
                response.close();
            }
        });
    }

    private void requestSimulationManager(){
        String RESOURCE = "/Server_Web_exploded/simulation-manager";
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .get()
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        try(Response response = call.execute()){
            if(response.isSuccessful()){
                String jsonResponse = response.body().string();
                Gson gson = new Gson();
                simulationManagerDTO = gson.fromJson(jsonResponse, SimulationManagerDTO.class);


            }

            response.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean requestEndSimulation(int id){
        String RESOURCE = "/Server_Web_exploded/end-simulation";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String simulationID = gson.toJson(id);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + RESOURCE)
                .newBuilder()
                .addQueryParameter("simulationID", simulationID);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        Boolean isEndSimulation = null;
        try(Response response = call.execute()){
            if(response.isSuccessful()){
                isEndSimulation = gson.fromJson(response.body().string(), Boolean.class);
            }
            //response.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return isEndSimulation;
    }


    @FXML
    private void updateEntitiesGraph(){
        String selectedItem = executionListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            String[] split = selectedItem.split(" ");
            int id = Integer.parseInt(split[3]);
            SimulationDTO simulationForDetails = null;
            for (SimulationDTO simulation : simulationManagerDTO.getSimulationList()) {
                if (id == simulation.getId()) {
                    simulationForDetails = simulation;
                    break;
                }
            }
            if(requestEndSimulation(simulationForDetails.getId())){
                graphOfEntities.getData().clear();
                graphOfEntities.setTitle("Entity Amount for Simulation: " + simulationForDetails.getId());
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                SimulationCurrentDetailsDTO simulationCurrentDetailsDTO =requestForCurrentDetailsSimulation(simulationForDetails.getId());
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
        if (selectedSimulation != null) {
            entityDataList = FXCollections.observableArrayList();
            String[] split = selectedSimulation.split(" ");
            int id = Integer.parseInt(split[3]);
            SimulationDTO simulationForDetails = null;
            for (SimulationDTO simulation : simulationManagerDTO.getSimulationList()) {
                if (id == simulation.getId()) {
                    simulationForDetails = simulation;
                    break;
                }
            }
            for (EntityDefinitionDTO entity : simulationForDetails.getWorldDefinitionDTO().getEntityDefinitionDTOS()) {
                boolean found = false;
                for (DataTable existingData : entityDataList) {
                    if (existingData.getEntityName().equals(entity.getName())) {
                        existingData.setEntityAmount(requestForCurrentDetailsSimulation(simulationForDetails.getId())
                                .getAmoutOfEntities().get(entity.getName()));
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    entityDataList.add(new DataTable(entity.getName(),
                            requestForCurrentDetailsSimulation(simulationForDetails.getId())
                                    .getAmoutOfEntities().get(entity.getName())));
                }
            }


        }

    }
    private SimulationCurrentDetailsDTO requestForCurrentDetailsSimulation(int id){
        String RESOURCE = "/Server_Web_exploded/current-datails-simulation";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String simulationID = gson.toJson(id);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + RESOURCE)
                .newBuilder()
                .addQueryParameter("simulationID", simulationID);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        SimulationCurrentDetailsDTO simulationCurrentDetailsDTO = null;
        try(Response response = call.execute()){
            if(response.isSuccessful()){
                simulationCurrentDetailsDTO = gson.fromJson(response.body().string(), SimulationCurrentDetailsDTO.class);
            }
            response.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return simulationCurrentDetailsDTO;
    }
    private void requestPauseSimulation(int id){
        String RESOURCE = "/Server_Web_exploded/pause-simulation";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String idJson = gson.toJson(id);
        RequestBody formBody = new FormBody.Builder()
                .add("simulationID", idJson)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .post(formBody)
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {

                } else {
                    // Handle unsuccessful response here
                }
                response.close();
            }
        });
    }

    private void requestResumeSimulation(int id){
        String RESOURCE = "/Server_Web_exploded/resume-simulation";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String idJson = gson.toJson(id);
        RequestBody formBody = new FormBody.Builder()
                .add("simulationID", idJson)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .post(formBody)
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {


                } else {
                    // Handle unsuccessful response here
                }
            }
        });
    }

    private void requestStopSimulation(int id){
        String RESOURCE = "/Server_Web_exploded/stop-simulation";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String idJson = gson.toJson(id);
        RequestBody formBody = new FormBody.Builder()
                .add("simulationID", idJson)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .post(formBody)
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {

                } else {
                    // Handle unsuccessful response here
                }
                response.close();
            }
        });
    }

        @FXML
    private void onPauseButtonClicked(ActionEvent event) {
        String selectedSimulation = executionListView.getSelectionModel().getSelectedItem();
        if (selectedSimulation != null) {
            Thread thread = new Thread(() -> {
                String[] split = selectedSimulation.split(" ");
                int id = Integer.parseInt(split[3]);
                //TODO : request from server to pause
                for(SimulationDTO simulation : simulationManagerDTO.getSimulationList()){
                    if(simulation.getId() == id){
                        requestPauseSimulation(simulation.getId());
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
                int id = Integer.parseInt(split[3]);
                //TODO: request from server to resume
                for(SimulationDTO simulation : simulationManagerDTO.getSimulationList()){
                    if(simulation.getId() == id){
                        requestResumeSimulation(simulation.getId());
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
                int id = Integer.parseInt(split[3]);
                //TODO: request from server to stop
                for(SimulationDTO simulation : simulationManagerDTO.getSimulationList()){
                    if(simulation.getId() == id){
                        requestStopSimulation(simulation.getId());

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
            SimulationDTO simulationEnding;
            String[] split = selectedSimulation.split(" ");
            int id = Integer.parseInt(split[3]);
            //TODO: request from server to rerun
            requestSimulationManager();
            simulationEnding = simulationManagerDTO.getSimulationList().stream().filter(simulation -> simulation.getId() == id).findFirst().orElse(null);
            rerunThread = new Thread(() -> {
                Platform.runLater(() -> {
                    if(requestEndSimulation(id)){
                        mainController.setSimulationHistory(simulationEnding);
                        mainController.loadNewExecutionSceneFromResultScene();

                    }


                });
            });
            rerunThread.start();
        }

    }
    public void setHboxScene(HBox hboxScene) {
        this.hboxScene = hboxScene;
    }


    private List<SimulationDTO> requestForSimulationUser(){
        String RESOURCE = "/Server_Web_exploded/get-simulations-per-user";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .get()
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        List<SimulationDTO> simulationDTOList = null;
        try(Response response = call.execute()){
            if(response.isSuccessful()){
                simulationDTOList = gson.fromJson(response.body().string(), new TypeToken<List<SimulationDTO>>(){}.getType());
            }
            response.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return simulationDTOList;
    }

    private void loadSimulationList(){
        SimulationDTO findSimulation = null;
        Thread labelOfRunStop;
        for(SimulationDTO simulationDTO : requestForSimulationUser()){
            labelOfRunStop = new Thread(() -> {
                if (requestEndSimulation(simulationDTO.getId())) {
                    Platform.runLater(() -> {
                        executionListView.getItems().add("S simulation " + simulationDTO.getSimulationName() + " "+ simulationDTO.getId() + " - date: " + simulationDTO.getDate());
                    });
                } else {
                    Platform.runLater(() -> {

                        executionListView.getItems().add("R simulation " + simulationDTO.getSimulationName() + " "+ simulationDTO.getId() + " - date: " + simulationDTO.getDate());
                            //showCurrentTicks(simulationDTO.getId());


                        //showCurrentTicks(simulationDTO.getId());

                    });
                }

            });

            labelOfRunStop.start();
            ScheduledExecutorService someUpdateThread = Executors.newSingleThreadScheduledExecutor();
            someUpdateThread.scheduleAtFixedRate(() -> {
                if (requestEndSimulation(simulationDTO.getId())) {
                    Platform.runLater(() -> {
                        // Replace 'YourSimulationId' with the actual ID you're looking for
                        int targetSimulationId = simulationDTO.getId();

                        // Use the 'filtered' method to find the item by ID
                        Optional<String> item = executionListView.getItems().stream()
                                .filter(s -> s.contains("R simulation " + targetSimulationId))
                                .findFirst();

                        // Check if the item was found, and then do something with it
                        item.ifPresent(foundItem -> {
                            String updatedItem = foundItem.replace("R simulation ", "S simulation ");

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
            int id = Integer.parseInt(split[3]);
            //requestSimulationManager();
            SimulationDTO simulationForDetails = simulationManagerDTO.getSimulationList().stream().filter(simulation -> id == simulation.getId()).findFirst().orElse(null);
            SimulationCurrentDetailsDTO simulationCurrentDetailsDTO = requestForCurrentDetailsSimulation(simulationForDetails.getId());
            if(simulationCurrentDetailsDTO != null){
                numberOfTick.setText(
                        Integer.toString(simulationCurrentDetailsDTO.getCurrentTick()));
            }



            if(requestEndSimulation(simulationForDetails.getId()) && !alertShown){
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
            int id = Integer.parseInt(split[3]);
            SimulationDTO simulationForDetails = null;
            //requestSimulationManager();
            for (SimulationDTO simulation : simulationManagerDTO.getSimulationList()) {
                if (id == simulation.getId()) {
                    simulationForDetails = simulation;
                    break;
                }
            }
            SimulationCurrentDetailsDTO simulationCurrentDetailsDTO = requestForCurrentDetailsSimulation(simulationForDetails.getId());
            if(simulationCurrentDetailsDTO != null) {
                numberOfSeconds.setText(
                        Integer.toString(simulationCurrentDetailsDTO.getCurrentSecond()));
            }



        }

    }

    @FXML
    public void showHistogram() {
        updateEntitiesGraph();

        String selectedItem = executionListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String[] split = selectedItem.split(" ");
            int id = Integer.parseInt(split[3]);
            SimulationDTO chosenSimulation = null;
            for(SimulationDTO simulation : simulationManagerDTO.getSimulationList()){
                if(simulation.getId() == id){
                    chosenSimulation = simulation;
                    break;
                }
            }
            if(requestEndSimulation(chosenSimulation.getId())){
                showHistogramByEntity(chosenSimulation);
            }


        }
    }
    @FXML
    private void showHistogramByEntity(SimulationDTO chosenSimulation){
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
            showHistogramByProp(entity, chosenSimulation, numberOfEntity);
            numberOfEntity++;
        }
    }

    private HistogramSimulationDTO requestForHistogram() {
        String RESOURCE = "/Server_Web_exploded/create-histogram";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .get()
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        HistogramSimulationDTO histogramSimulationDTO = null;
        try(Response response = call.execute()){
            if(response.isSuccessful()){
                histogramSimulationDTO = gson.fromJson(response.body().string(), HistogramSimulationDTO.class);
            }
            response.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return histogramSimulationDTO;

    }

    private void requestForBuildHistogram(Integer id, EntityDefinitionDTO chosenEntity, PropertyDefinitionDTO chosenProperty){
        String RESOURCE = "/Server_Web_exploded/create-histogram";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String idJson = gson.toJson(id);
        String propertyJson = gson.toJson(chosenProperty);
        String entityJson = gson.toJson(chosenEntity);
        RequestBody formBody = new FormBody.Builder()
                .add("simulationID", idJson)
                .add("property", propertyJson)
                .add("entity", entityJson)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .post(formBody)
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        //HistogramSimulationDTO histogramSimulationDTO = null;
        try(Response response = call.execute()){
            if(response.isSuccessful()){
                //histogramSimulationDTO = gson.fromJson(response.body().string(), HistogramSimulationDTO.class);
            }
            response.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void showHistogramByProp(EntityDefinitionDTO chosenEntity, SimulationDTO simulation, int numberOfEntity){
        int numberOfProperty = 2;
        for (PropertyDefinitionDTO property : chosenEntity.getProperties()) {
            treeOfHistogram.getRoot().getChildren().get(numberOfEntity).getChildren().add(new TreeItem<>(property.getName())); //2
            requestForBuildHistogram(simulation.getId(), chosenEntity, property);
            //TODO: the server will do it by dto and no simulation - the server will build the Histogram DTO by sending him the dto simulation
            HistogramSimulationDTO histogramSimulationDTO =requestForHistogram();
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
