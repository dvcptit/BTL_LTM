package com.n19.ltmproject.client.test_request.game.test_logicPage;

import com.n19.ltmproject.client.controller.GamePlayController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class TestGamePlay extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/n19/ltmproject/GamePlay.fxml"));
        Parent root = loader.load();

        // Lấy GamePlayController từ FXMLLoader
        GamePlayController gamePlayController = loader.getController();

        // Tạo một Stage và gọi hàm setStage
        gamePlayController.setStage(primaryStage);

        // Khởi tạo Scene và hiển thị
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Test GamePlay");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
