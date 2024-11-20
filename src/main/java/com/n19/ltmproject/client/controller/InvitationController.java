package com.n19.ltmproject.client.controller;

import java.io.IOException;
import java.util.HashMap;

import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.model.auth.SessionManager;
import com.n19.ltmproject.client.model.dto.Response;
import com.n19.ltmproject.client.service.MessageService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Setter;

public class InvitationController {

    private final ServerHandler serverHandler = ServerHandler.getInstance();
    private final MessageService messageService = new MessageService(serverHandler);
    private volatile boolean running = true;
    @Setter
    private Stage primaryStage;

    @FXML
    private Button inviter;

    @FXML
    private Label invitationProfile;

    @FXML
    private Label countdownInvitation;
    @FXML
    private Button LabelTotalGames;
    @FXML
    private Button LabelTotalPoint;
    @FXML
    private Button LabelWins;
    @FXML
    private Button LabelDraws;
    @FXML
    private Button LabelLosses;

    private String inviterName;
    private long inviterId;
    private long inviteeId;
    private int invitationCountdownSeconds = 10;
    private volatile boolean isCountdownRunning = true;
    private volatile boolean isOpponentExit = false;


    /**
     * Set up the invitation information.
     *
     * @param inviterName The inviter name
     * @param inviterId The inviter ID
     * @param inviteeId The invitee ID
     * @param invitationProfile The invitation profile
     */
    public void setUpInvitation(String inviterName, long inviterId, long inviteeId, String invitationProfile) {
        this.inviterName = inviterName;
        this.inviterId = inviterId;
        this.inviteeId = inviteeId;
        this.inviter.setText(inviterName.toUpperCase() + " INVITE YOU");
        this.invitationProfile.setText(invitationProfile);
        createReturnToMainPageTimeline(inviterName, inviterId, inviteeId);
        startListeningForServer();
    }
    public void SetUpWinLossDrawInviter(long totalgames, long totalpoints,long wins, long draw, long loss){
        this.LabelTotalGames.setText("TotalGame: " + totalgames);
        this.LabelTotalPoint.setText("TotalPoint: " + totalpoints);
        this.LabelWins.setText("Win: " + wins);
        this.LabelDraws.setText("Draw: " + draw);
        this.LabelLosses.setText("Loss: " + loss);
    }

    public void startListeningForServer() {
        System.out.println("Listening for player1 quit game ...");

        new Thread(() -> {
            try {
                while (running) {
                    if (!this.running) {
                        break;
                    }
                    System.out.println("Thread in Invitation");
                    String serverMessage = serverHandler.receiveMessage();
                    System.out.println("THREAD: " + serverMessage);
                    if (serverMessage != null && serverMessage.contains("EXITRESULT")){
                        isOpponentExit=true;
                        handleExit();
//                        playAgainButton.setVisible(false);
//                        opponentExitLabel.setVisible(true);
                    }
                }
                System.out.println("END THREAD IN INVITATION");
            } catch (IOException ex) {
                System.out.println("Error receiving message from server: " + ex.getMessage());
            }
        }).start();
    }

    /**
     * Create a timeline to return to the main page after the countdown.
     *
     * @param userInvite The user invite
     * @param inviterId The inviter ID
     * @param inviteeId The invitee ID
     */
    private void createReturnToMainPageTimeline(String userInvite, long inviterId, long inviteeId) {
        countdownInvitation.setText(String.valueOf(invitationCountdownSeconds));
        startCountdown(userInvite, inviterId, inviteeId);
    }

