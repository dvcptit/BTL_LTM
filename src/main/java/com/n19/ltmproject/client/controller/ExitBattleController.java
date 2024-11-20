// XAC NHAN EXIT
package com.n19.ltmproject.client.controller;

import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.service.MessageService;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExitBattleController {

    private final ServerHandler serverHandler = ServerHandler.getInstance();
    private final MessageService messageService = new MessageService(serverHandler);
    private Stage primaryStage;

    private long gameId;
    private long currentPlayerId;
    private long opponentPlayerId;
    private int score;
    private int opponentScore;
    private int timeLeft;
    private Timeline timeline;
    private Timeline exitTimeline;
    private GamePlayController gamePlayController;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    // Hàm để thiết lập trạng thái của GamePlay
    //TODO remove this method if not used
    public void setGamePlayState(long gameId, long currentPlayerId, long opponentPlayerId, int score, int opponentScore, int timeLeft, Timeline timeline, Stage primaryStage) {
        this.gameId = gameId;
        this.currentPlayerId = currentPlayerId;
        this.opponentPlayerId = opponentPlayerId;
        this.score = score;
        this.opponentScore = opponentScore;
        this.timeLeft = timeLeft;
        this.timeline = timeline;
        this.primaryStage = primaryStage;
    }

    @FXML
    public void setGamePlayController(GamePlayController gamePlayController) {
        this.gamePlayController = gamePlayController;
    }

    @FXML
    private void onConfirmExit(ActionEvent event) throws IOException {
        gamePlayController.stopListening();

        sendResultToServer();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

        gamePlayController.returnMainPage();
    }

    private void sendResultToServer() {
        try {
            // Send the result to the server
            Map<String, Object> endGameParams = new HashMap<>();
            endGameParams.put("gameId", gameId);
            endGameParams.put("player1Id", currentPlayerId);
            endGameParams.put("player2Id", opponentPlayerId);
            endGameParams.put("player1Score", score);
            endGameParams.put("player2Score", opponentScore);

            messageService.sendRequestAndReceiveResponse("exitGameById", endGameParams);

            // exiter is the loser
            Map<String, Object> matchResultParams = new HashMap<>();
            matchResultParams.put("winnerId", opponentPlayerId);
            matchResultParams.put("loserId", currentPlayerId);
            matchResultParams.put("isWin", true);
            matchResultParams.put("isDraw", false);

            messageService.sendRequestAndReceiveResponse("sendMatchResult", matchResultParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi: Không thể gửi kết quả về server");
        }
    }

    @FXML
    private void onCancelExit(ActionEvent event) {
        System.out.println("Thoát đã bị hủy");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}