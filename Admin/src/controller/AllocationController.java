package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.RequestDetailsDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static controller.MainController.BASE_URL;
import static controller.MainController.HTTP_CLIENT;

public class AllocationController {
    private ObservableList<RequestDetailsDTO> dataListTable;
    private Map<String, List<RequestDetailsDTO>> responseMap;
    @FXML
    private TableView requestTable;

    public void initialize(){
        requestTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ScheduledExecutorService updateRequestsTable = Executors.newSingleThreadScheduledExecutor();
        dataListTable = FXCollections.observableArrayList();
        updateRequestsTable.scheduleAtFixedRate(() -> {
            requestAllRequests();
        }, 0, 1, TimeUnit.SECONDS);
    }

    @FXML
    private void approveOrCancel(){
        RequestDetailsDTO selectedDTO = (RequestDetailsDTO) requestTable.getSelectionModel().getSelectedItem();
        if (selectedDTO != null && selectedDTO.getRequestStatus().equals("pending")) {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Select an Option");
            dialog.setHeaderText("Choose an option for: " + selectedDTO.getSimulationName());
            ToggleGroup toggleGroup = new ToggleGroup();
            RadioButton approvedBut = new RadioButton("approve");
            RadioButton deniedBut = new RadioButton("deny");
            approvedBut.setToggleGroup(toggleGroup);
            deniedBut.setToggleGroup(toggleGroup);
            VBox content = new VBox(10);
            content.getChildren().addAll(approvedBut, deniedBut);
            dialog.getDialogPane().setContent(content);
            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
            dialog.setResultConverter(buttonType -> {
                if (buttonType == okButtonType) {
                    RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
                    if (selectedRadioButton != null) {
                        String selectedOption = selectedRadioButton.getText();
                        proceesRequest(selectedOption, selectedDTO);
                    }
                }
                return null;
            });
            dialog.showAndWait();


        }
    }

    private void proceesRequest(String option, RequestDetailsDTO selectedRequest){
        String RESOURCE = "/Server_Web_exploded/approved-or-deny";
        RequestBody formBody = new FormBody.Builder()
                .add("option", option)
                .add("requestID",String.valueOf(selectedRequest.getRequestNumber()))
                .add("username",selectedRequest.getUserName())
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .post(formBody)
                .build();
        Call call = MainController.HTTP_CLIENT.newCall(request);
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
                        alert.setTitle("Status update response");
                        alert.setHeaderText(null);
                        try {
                            alert.setContentText(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        alert.showAndWait();
                        response.close();
                    });
                }

            }
        });

    }

    private void requestAllRequests(){
        String RESOURCE = "/Server_Web_exploded/requests-users";
        Request request = new Request.Builder()
                .url(BASE_URL + RESOURCE)
                .get()
                .build();
        Call call = MainController.HTTP_CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    Type mapType = new TypeToken<Map<String, List<RequestDetailsDTO>>>() {}.getType();
                     responseMap = gson.fromJson(jsonResponse, mapType);
                    if(responseMap != null){
                        for(List<RequestDetailsDTO> listRequestDetailsDTO : responseMap.values()){
                            for(RequestDetailsDTO requestDetailsDTO : listRequestDetailsDTO){
                                boolean isFound = false;
                                for(RequestDetailsDTO existingData : dataListTable){
                                    if(requestDetailsDTO.getRequestNumber() == existingData.getRequestNumber()){
                                        //TODO : need to add the still running and ending
                                        isFound = true;
                                        break;
                                    }
                                }
                                if(!isFound){
                                    dataListTable.add(new RequestDetailsDTO(requestDetailsDTO.getUserName(),requestDetailsDTO.getRequestNumber(),
                                            requestDetailsDTO.getSimulationName(), requestDetailsDTO.getAmountOfRunning(),
                                            requestDetailsDTO.getRequestStatus(), requestDetailsDTO.getAmountOfSimulationsRuunning(),
                                            requestDetailsDTO.getAmountOfSimulationEnding(), requestDetailsDTO.getTerminateConditions()));
                                }
                            }
                            Platform.runLater(() ->{


                                requestTable.setItems(dataListTable);


                            });
                            }

                    }
                    response.close();

                }
            }
        });

    }

}
