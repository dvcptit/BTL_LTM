//Known bug:
//Khi trong thoi gian dem nguoc ma click ve trang main thi user con lai van chua back ve
//
//Click invitee click refuse invitation thi back lai main chua load lai dc bang
//
//Tao idGame moi khi countdown kethuc (done)



package com.n19.ltmproject.client.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.n19.ltmproject.client.model.dto.Response;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.service.MessageService;

public class WaitingRoomController {

    private final ServerHandler serverHandler = ServerHandler.getInstance();
    private final MessageService messageService = new MessageService(serverHandler);

    private Stage primaryStage;
    private volatile boolean running = true;
    private Thread listenerThread;
    private volatile boolean isCountdownRunning = true;
    private boolean isInviter; // true nếu là chủ phòng, false nếu là người được mời

    @FXML
    private Label waitingRoomHostName;
    @FXML
    private Label waitingRoomPlayerName;
    @FXML
    private Button inviterButton;
    @FXML
    private Button inviteeButton;
    @FXML
    private Label countdownLabel;
    @FXML
    private Label countdownText;
    @FXML
    private Label LabelWaitReady;
    @FXML
    private Label LabelWaitgoRoom;
    @FXML
    private Button exitButtonWaiting;


    private int countdownTime = 5;
    private long inviterId;
    private long inviteeId;
    private String username;
    private  String opponentName;
    private long gameId;
    private volatile boolean isOpponentExit = false;

    /**
     * Set up the host information.
     *
     * @param inviterId The inviter ID
     * @param inviteeId The invitee ID
     * @param waitingRoomHostName The waiting room host name
     * @param waitingRoomPlayerName The waiting room player name
     */
    public void setUpHost(long currentPlayerId, long opponentPlayerId, String waitingRoomHostName, String waitingRoomPlayerName) {
        this.waitingRoomHostName.setText(waitingRoomHostName);
        this.inviterId = currentPlayerId;
        this.inviteeId = opponentPlayerId;
        this.waitingRoomPlayerName.setText(waitingRoomPlayerName);
        this.isInviter = true;
        this.username = waitingRoomHostName;
        this.opponentName = waitingRoomPlayerName;
    }

    /**
     * Set up the opponent player information.
     *
     * @param inviterId The inviter ID
     * @param inviteeId The invitee ID
     * @param host The host name
     * @param waitingRoomPlayerName The waiting room player name
     */
    public void setUpOpponent(long inviterId, long inviteeId, String host, String waitingRoomPlayerName) {
        this.waitingRoomHostName.setText(host);
        this.inviterId = inviterId;
        this.inviteeId = inviteeId;
        this.waitingRoomPlayerName.setText(waitingRoomPlayerName);
        this.isInviter = false;
        this.username = waitingRoomPlayerName;
        this.opponentName = host;
    }

    /**
     * Set the primary stage.
     *
     * @param stage The primary stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        inviterButton.setText("READY");
        inviteeButton.setText("WAITING");
        startListeningForInvitee();
    }

    /**
     * Start listening for the invitee to join the room.
     * The server will send a message when the invitee joins the room.
     */
    public void startListeningForInvitee() {
        System.out.println("Listening for player 2 to join room...");

        listenerThread = new Thread(() -> {
            try {
                while (running) {
                    System.out.println("Thread in Waiting Room");
                    String serverMessage = serverHandler.receiveMessage();
                    if (serverMessage != null && serverMessage.contains("EXITRESULT")){
                        isOpponentExit=true;
                        handleExit();
                    }
                    else if(serverMessage != null && serverMessage.contains("ClickReady")){
                        handleReady();
                    }
                    else if (serverMessage.contains("New game started! Game ID:")) {
                        handleNewGameStartedMessage(serverMessage);
                    } else if (serverMessage.startsWith("{")) {
                        handleJsonMessage(serverMessage);
                    }
                }
                System.out.println("END THREAD IN WAITING ROOM");
            } catch (IOException ex) {
                System.out.println("Error receiving message from server: " + ex.getMessage());
            }
        });
        listenerThread.start();
    }

