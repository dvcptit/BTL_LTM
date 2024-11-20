package com.n19.ltmproject.client.controller;

import com.google.gson.Gson;
import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.model.auth.SessionManager;
import com.n19.ltmproject.client.model.dto.Response;
import com.n19.ltmproject.client.service.MessageService;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class ResultController {

    private final ServerHandler serverHandler = ServerHandler.getInstance();
    private final MessageService messageService = new MessageService(serverHandler);
    @Setter
    private volatile boolean running = true;
    @Setter
    private volatile boolean isOpponentExit = false;

    @FXML
    private Label resultLabel;

    @FXML
    private Label opponentExitLabel;

    @FXML
    private Label opponentExitMessageLabel;

    @FXML
    private Label scoreLabel;
    @FXML
    private Button playAgainButton;
    @FXML
    private Label playAgainPane;
    @FXML
    private Label playAgainMessage;
    @FXML
    private Button playAgainAcceptButton;
    @FXML
    private Button playAgainRefuseButton;

    private boolean isWinner;
    private boolean isDraw;
    private String opponent;
    private long gameId;
    private Stage primaryStage;
    @FXML
    private Label currentPlayerNameLabel;
    @FXML
    private Label opponentPlayerNameLabel;

    private long currentPlayerId;
    private long opponentPlayerId;
    private String username;
    private String opponentName;

    @FXML
    private TextField chatInput;

    @FXML
    private VBox chatBox;

    // Định dạng thời gian: giờ:phút
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        chatInput.setOnKeyPressed(this::handleKeyPress);
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleSendChat();
        }
    }

    public void setResults(String results, String score, boolean isWinner, boolean isDraw,
            long currentPlayerId, long opponentPlayerId) {
        this.isWinner = isWinner;
        this.isDraw = isDraw;
        scoreLabel.setText(score);
        resultLabel.setText(results);
        this.currentPlayerId = currentPlayerId;
        this.opponentPlayerId = opponentPlayerId;
    }

    public void setUpPlayerID(long playerId, long opponentId, String username, String opponentName) {
        this.currentPlayerId = playerId;
        this.opponentPlayerId = opponentId;
        this.username = username;
        this.opponentName = opponentName;
    }

    public void setPlayerNames(String currentPlayerName, String opponentPlayerName) {
        currentPlayerNameLabel.setText(currentPlayerName);
        opponentPlayerNameLabel.setText(opponentPlayerName);
    }

    void setOpponentExit() {
        isOpponentExit = true;
        playAgainButton.setVisible(false);
        opponentExitLabel.setVisible(true);
    }

    @FXML
    private void handleExit() {
        stopListening();
        if (!isOpponentExit) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("username", username);
            params.put("opponent", opponentName);
            params.put("userId", currentPlayerId);
            params.put("opponentId", opponentPlayerId);

            Response response = messageService.sendRequestAndReceiveResponse("exitResult", params);
            if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
                loadMainPage();
            } else {
                System.out
                        .println("Invitation failed: " + (response != null ? response.getMessage() : "Unknown error"));
            }
        } else {
            loadMainPage();
        }
    }

    @FXML
    private void handlePlayAgain() {
        stopListening();
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("opponent", opponentName);
        params.put("userId", currentPlayerId);
        params.put("opponentId", opponentPlayerId);

        Response response = messageService.sendRequestAndReceiveResponse("playagain", params);
        if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
            moveToWaitingRoom();
        } else {
            System.out.println("Invitation failed: " + (response != null ? response.getMessage() : "Unknown error"));
        }
    }

    public void startListeningForServer() {
        System.out.println("Listening for player1 quit game ...");

        new Thread(() -> {
            try {
                while (running) {
                    if (!this.running) {
                        break;
                    }
                    System.out.println("Thread in Result");
                    String serverMessage = serverHandler.receiveMessage();
                    System.out.println("THREAD: " + serverMessage);
                    if (serverMessage.contains("EXITRESULT")) {
                        isOpponentExit = true;
                        playAgainButton.setVisible(false);
                        opponentExitLabel.setVisible(true);
                    }
                    if (serverMessage.contains("PlayAgain")) {
                        playAgainPane.setVisible(true);
                        playAgainMessage.setVisible(true);
                        playAgainAcceptButton.setVisible(true);
                        playAgainRefuseButton.setVisible(true);
                        System.out.println("Bạn nhận dc lời mời chs lại");
                    }
                    if (serverMessage.contains("Send message from user1:")) {
                        parseServerChatMessage(serverMessage);
                    }
                }
                System.out.println("END THREAD IN RESULT");
            } catch (IOException ex) {
                System.out.println("Error receiving message from server: " + ex.getMessage());
            }
        }).start();
    }

    private void parseServerChatMessage(String serverMessage) {
        // The request is in the format: "currentPlayerId-opponentPlayerId-message"
        String playerIdAndMesStr = serverMessage.substring(25);
        String[] messages = playerIdAndMesStr.split("\\-");
        int senderId = Integer.parseInt(messages[0]);
        int receiverId = Integer.parseInt(messages[1]);
        String message = messages[2];
        System.out.println("message from user2 " + message);
        renderChatMessage(senderId, receiverId, message);
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        startListeningForServer();
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

    private void stopListening() {
        running = false;
        serverHandler.sendMessage("STOP_LISTENING");
    }

    private void sendAcceptanceToServer(String inviterPlayer, String currentAccepterPlayer, long inviterId,
            long inviteeId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", currentAccepterPlayer);
        params.put("inviterName", inviterPlayer);
        params.put("inviterId", inviterId);
        params.put("inviteeId", inviteeId);

        messageService.sendRequestNoResponse("userJoinedRoom", params);
    }

    public void ClickAcceptPlayAgain(ActionEvent e) throws IOException {
        stopListening();
        sendAcceptanceToServer(opponentName, username, opponentPlayerId, currentPlayerId);
        loadWaitingRoom();
    }

    public void ClickRefusePlayAgain(ActionEvent e) throws IOException {
        stopListening();
        HashMap<String, Object> params = new HashMap<>();

        params.put("invitee", username);
        params.put("inviter", opponentName);
        params.put("inviterId", opponentPlayerId);
        params.put("inviteeId", currentPlayerId);

        Response response = messageService.sendRequestAndReceiveResponse("refuseInvitation", params);
        if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
            loadMainPage();

        } else {
            System.out.println("REFUSED failed: " + (response != null ? response.getMessage() : "Unknown error"));
        }
    }

    // đây là sang trang waitinh của người thách đấu lại
    private void moveToWaitingRoom() {
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
                        currentPlayerId,
                        opponentPlayerId,
                        username,
                        opponentName);

                Scene scene = new Scene(waitViewParent);
                primaryStage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // đây là sang trang waitng của người dc mời
    private void loadWaitingRoom() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/n19/ltmproject/WaitingRoom.fxml"));

        Parent WaitingRoomParent = loader.load();
        Scene scene = new Scene(WaitingRoomParent);

        WaitingRoomController waitingRoomController = loader.getController();
        waitingRoomController.setPrimaryStage(primaryStage);
        waitingRoomController.setUpOpponent(
                opponentPlayerId,
                currentPlayerId,
                opponentName,
                username);

        primaryStage.setScene(scene);
    }

    @FXML
    private void handleSendChat() {
        String message = chatInput.getText();
        if (!message.isEmpty()) {
            sendChatToServer(currentPlayerId, opponentPlayerId, message);
            renderChatMessage(this.currentPlayerId, this.opponentPlayerId, message);
        }
    }

    private void sendChatToServer(long currentPlayerId, long opponentPlayerId, String message) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("currentPlayerId", currentPlayerId + "");
        params.put("opponentPlayerId", opponentPlayerId + "");
        params.put("message", message);

        messageService.sendRequestNoResponse("sendChatMessage", params);
    }

    private void renderChatMessage(long currentPlayerId, long opponentPlayerId, String message) {
        // Lấy thời gian hiện tại
        String currentTime = LocalDateTime.now().format(TIME_FORMATTER);

        // Tạo nội dung tin nhắn kèm thời gian
        String messageWithTime;
        if (currentPlayerId == SessionManager.getCurrentUser().getId()) {
            messageWithTime = "[" + currentTime + "] " + username + ": " + message;
        } else {
            messageWithTime = "[" + currentTime + "] " + opponentName + ": " + message;
        }

        // Đảm bảo việc cập nhật giao diện xảy ra trên UI thread
        Platform.runLater(() -> {
            // Tạo một Label mới cho tin nhắn
            Label messageLabel = new Label(messageWithTime);
            messageLabel.setMaxWidth(350); // Đặt kích thước tối đa cho tin nhắn
            messageLabel.setWrapText(true); // Cho phép ngắt dòng nếu tin nhắn quá dài

            // Tạo một HBox chứa tin nhắn và căn chỉnh dựa trên người gửi
            HBox messageBox = new HBox(messageLabel);
            messageBox.setPrefWidth(510);

            if (currentPlayerId == SessionManager.getCurrentUser().getId()) {
                messageBox.setAlignment(Pos.CENTER_RIGHT);
                messageLabel.setStyle(
                        "-fx-text-fill: white; -fx-background-color: #4CAF50; -fx-padding: 10; -fx-background-radius: 10;");
            } else {
                messageBox.setAlignment(Pos.CENTER_LEFT);
                messageLabel.setStyle(
                        "-fx-text-fill: white; -fx-background-color: #4A4A4A; -fx-padding: 10; -fx-background-radius: 10;");
            }

            // Thêm tin nhắn vào VBox chatBox
            chatBox.getChildren().add(messageBox);

            // Làm trống ô nhập sau khi gửi tin nhắn
            chatInput.clear();
        });
    }
}
