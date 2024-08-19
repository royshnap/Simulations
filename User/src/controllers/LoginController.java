package controllers;

import jakarta.servlet.http.HttpServletResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.*;
import utils.HttpClientUtil;

import java.io.IOException;

public class LoginController {
    public final static String BASE_URL = "http://localhost:8080";
    public final static OkHttpClient HTTP_CLIENT = HttpClientUtil.HTTP_CLIENT;
    @FXML private TextField usernameTextField;

    @FXML private void onButtonLogin(){
        String RESOURCE = "/Server_Web_exploded/login";
        String username = usernameTextField.getText();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + RESOURCE).newBuilder();
        urlBuilder.addQueryParameter("username", username);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
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
                if(response.isSuccessful()){
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Login Response");
                        alert.setHeaderText(null);
                        try {
                            alert.setContentText("Login successful" + response.body().string());
                            alert.showAndWait();

                            // Get the current stage (login stage)
                            Stage loginStage = (Stage) usernameTextField.getScene().getWindow();

                            // Close the login stage
                            loginStage.close();

                            // Create a new stage for the new scene
                            Stage newStage = new Stage();

                            // Load and set the FXML for the new scene
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/MainScene.fxml"));
                            Parent root = loader.load();
                            Scene newScene = new Scene(root);

                            // Set the new scene on the new stage
                            newStage.setScene(newScene);

                            // Show the new stage
                            newStage.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                else if (response.code() == HttpServletResponse.SC_UNAUTHORIZED) {
                    Platform.runLater(() -> {
                        // Handle unauthorized response here
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Login Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Username already exists. Please enter a different username.");
                        alert.showAndWait();

                    });
                }
                else if (response.code() == HttpServletResponse.SC_CONFLICT) {
                    Platform.runLater(() -> {
                        // Handle unauthorized response here
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Login Error");
                        alert.setHeaderText(null);
                        alert.setContentText("There isn't username that selected");
                        alert.showAndWait();
                    });
                }



            }
        });

    }
}