    /**
     * Handle the new game started message.
     * The server message should be in the format: "New game started! Game ID: 123"
     * If the message is in the correct format, extract the game ID, start the game, and end listening thread.
     *
     * @param serverMessage The server message
     */
    private void handleNewGameStartedMessage(String serverMessage) {
        String regex = "New game started! Game ID: (\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(serverMessage);

        if (matcher.find()) {
            gameId = Long.parseLong(matcher.group(1));
            System.out.println("Đã gán game ID: " + gameId);

            running = false;
            serverHandler.sendMessage("STOP_LISTENING");
            Platform.runLater(() -> startGame(gameId, inviterId, inviteeId));
        }
    }
    @FXML
    public void ClickReady(ActionEvent event) {
        if(!isInviter){
            HashMap<String, Object> params = new HashMap<>();
            params.put("username",username);
            params.put("opponent", opponentName);
            params.put("userId",inviteeId);
            params.put("opponentId", inviterId);

            Response response =messageService.sendRequestAndReceiveResponse("clickReady", params);
            if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
                inviteeButton.setText("READY");
            } else {
                System.out.println("Invitation failed: " + (response != null ? response.getMessage() : "Unknown error"));
            }
        }
    }
    private void handleReady(){
        Platform.runLater(() -> {
            countdownLabel.setVisible(true);
            countdownText.setVisible(true);
            inviteeButton.setText("READY");
            inviteeButton.setStyle("-fx-background-color: #2BC9FC;");
            if(isInviter){
                LabelWaitReady.setVisible(false);
            }
            startCountdown();
        });
    }

    private void handleExit() {
        isCountdownRunning = false;
        Platform.runLater(() -> {
            stopListening();
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com/n19/ltmproject/MainPage.fxml"));

                Parent MainPageViewParent = loader.load();
                Scene scene = new Scene(MainPageViewParent);

                MainPageController mainPageController = loader.getController();
                mainPageController.setPrimaryStage(primaryStage);
                mainPageController.showLabelCancelMatch();
                primaryStage.setScene(scene);
                mainPageController.setupMainPage();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: Unable to load MainPage.fxml");
            }
        });
    }

    /**
     * Handle the JSON message from the server.
     * The server message should be in the JSON format.
     * If the message is in the correct format, extract the message and update the UI.
     *
     * @param serverMessage The server message
     */
    private void handleJsonMessage(String serverMessage) {
        try {
            Response response = new Gson().fromJson(serverMessage, Response.class);

            if ("SUCCESS".equals(response.getStatus())) {
                updateUIWithJoinMessage(response.getMessage());
            } else if ("REFUSED".equals(response.getStatus())) {
                Platform.runLater(() -> {
                    System.out.println("Player declined the invitation.");
                    try {
                        handleRufuse();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (JsonSyntaxException e) {
            System.out.println("Invalid JSON format: " + e.getMessage());
        }
    }

    /**
     * Update the UI with the join message.
     * The message should be in the format: "[JOINED] User username với playerId 123 đã tham gia phòng với username playerId 123."
     * If the message is in the correct format, extract the usernames and update the UI.
     *
     * @param message The join message
     */
    private void updateUIWithJoinMessage(String message) {
        String regex = "\\[JOINED\\] User (\\w+) với playerId (\\d+) đã tham gia phòng với (\\w+) playerId (\\d+)\\.";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String inviteeUsername = matcher.group(1);
            long inviteePlayerId = Long.parseLong(matcher.group(2));

            this.inviterId = Long.parseLong(matcher.group(4));
            this.inviteeId = inviteePlayerId;

            Platform.runLater(() -> {
                inviteeButton.setText("NOT READY");
                waitingRoomPlayerName.setText(inviteeUsername);
                LabelWaitgoRoom.setVisible(false);
                if(isInviter){
                    LabelWaitReady.setVisible(true);
                }
            });
        } else {
            System.out.println("Could not extract usernames from message.");
        }
    }

    /**
     * Start the countdown for the game to start.
     * If the countdown reaches 0 and still running, start the game.
     * This will be running in a separate thread.
     */
    private void startCountdown() {
        countdownLabel.setText(String.valueOf(countdownTime));
        exitButtonWaiting.setVisible(false);
        stopListening();
        new Thread(() -> {
            while (countdownTime > 0 && isCountdownRunning) {
                try {
                    Thread.sleep(1000);
                    countdownTime--;
                    Platform.runLater(() -> countdownLabel.setText(String.valueOf(countdownTime)));
                    if(isOpponentExit){
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            if (countdownTime==0 && !isOpponentExit) {
                if (isInviter) {
                    sendStartGameCommand();
                } else {
                    Platform.runLater(() -> startGame(gameId, inviterId, inviteeId));
                }
            }
        }).start();
    }


    /**
     * Start the game with the given gameId, currentPlayerId, and opponentPlayerId.
     * If isInviter is true, then currentPlayerId is inviterId and currentPlayerName is the host name
     * else the invitee name
     *
     * @param gameId The game ID
     * @param currentPlayerId The current player ID
     * @param opponentPlayerId The opponent player ID
     */
    private void startGame(long gameId, long currentPlayerId, long opponentPlayerId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/n19/ltmproject/GamePlay.fxml"));
            Parent root = loader.load();

            GamePlayController gamePlayController = loader.getController();
            gamePlayController.setStage(primaryStage);

            // init the game base on isInviter, if isInviter then currentPlayerId is inviterId, and currentPlayerName is host name
            if(isInviter) {
                gamePlayController.initGame(gameId, currentPlayerId, opponentPlayerId, waitingRoomHostName.getText(), waitingRoomPlayerName.getText(), isInviter);
            } else {
                gamePlayController.initGame(gameId, opponentPlayerId, currentPlayerId, waitingRoomPlayerName.getText(), waitingRoomHostName.getText(), isInviter);
            }

            Scene scene = new Scene(root);
            Platform.runLater(() -> {
                primaryStage.setScene(scene);
                primaryStage.setTitle("Game Interface");
                primaryStage.show();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send the start game command to the server.
     * The server will start the game and notify both players.
     * The page will be navigated to the game interface.
     */
    private void sendStartGameCommand() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("player1Id", inviterId);
        params.put("player2Id", inviteeId);

        Response response = messageService.sendRequestAndReceiveResponse("startNewGame", params);

        if ("OK".equals(response.getStatus())) {
            //TODO refactor, unchecked cast should be remove
            gameId = ((Number) ((Map<String, Object>) response.getData()).get("gameId")).longValue();
            System.out.println("New game started with gameId: " + gameId);

            Platform.runLater(() -> startGame(gameId, inviterId, inviteeId));
        } else {
            System.out.println("Failed to start game: " + response.getMessage());
        }
    }

    /**
     * Handle the exit button click event.
     * Stop the countdown and navigate back to the main page.
     *
     * @throws IOException If the main page cannot be loaded
     */

    // click refuse
    void handleRufuse() throws IOException {
        // Dừng đếm ngược khi người dùng nhấn "Exit"
        isCountdownRunning = false;
        stopListening();
        // Điều hướng về MainPage
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/n19/ltmproject/MainPage.fxml"));

        Parent MainPageViewParent = loader.load();
        Scene scene = new Scene(MainPageViewParent);

        MainPageController mainPageController = loader.getController();
        mainPageController.setPrimaryStage(primaryStage);
        mainPageController.showLabelRefuseMatch();
        primaryStage.setScene(scene);
        mainPageController.setupMainPage();
    }
    @FXML
    void ClickExit(ActionEvent e) throws IOException {
        // Chủ phòng
        if(isInviter){
            stopListening();
            if(!isOpponentExit){
                isOpponentExit=true;
                HashMap<String, Object> params = new HashMap<>();
                params.put("username",username);
                params.put("opponent", opponentName);
                params.put("userId",inviterId);
                params.put("opponentId", inviteeId);

                Response response =messageService.sendRequestAndReceiveResponse("exitResult", params);
                if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
                    loadMainPage();
                } else {
                    System.out.println("Invitation failed: " + (response != null ? response.getMessage() : "Unknown error"));
                }
            }
            else {
                loadMainPage();
            }
        }
        else{
            // Người chơi
            stopListening();
            if(!isOpponentExit){
                isOpponentExit=true;
                HashMap<String, Object> params = new HashMap<>();
                params.put("username",username);
                params.put("opponent", opponentName);
                params.put("userId",inviteeId);
                params.put("opponentId", inviterId);
                System.out.println("username "+ opponentName);
                System.out.println("opponent "+ username);
                System.out.println("userId" + inviteeId);
                System.out.println("opponentId "+ inviterId);

                Response response =messageService.sendRequestAndReceiveResponse("exitResult", params);
                if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
                    loadMainPage();
                } else {
                    System.out.println("Invitation failed: " + (response != null ? response.getMessage() : "Unknown error"));
                }
            }
            else {
                loadMainPage();
            }
        }

    }

    private void loadMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/n19/ltmproject/MainPage.fxml"));

            Parent MainPageViewParent = loader.load();
            Scene scene = new Scene(MainPageViewParent);

            MainPageController mainPageController = loader.getController();
            mainPageController.setPrimaryStage(primaryStage);
            primaryStage.setScene(scene);
            mainPageController.setupMainPage();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Unable to load MainPage.fxml");
        }
    }

    /**
     * Handle the refuse button click event.
     * Notify the server that the player refused the invitation and navigate back to the main page.
     * Now this button is deprecated, the current implementation is countdown and navigate to the game play page.
     * Should be handled in future
     */


    /**
     * Stop listening for the invitee to join the room after the invitee has joined or the player has exited.
     */
    private void stopListening() {
        running = false;
        serverHandler.sendMessage("STOP_LISTENING");
    }
}