    /**
     * Start the countdown for the invitation.
     * If the countdown reaches 0 and still running, send a refusal to the server and move to the main page.
     * This will be running in a separate thread.
     *
     * @param userInvite The user invite
     * @param inviterId The inviter ID
     * @param inviteeId The invitee ID
     */
    private void startCountdown(String userInvite, long inviterId, long inviteeId) {
        new Thread(() -> {
            while (invitationCountdownSeconds > 0 && isCountdownRunning) {
                try {
                    Thread.sleep(1000);
                    invitationCountdownSeconds--;
                    Platform.runLater(() -> countdownInvitation.setText(String.valueOf(invitationCountdownSeconds)));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (isCountdownRunning && !isOpponentExit) {
                stopListening();
                sendRefusalAndMoveToMainPage(userInvite, inviterId, inviteeId);
            }
        }).start();
    }

    /**
     * Send a refusal to the server so server can notify the inviter and move to the main page.
     *
     * @param userInvite The user invite
     * @param inviterId The inviter ID
     * @param inviteeId The invitee ID
     */

    private void sendRefusalAndMoveToMainPage(String userInvite, long inviterId, long inviteeId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("invitee", SessionManager.getCurrentUser().getUsername());
        params.put("inviter", userInvite);
        params.put("inviterId", inviterId);
        params.put("inviteeId", inviteeId);

        Response response = messageService.sendRequestAndReceiveResponse("refuseInvitation", params);
        if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
            Platform.runLater(() -> {
                try {
                    moveToMainPage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            System.out.println("REFUSED failed: " + (response != null ? response.getMessage() : "Unknown error"));
        }
    }

    /**
     * Accept the invitation, notify server that player accepted invitation and move to the waiting room.
     *
     * @param e The action event
     * @throws IOException If the waiting room cannot be loaded
     */
    public void ClickAccept(ActionEvent e) throws IOException {
        isCountdownRunning = false;
        stopListening();
        sendAcceptanceToServer(this.inviterName, SessionManager.getCurrentUser().getUsername(), inviterId, inviteeId);
        loadWaitingRoom();
    }

    /**
     * Send the acceptance to the server, and the server will notify the inviter player.
     *
     * @param inviterPlayer The inviter player
     * @param currentAccepterPlayer The current accepter player
     * @param inviterId The inviter ID
     * @param inviteeId The invitee ID
     */
    private void sendAcceptanceToServer(String inviterPlayer, String currentAccepterPlayer, long inviterId, long inviteeId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", currentAccepterPlayer);
        params.put("inviterName", inviterPlayer);
        params.put("inviterId", inviterId);
        params.put("inviteeId", inviteeId);

        messageService.sendRequestNoResponse("userJoinedRoom", params);
    }

    /**
     * Load the waiting room after accepting the invitation.
     *
     * @throws IOException If the waiting room cannot be loaded
     */
    private void loadWaitingRoom() throws IOException {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com/n19/ltmproject/WaitingRoom.fxml"));
                Parent waitViewParent = loader.load();

                WaitingRoomController waitingRoomController = loader.getController();
                waitingRoomController.setPrimaryStage(primaryStage);
                waitingRoomController.setUpOpponent(
                this.inviterId,
                SessionManager.getCurrentUser().getId(),
                this.inviterName,
                SessionManager.getCurrentUser().getUsername()
        );

                Scene scene = new Scene(waitViewParent);
                primaryStage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("/com/n19/ltmproject/WaitingRoom.fxml"));
//
//        Parent WaitingRoomParent = loader.load();
//        Scene scene = new Scene(WaitingRoomParent);
//
//        WaitingRoomController waitingRoomController = loader.getController();
//        waitingRoomController.setPrimaryStage(primaryStage);
//        waitingRoomController.setUpOpponent(
//                this.inviterId,
//                SessionManager.getCurrentUser().getId(),
//                this.inviterName,
//                SessionManager.getCurrentUser().getUsername()
//        );
//
//        primaryStage.setScene(scene);
    }

    /**
     * Refuse the invitation, notify server that player refused the invitation and move to the main page.
     *
     * @param e The action event
     * @throws IOException If the main page cannot be loaded
     */
    public void ClickRefuse(ActionEvent e) throws IOException {
        isCountdownRunning = false;
        stopListening();
        HashMap<String, Object> params = new HashMap<>();

        params.put("invitee", SessionManager.getCurrentUser().getUsername());
        params.put("inviter", inviterName);
        params.put("inviterId", inviterId);
        params.put("inviteeId", inviteeId);

        Response response = messageService.sendRequestAndReceiveResponse("refuseInvitation", params);
        if (response != null && "OK".equalsIgnoreCase(response.getStatus())) {
            moveToMainPage();

        } else {
            System.out.println("REFUSED failed: " + (response != null ? response.getMessage() : "Unknown error"));
        }
    }

    /**
     * Move to the main page.
     *
     * @throws IOException If the main page cannot be loaded
     */
    private void moveToMainPage() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/n19/ltmproject/MainPage.fxml"));

        Parent MainPageViewParent = loader.load();
        Scene scene = new Scene(MainPageViewParent);

        MainPageController mainPageController = loader.getController();
        mainPageController.setPrimaryStage(primaryStage);

        primaryStage.setScene(scene);
        mainPageController.setupMainPage();
    }
    @FXML
    private void handleExit() throws IOException {
        isCountdownRunning = false;
        Platform.runLater(() -> {
            try {
                stopListening();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com/n19/ltmproject/MainPage.fxml"));

                Parent MainPageViewParent = loader.load();
                Scene scene = new Scene(MainPageViewParent);

                MainPageController mainPageController = loader.getController();
                mainPageController.setPrimaryStage(primaryStage);

                primaryStage.setScene(scene);
                mainPageController.setupMainPage();
                mainPageController.showLabelCancelMatch();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void stopListening() {
        running = false;
        serverHandler.sendMessage("STOP_LISTENING");
    }
}