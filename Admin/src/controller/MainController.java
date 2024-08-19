package controller;

import com.google.gson.Gson;
import dto.SimulationManagerDTO;
import dto.WorldDefinitionDTO;
import facade.Facade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainController {
    @FXML private Button ManagementButton;
    @FXML private HBox hboxScene;
    private WorldDefinitionDTO worldDefinitionDTO;
    private Facade facade;
    public final static String BASE_URL = "http://localhost:8080";
    public final static OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private SimulationManagerDTO simulationManagerDTO;
    private void requestSimulationManager() {
        String RESOURCE = "/Server_Web_exploded/simulation-manager";
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
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    simulationManagerDTO = gson.fromJson(jsonResponse, SimulationManagerDTO.class);
                } else {
                    // Handle unsuccessful response here
                }
                response.close();
            }
        });
    }

    public void initialize() {
        ScheduledExecutorService requestSimulationManager = Executors.newSingleThreadScheduledExecutor();
        requestSimulationManager.scheduleAtFixedRate(() -> {
            requestSimulationManager();
        } , 0, 1, TimeUnit.SECONDS);
    }
    private void requestFacade(){
        String RESOURCE = "/Server_Web_exploded/queue-management";
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .get()
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if(response.isSuccessful()){
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    facade = gson.fromJson(jsonResponse, Facade.class);
                }
                else {
                    // Handle unsuccessful response here
                    System.err.println("HTTP Error: " + response.code());
                }
                response.close();
            }

        });

    }

    @FXML
    private void setOnActionAllocationsButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/AllocationsScene.fxml"));
            Parent newSceneRoot = fxmlLoader.load();
            AllocationController allocationController = fxmlLoader.getController();
            hboxScene.getChildren().clear();
            hboxScene.getChildren().add(newSceneRoot);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML private void setOnActionExecutionHistoryButton(){
        try {
            //TODO: i don't need the facade here - just play with the dto...i canot use the facde here...
            //instead of using facade, i need to use the dtos inside him

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/ResultsAdminScene.fxml"));
                Parent resultSceneRoot = fxmlLoader.load();
                ResultAdminController resultController = fxmlLoader.getController();
                //resultController.setFacade(facade);
                // resultController.setSimulationManagerDTO(facade.getSimulationManagerDTO());
                //resultController.setHboxScene(hboxScene);
                resultController.setMainController(this);

                hboxScene.getChildren().clear();
                hboxScene.getChildren().add(resultSceneRoot);


            // Add new scene contents to HBox
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        @FXML
    private void setOnActionManagementButton(){
        try {
            //requestFacade();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/ManagmentScene.fxml"));
            Parent newSceneRoot = fxmlLoader.load();
            ManagementController managementController = fxmlLoader.getController();
            //managementController.setFacade(facade);

            hboxScene.getChildren().clear();
            hboxScene.getChildren().add(newSceneRoot);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
