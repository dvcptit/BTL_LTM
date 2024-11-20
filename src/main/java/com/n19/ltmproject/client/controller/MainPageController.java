package com.n19.ltmproject.client.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.model.auth.SessionManager;
import com.n19.ltmproject.client.model.dto.PlayerHistoryDto;
import com.n19.ltmproject.client.model.dto.Response;
import com.n19.ltmproject.client.model.enums.PlayerStatus;
import com.n19.ltmproject.client.service.MessageService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.n19.ltmproject.client.model.Player;
import javafx.util.Duration;

public class MainPageController {

    @FXML
    public Button inviteButton;
    @FXML
    private Label labelrefusematch;
    @FXML
    private Label labelcancelmatch;
    @FXML
    private TableView<Player> table;
    @FXML
    private TableColumn<Player, String> username;
    @FXML
    private TableColumn<Player, Integer> numberColumn;
    @FXML
    private TableColumn<Player, PlayerStatus> statusColumn;

    @FXML
    private Label mainPageUsername;

    @FXML
    private Button onlineButton;
    @FXML
    private Button inGameButton;
    @FXML
    private Button offlineButton;
    @FXML
    private Label scoreUser;


    private ObservableList<Player> filteredList = FXCollections.observableArrayList(); // Danh sách lọc

    private ObservableList<Player> playerList;
    private Stage primaryStage;
    private final Gson gson = new Gson();
    private final ServerHandler serverHandler = ServerHandler.getInstance();
    private MessageService messageService;
    private volatile boolean running = true;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * This method is used to set up the main page.
     * It will display the current user's username and load all players from the server.
     */
    @FXML
    public void setupMainPage() {
        mainPageUsername.setText(SessionManager.getCurrentUser().getUsername());
        messageService = new MessageService(serverHandler);
        System.out.println("Load bang trong setUp");
        loadPlayers();

        // Add listener to table selection model
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateInviteButtonState(newSelection);
        });
    }

    private void updateInviteButtonState(Player selectedPlayer) {
        if (selectedPlayer != null) {
            PlayerStatus status = selectedPlayer.getStatus();
            // Disable the invite button if the player is in-game or offline
            inviteButton.setDisable(status == PlayerStatus.IN_GAME || status == PlayerStatus.OFFLINE);
        } else {
            // If no player is selected, disable the button
            inviteButton.setDisable(true);
        }
    }

    public void setThread(){
        this.running = true;
        System.out.println("Thread in setThread");
        startListeningForInvite();
    }

    /**
     * This method is used to load all players from the server and display them in the table.
     * It is called when the main page is loaded. And call api to server and get all players
     */
    private void loadPlayers() {
        try {
            Map<String, Object> params = Map.of();
            Response response = messageService.sendRequestAndReceiveResponse("getAllPlayer", params);

            Platform.runLater(() -> {
                if (response != null) {
                    System.out.println("Status từ server: " + response.getStatus());
                    System.out.println("Dữ liệu nhận được: " + response.getData());
                }

                if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
                    List<Player> players = gson.fromJson(new Gson().toJson(response.getData()), new TypeToken<List<Player>>() {}.getType());

                    // Get the current user's username
                    String currentUsername = SessionManager.getCurrentUser().getUsername(); // Assuming this method exists

                    // Filter out the current player
                    List<Player> otherPlayers = players.stream()
                            .filter(player -> !player.getUsername().equals(currentUsername)) // Exclude the current player
                            .collect(Collectors.toList());

                    playerList = FXCollections.observableArrayList(otherPlayers);
                    numberColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(cellData.getValue()) + 1));
                    username.setCellValueFactory(new PropertyValueFactory<>("username"));
                    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

                    table.setItems(playerList);
                    System.out.println("Tạo bảng thành công");
                    table.setFocusTraversable(false);
                    table.getSelectionModel().clearSelection();
                } else {
                    System.out.println("Failed to get players: " + (response != null ? response.getMessage() : "Unknown error"));
                }

                // Retrieve player history
                HashMap<String, Object> param2 = new HashMap<>();
                param2.put("inviterId", SessionManager.getCurrentUser().getId());
                Response response2 = messageService.sendRequestAndReceiveResponse("getPlayerHistoryById", param2);
                if (response2 != null && "OK".equalsIgnoreCase(response2.getStatus())) {
                    PlayerHistoryDto playerHistory = gson.fromJson(new Gson().toJson(response2.getData()), PlayerHistoryDto.class);
                    scoreUser.setText("Score: " + playerHistory.getTotalPoints());
                    setThread();
                } else {
                    System.out.println("Failed to get player history: " + (response2 != null ? response2.getMessage() : "Unknown error"));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Phương thức lọc theo trạng thái
    private void filterPlayers(PlayerStatus status) {
        filteredList.setAll(playerList.filtered(player -> player.getStatus().equals(status)));
        table.setItems(filteredList); // Hiển thị danh sách lọc
        table.getSelectionModel().clearSelection();
    }

    @FXML
    public void ClickOnlineButton(ActionEvent event) {
        filterPlayers(PlayerStatus.ONLINE);
    }

    @FXML
    public void ClickInGameButton(ActionEvent event) {
        filterPlayers(PlayerStatus.IN_GAME);
    }

    @FXML
    public void ClickOfflineButton(ActionEvent event) {
        filterPlayers(PlayerStatus.OFFLINE);
    }


    /**
     * This method is used to start a new thread to listen for server messages.
     * Which in this case is listening for invitation from other players.
     */
    public void startListeningForInvite() {
        System.out.println("Start thread");
        new Thread(this::listenForServerMessages).start();
    }

    /**
     * This method is used to listen for server messages.
     * If the server sends an invitation message, it will handle the invitation.
     */
    private void listenForServerMessages() {
        try {
            while (this.running) {
                if (!this.running) {
                    break;
                }
                String serverMessage = serverHandler.receiveMessage();
                System.out.println("Received from server: " + serverMessage);

                if (serverMessage != null && serverMessage.contains("INVITATION")) {
                    Platform.runLater(() -> handleInvitation(serverMessage));
                }
            }
            System.out.println("END THREAD");
        } catch (IOException ex) {
            System.out.println("Error receiving message from server: " + ex.getMessage());
        }
    }

    /**
     * This method is used to handle the invitation message from the server.
     * It will stop the current thread and show the invitation dialog.
     *
     * @param serverMessage The invitation message from the server
     */
    private void handleInvitation(String serverMessage) {
        this.running = false;
        serverHandler.sendMessage("STOP_LISTENING");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/n19/ltmproject/Invitation.fxml"));
            Scene invitationScene = new Scene(loader.load());
            InvitationController invitationController = loader.getController();
            invitationController.setPrimaryStage(primaryStage);

            String userInvite = serverMessage.split(" ")[1];
            long inviterId = Long.parseLong(serverMessage.split(" ")[4]);
            long inviteeId = SessionManager.getCurrentUser().getId();

            HashMap<String, Object> params = new HashMap<>();
            params.put("inviterId", inviterId);
            Response response = messageService.sendRequestAndReceiveResponse("getPlayerHistoryById", params);
            if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
                PlayerHistoryDto playerHistory = gson.fromJson(new Gson().toJson(response.getData()), PlayerHistoryDto.class);
                invitationController.SetUpWinLossDrawInviter(playerHistory.getTotalGames(),playerHistory.getTotalPoints(),playerHistory.getWins(),playerHistory.getDraws(),playerHistory.getLosses());
            } else {
                System.out.println("Failed to get player history: " + (response != null ? response.getMessage() : "Unknown error"));
            }
            String inviterProfile = "Default";
            invitationController.setUpInvitation(userInvite, inviterId, inviteeId, inviterProfile);
            primaryStage.setScene(invitationScene);
            primaryStage.setTitle("Giao diện phòng chờ");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to handle the logout action.
     * It will send a request to the server to log out the current user.
     * If the logout is successful, it will clear the session and show the login page.
     *
     * @param e The action event
     * @throws IOException If an I/O error occurs
     */
    public void ClickLogout(ActionEvent e) throws IOException {
        this.running = false;
        serverHandler.sendMessage("STOP_LISTENING");
        Player currentUser = SessionManager.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("username", currentUser.getUsername());
        Response response = messageService.sendRequestAndReceiveResponse("logout", params);

        if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
            SessionManager.clearSession();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/n19/ltmproject/Login.fxml"));
            Parent loginViewParent = loader.load();
            Scene scene = new Scene(loginViewParent);
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } else {
            System.out.println("Logout failed");
        }
    }

    /**
     * This method is used to handle the invite player action.
     * It will send an invitation to the selected player through server.
     * The flow is as follows:
     * 1. Get the selected player from the table
     * 2. Send an invitation to the selected player
     * 3. If the invitation is successful, stop the current thread and move to the waiting room
     * 4. If the invitation fails, show an error message
     *
     * @param event The action event
     * @throws IOException If an I/O error occurs
     */
    public void ClickInvitePlayer(ActionEvent event) throws IOException {
        Player selectedPlayer = table.getSelectionModel().getSelectedItem();
        inviteButton.setDisable(false);

        if (selectedPlayer != null) {
            long inviterId = SessionManager.getCurrentUser().getId();
            long inviteeId = selectedPlayer.getId();
            String inviterName = SessionManager.getCurrentUser().getUsername();
            String inviteeName = selectedPlayer.getUsername();

            Map<String, Object> params = new HashMap<>();
            params.put("inviter", inviterName);
            params.put("inviterId", inviterId);
            params.put("invitee", inviteeName);
            params.put("inviteeId", inviteeId);

            Response response = messageService.sendRequestAndReceiveResponse("invitation", params);

            if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
                this.running = false;
                serverHandler.sendMessage("STOP_LISTENING");
                moveToWaitingRoom(selectedPlayer);
            } else {
                System.out.println("Invitation failed: " + (response != null ? response.getMessage() : "Unknown error"));
            }
        } else {
            System.out.println("Please choose a player to invite!");
        }
    }

    /**
     * This method is used to move to the waiting room.
     * It will stop the current thread and show the waiting room.
     *
     * @param selectedPlayer The selected player
     */
    private void moveToWaitingRoom(Player selectedPlayer) {
        this.running = false;
        serverHandler.sendMessage("STOP_LISTENING");
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com/n19/ltmproject/WaitingRoom.fxml"));
                Parent waitViewParent = loader.load();

                WaitingRoomController waitingRoomController = loader.getController();
                waitingRoomController.setPrimaryStage(primaryStage);
                waitingRoomController.setUpHost(
                        SessionManager.getCurrentUser().getId(),
                        selectedPlayer.getId(),
                        SessionManager.getCurrentUser().getUsername(),
                        selectedPlayer.getUsername()
                );

                Scene scene = new Scene(waitViewParent);
                primaryStage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * This method is used to handle the refresh action.
     * It will stop the current thread and refresh the main page.
     *
     * @param e The action event
     */
    public void ClickGameRule(ActionEvent e) throws IOException {
        this.running = false;
        serverHandler.sendMessage("STOP_LISTENING");

        FXMLLoader loader = new FXMLLoader();
        //TODO change path
        loader.setLocation(getClass().getResource("/com/n19/ltmproject/Tutorial.fxml"));

        Parent TutorialViewParent = loader.load();
        Scene scene = new Scene(TutorialViewParent);

        TutorialController tutorialController = loader.getController();
        tutorialController.setPrimaryStage(primaryStage);

        primaryStage.setScene(scene);
    }

    /**
     * This method is used to handle the leaderboard action.
     * It will stop the current thread and show the leaderboard page.
     *
     * @param e The action event
     */
    public void ClickLeaderBoard(ActionEvent e) throws IOException {
        this.running = false;
        serverHandler.sendMessage("STOP_LISTENING");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/n19/ltmproject/LeaderBoard.fxml"));
        Parent LeaderBoardViewParent = loader.load();
        Scene scene = new Scene(LeaderBoardViewParent);

        LeaderBoardController boardController = loader.getController();
        boardController.setPrimaryStage(primaryStage);

        primaryStage.setScene(scene);
    }

    public void ClickHistory(ActionEvent actionEvent) throws IOException {
        this.running = false;
        serverHandler.sendMessage("STOP_LISTENING");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/n19/ltmproject/GameHistory.fxml"));
        Parent GameHistoryViewParent = loader.load();
        Scene scene = new Scene(GameHistoryViewParent);

        GameHistoryController boardController = loader.getController();
        boardController.setPrimaryStage(primaryStage);

        primaryStage.setScene(scene);
    }

    /**
     * This method is used to handle the refresh action.
     * It will stop the current thread and refresh the main page.
     *
     * @param actionEvent The action event
     */
    public void ClickRefresh(ActionEvent actionEvent) {
        this.running = false;
        serverHandler.sendMessage("STOP_LISTENING");
        setupMainPage();
    }

    public void showLabelRefuseMatch(){
        labelrefusematch.setVisible(true);

        // Tạo Timeline để ẩn Label sau 3 giây
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(2),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        labelrefusematch.setVisible(false); // Ẩn Label sau 5 giây
                    }
                }
        ));

        timeline.setCycleCount(1); // Chạy 1 lần duy nhất
        timeline.play(); // Bắt đầu Timeline
    }

    public void showLabelCancelMatch(){
        labelcancelmatch.setVisible(true);

        // Tạo Timeline để ẩn Label sau 3 giây
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(2),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        labelcancelmatch.setVisible(false); // Ẩn Label sau 5 giây
                    }
                }
        ));

        timeline.setCycleCount(1); // Chạy 1 lần duy nhất
        timeline.play(); // Bắt đầu Timeline
    }
}
