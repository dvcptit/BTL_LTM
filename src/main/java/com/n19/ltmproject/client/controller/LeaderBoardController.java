package com.n19.ltmproject.client.controller;
// GET BXH
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.model.dto.PlayerHistoryDto;
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

public class LeaderBoardController implements Initializable {

    private ObservableList<PlayerHistoryDto> playerList;
    private final Gson gson = new Gson();
    private final ServerHandler serverHandler = ServerHandler.getInstance();
    private final MessageService messageService = new MessageService(serverHandler);
    private Stage primaryStage;

    @FXML
    private TableView<PlayerHistoryDto> rankBoard;

    @FXML
    private TableColumn<PlayerHistoryDto, String> nameColumn;

    @FXML
    private TableColumn<PlayerHistoryDto, String> matchColumn;

    @FXML
    private TableColumn<PlayerHistoryDto, Integer> rankColumn;

    @FXML
    private TableColumn<PlayerHistoryDto, Integer> winColumn;

    @FXML
    private TableColumn<PlayerHistoryDto, Integer> drawColumn;

    @FXML
    private TableColumn<PlayerHistoryDto, Integer> lossColumn;

    @FXML
    private TableColumn<PlayerHistoryDto, Integer> pointColumn;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void clickHome(ActionEvent e) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/n19/ltmproject/MainPage.fxml"));

        Parent MainPageViewParent = loader.load();
        Scene scene = new Scene(MainPageViewParent);

        MainPageController mainPageController = loader.getController();
        mainPageController.setPrimaryStage(primaryStage);

        primaryStage.setScene(scene);
        mainPageController.setupMainPage();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadPlayers();
    }

    private void loadPlayers() {
        try {
            Map<String, Object> params = Map.of();
            Response response = messageService.sendRequestAndReceiveResponse("getAllPlayerHistory", params);
            System.out.println(response);

            Platform.runLater(() -> {
                if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
                    List<PlayerHistoryDto> playerHistoriesDto = gson.fromJson(new Gson().toJson(response.getData()), new TypeToken<List<PlayerHistoryDto>>() {}.getType());

                    playerList = FXCollections.observableArrayList(playerHistoriesDto);

                    rankColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(rankBoard.getItems().indexOf(cellData.getValue()) + 1));
                    nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
                    matchColumn.setCellValueFactory(new PropertyValueFactory<>("totalGames"));
                    winColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));
                    drawColumn.setCellValueFactory(new PropertyValueFactory<>("draws"));
                    lossColumn.setCellValueFactory(new PropertyValueFactory<>("losses"));
                    pointColumn.setCellValueFactory(new PropertyValueFactory<>("totalPoints"));

                    rankBoard.setItems(playerList);
                    rankBoard.setFocusTraversable(false);
                    rankBoard.getSelectionModel().clearSelection();

                    rankBoard.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                        if (newSelection != null) {
                            System.out.println("Selected items: " + newSelection.getUsername());
                        }
                    });
                } else {
                    System.out.println("Failed to get players: " + (response != null ? response.getMessage() : "Unknown error"));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
