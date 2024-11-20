package com.n19.ltmproject.client;

import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.model.Player;
import com.n19.ltmproject.client.model.auth.SessionManager;
import com.n19.ltmproject.client.service.MessageService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.stage.WindowEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Client extends Application {
    private final ServerHandler serverHandler = ServerHandler.getInstance();
    private final MessageService messageService = new MessageService(serverHandler);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            ServerHandler serverHandler = ServerHandler.getInstance();
            serverHandler.init("localhost", 1234);
        } catch (Exception e) {
            System.out.println("Failed to connect to the server: " + e.getMessage());
        }

        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/n19/ltmproject/Login.fxml")));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/n19/ltmproject/css/MainPage.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/n19/ltmproject/css/WaitingRoom.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/n19/ltmproject/css/Achievement.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/n19/ltmproject/css/Login.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/n19/ltmproject/css/SignUp.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/n19/ltmproject/css/Invitation.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/n19/ltmproject/css/ExitBattle.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/n19/ltmproject/css/Result.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/n19/ltmproject/css/GameHistory.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/n19/ltmproject/css/Tutorial.css")).toExternalForm());
            primaryStage.setScene(scene);

            primaryStage.setOnCloseRequest(this::handleClose);

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClose(WindowEvent event) {
        Player currentUser = SessionManager.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("username", currentUser.getUsername());
        messageService.sendRequestNoResponse("logout", params);
    }
}
