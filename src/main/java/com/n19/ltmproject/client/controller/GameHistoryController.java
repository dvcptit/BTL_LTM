package com.n19.ltmproject.client.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.model.Player;
import com.n19.ltmproject.client.model.auth.SessionManager;
import com.n19.ltmproject.client.model.dto.GameHistoryDto;
import com.n19.ltmproject.client.model.dto.Response;
import com.n19.ltmproject.client.service.MessageService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class GameHistoryController implements Initializable {
    private ObservableList<GameHistoryDto> gameHistoriesList;

    @FXML
    private TableView<GameHistoryDto> gameHistoryTable;

    @FXML
    private TableColumn<GameHistoryDto, Integer> numberColumn;

    @FXML
    private TableColumn<GameHistoryDto, String> player1NameColumn;

    @FXML
    private TableColumn<GameHistoryDto, Long> player1ScoreColumn;

    @FXML
    private TableColumn<GameHistoryDto, String> player2NameColumn;

    @FXML
    private TableColumn<GameHistoryDto, Long> player2ScoreColumn;

    @FXML
    private TableColumn<GameHistoryDto, String> startTimeColumn;

    @FXML
    private TableColumn<GameHistoryDto, String> endTimeColumn;

    private final Gson gson = new Gson();
    private final ServerHandler serverHandler = ServerHandler.getInstance();
    private final MessageService messageService = new MessageService(serverHandler);

    private Stage primaryStage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadGameHistory();
    }

    private void loadGameHistory() {
        try {
            Player currentUser = SessionManager.getCurrentUser();

            Map<String, Object> params = Map.of("playerId", currentUser.getId() + "");
            Response response = messageService.sendRequestAndReceiveResponse("getAllGameData", params);
            System.out.println(response);

            Platform.runLater(() -> {
                if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
                    List<GameHistoryDto> gameHistoriesDto = gson.fromJson(new Gson().toJson(response.getData()), new TypeToken<List<GameHistoryDto>>() {}.getType());
                    gameHistoriesList = FXCollections.observableArrayList(gameHistoriesDto);

                    numberColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(gameHistoryTable.getItems().indexOf(cellData.getValue()) + 1));
                    player1NameColumn.setCellValueFactory(new PropertyValueFactory<>("player1Name"));
                    player1ScoreColumn.setCellValueFactory(new PropertyValueFactory<>("player1Score"));
                    player2NameColumn.setCellValueFactory(new PropertyValueFactory<>("player2Name"));
                    player2ScoreColumn.setCellValueFactory(new PropertyValueFactory<>("player2Score"));
                    startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
                    endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));

                    gameHistoryTable.setItems(gameHistoriesList);
                    gameHistoryTable.setFocusTraversable(false);
                    gameHistoryTable.getSelectionModel().clearSelection();

                    gameHistoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                        if (newSelection != null) {
                            int rowIndex = gameHistoryTable.getItems().indexOf(newSelection) + 1; // Lấy số thứ tự của dòng (bắt đầu từ 1)
                            System.out.println("Selected row number: " + rowIndex);
                        }
                    });
                } else {
                    System.out.println("Failed to get game history: " + (response != null ? response.getMessage() : "Unknown error"));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleBackToHome(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/n19/ltmproject/MainPage.fxml"));
        Parent root = loader.load();

        MainPageController mainpageController = loader.getController();
        mainpageController.setPrimaryStage(primaryStage);
        primaryStage.setScene(new Scene(root));
        mainpageController.setupMainPage();
    }
}
