package controllers;

import com.google.gson.Gson;
import dto.SimulationDTO;
import dto.WorldDefinitionDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import static controllers.LoginController.BASE_URL;
import static controllers.LoginController.HTTP_CLIENT;

public class MainController {
    @FXML private Button simulationsDeatilsButton;
    @FXML private Button requestsButton;
    @FXML private Button executionButton;
    @FXML private Button resultsButton;
    @FXML private HBox hboxScene;
    private WorldDefinitionDTO worldDefinitionDTO;
    private WorldDefinitionDTO[] simulationsArray;
    private SimulationDTO simulationEnding;

    public void setSimulationHistory(SimulationDTO simulationEnding) {
        this.simulationEnding = simulationEnding;
    }

    private void simulationsRequest(){
        String RESOURCE = "/Server_Web_exploded/simulations-definition";
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .get()
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
                             if (response.isSuccessful()) {
                                 // Handle successful response here
                                 String jsonResponse = response.body().string();
                                 Gson gson = new Gson();
                                 simulationsArray = gson.fromJson(jsonResponse, WorldDefinitionDTO[].class);

//                                 if (simulationsArray != null) {
//                                     for (WorldDefinitionDTO definition : simulationsArray) {
//                                         TreeView<String> treeView = createTreeView(definition);
//                                         Platform.runLater(() -> {
//                                             treeViews.add(treeView);
//                                         });
//                                     }
//                                 }

                             } else {
                                 // Handle unsuccessful response here
                                 System.err.println("HTTP Error: " + response.code());
                             }
                             response.close();
                         }
                     }

        );

    }

    @FXML public void setSimulationsDeatilsButtonOnAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/DetailsScene.fxml"));
            Parent newSceneRoot = fxmlLoader.load();
            SimulationsDetailsController simulationsDetailsController = fxmlLoader.getController();
            simulationsDetailsController.setMainController(this);

            hboxScene.getChildren().clear();
            hboxScene.getChildren().add(newSceneRoot);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setWorldDefinitionDTOFromRequest(String simulationName) {
        for(WorldDefinitionDTO worldDefinitionDTO1 : simulationsArray){
            if(worldDefinitionDTO1.getName().equals(simulationName)){
                worldDefinitionDTO = worldDefinitionDTO1;
                break;
            }
        }
    }

    public WorldDefinitionDTO[] getSimulationsArray() {
        return simulationsArray;
    }

    @FXML public void setOnActionRequestsButton() {

        try {
            simulationsRequest();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/RequestsScene.fxml"));
            Parent newSceneRoot = fxmlLoader.load();
            RequestsController requestsController = fxmlLoader.getController();
            requestsController.setMainController(this);

            hboxScene.getChildren().clear();
            hboxScene.getChildren().add(newSceneRoot);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML public void loadExecutionScene() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/ExecutionScene.fxml"));
            Parent newSceneRoot = fxmlLoader.load();
            ExecutionController executionController = fxmlLoader.getController();
            executionController.setWorldDefinitionDTO(worldDefinitionDTO);
            executionController.setMainController(this);
            hboxScene.getChildren().clear();
            hboxScene.getChildren().add(newSceneRoot);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadNewExecutionSceneFromResultScene() {
        try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/ExecutionScene.fxml"));
                Parent newSceneRoot = fxmlLoader.load();
                ExecutionController newExecutionController = fxmlLoader.getController();
                newExecutionController.setSimulationForHistoryID(simulationEnding.getId());
                newExecutionController.setIsFromResultScene(true);

                newExecutionController.setWorldDefinitionDTO(simulationEnding.getWorldDefinitionDTO());
                newExecutionController.setHboxScene(hboxScene);
                newExecutionController.setMainController(this);


                hboxScene.getChildren().clear();
                hboxScene.getChildren().add(newSceneRoot);
            }

        catch (IOException e){
            e.printStackTrace();
        }

    }

    public void setWorldDefinitionDTO(WorldDefinitionDTO worldDefinitionDTO) {
        //TODO : on the request - put the world definitoin of the name sinualtion
        this.worldDefinitionDTO = worldDefinitionDTO;
    }

    @FXML public void loadResultScene() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/ResultUserScene.fxml"));
            Parent newSceneRoot = fxmlLoader.load();
            ResultUserController executionController = fxmlLoader.getController();
            executionController.setMainController(this);
            hboxScene.getChildren().clear();
            hboxScene.getChildren().add(newSceneRoot);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
