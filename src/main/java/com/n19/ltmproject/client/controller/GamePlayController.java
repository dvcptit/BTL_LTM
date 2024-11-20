package com.n19.ltmproject.client.controller;
// CLICK EXIT
// SEND KET QUA TRAN DAU (UPDATE WIN , LOSS)
import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.service.MessageService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class GamePlayController {

    private final ServerHandler serverHandler = ServerHandler.getInstance();
    private final MessageService messageService = new MessageService(serverHandler);
    private volatile boolean running = true;

    @FXML
    private ImageView trashImage;

    @FXML
    private ImageView trashCan1;
    @FXML
    private ImageView trashCan2;
    @FXML
    private ImageView trashCan3;
    @FXML
    private ImageView trashCan4;
    @FXML
    private ImageView trashCan5;

    // TODO rename for better readability
    @FXML
    private ImageView humanGameplay;
    @FXML
    private ImageView messageGameplay;
    @FXML
    private Label feedbackLabel;

    private int cnt = 0;

    // TODO rename for better readability
    @FXML
    private ImageView humanGameplay1;
    @FXML
    private ImageView messageGameplay1;
    @FXML
    private Label feedbackLabel1;

    @FXML
    private Label currentPlayerScoreLabel;
    @FXML
    private Label opponentPlayerScoreLabel;

    @FXML
    private Label timerLabel;

    private final Random random = new Random();
    private final int[] trashImagesCount = {21, 22, 21, 24, 21};

	Timeline timeline;

    @Setter
    @Getter
    private Stage primaryStage;

    @Getter
    private int currentPlayerScore = 0;
    @Getter
    private int opponentPlayerScore = 0;
    @Getter
    @Setter
    private long gameId;

    private boolean isInviter;

    private long currentPlayerId;
    @FXML
    private Label currentPlayerName;

    private long opponentPlayerId;
    @FXML
    private Label opponentPlayerName;

    private int timeLeft = 60;

    private final String[] trashTypes = {"organic", "metal", "plastic", "glass", "paper"};
    private final String[] correctFeedback = {"Correct!", "Nice!", "Good job!"};
    private final String[] incorrectFeedback = {"Wrong!", "Try next time!"};
    private String currentTrashType;

    private boolean isDragging = false;
    private double offsetX;
    private double offsetY;
    private double initialX;
    private double initialY;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Initialize the game with the game ID, current player ID, opponent player ID,
     * current player name, and opponent player name.
     *
     * @param gameId             The game ID
     * @param currentPlayerId    The current player ID
     * @param opponentPlayerId   The opponent player ID
     * @param currentPlayerName  The current player name
     * @param opponentPlayerName The opponent player name
     */
    public void initGame(long gameId, long currentPlayerId, long opponentPlayerId, String currentPlayerName, String opponentPlayerName, boolean isInviter) {
        this.gameId = gameId;
        this.currentPlayerId = currentPlayerId;
        this.opponentPlayerId = opponentPlayerId;
        this.currentPlayerName.setText(currentPlayerName + " (me)");
        this.opponentPlayerName.setText(opponentPlayerName);
        this.isInviter = isInviter;
    }

    /**
     * Set the stage for the game play.
     *
     * @param stage The stage
     */
    public void setStage(Stage stage) {
        this.primaryStage = stage;
        startGame();
        startListeningToServer();
    }

    /**
     * Start the game by initializing the timer and spawning the first trash item.
     */
    private void startGame() {
        System.out.println(gameId);
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        spawnTrash();
    }

    /**
     * Start listening to the server for updates the score of the opponent player.
     * This will run in a separate thread.
     */
    void startListeningToServer() {

        new Thread(() -> {
            try {
                while (running) {
                    synchronized (this) {
                        String serverMessage = serverHandler.receiveMessage();
                        System.out.println("Thread in gameplay");
                        if (serverMessage != null) {
                            System.out.println("Received from server: " + serverMessage);
                            parseServerMessage(serverMessage);
                        }
                    }
                }
                System.out.println("END THREAD IN GAMEPLAY");
            } catch (IOException ex) {
                System.out.println("Error receiving message from server: " + ex.getMessage());
            }
        }).start();
    }

    // TODO refactor if have time
    /**
     * Parse the server message to update the score of the opponent player.
     * The message should be in the format: "[UPDATE_SCORE] Game ID: {gameId}, Opponent Score: {opponentScore}"
     *
     * @param serverMessage The server message
     */
    private synchronized void parseServerMessage(String serverMessage) {
        if (serverMessage.startsWith("New game started! Game ID:")) {
            // Extract game ID using string manipulation
            String[] parts = serverMessage.split(": ");
            if (parts.length > 1) {
                try {
                    long newGameId = Long.parseLong(parts[1].trim());
                    setGameId(newGameId); // Set the game ID in the controller
                    System.out.println("Successfully passed Game ID: " + newGameId);
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing Game ID: " + e.getMessage());
                }
            }
        }

        // The request is in the format: "[UPDATE_SCORE] Game ID: {gameId}, Opponent Score: {opponentScore}"
        if (serverMessage.startsWith("[UPDATE_SCORE]")) {
            String[] parts = serverMessage.split(",");
            if (parts.length == 2) {
                String opponentScorePart = parts[1].trim();
                String[] scoreParts = opponentScorePart.split(":");
                if (scoreParts.length == 2) {
                    int updatedOpponentScore = Integer.parseInt(scoreParts[1].trim());
                    updateOpponentScore(updatedOpponentScore);
                }
            }
        }

        if (serverMessage.contains("\"status\":\"GAME_END\"")) {
            // Hiển thị thông báo và chuyển người chơi đến trang kết quả với trạng thái thắng
            Platform.runLater(() -> {
                stopListening();
                if (timeline != null) {
                    timeline.stop();
                }
                showResultScreen();
            });
        }
        if (serverMessage.contains("\"status\":\"EXIT_GAME\"")) {
            // Hiển thị thông báo và chuyển người chơi đến trang kết quả với trạng thái thắng
            Platform.runLater(() -> {
                stopListening();
                if (timeline != null) {
                    timeline.stop();
                }
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/n19/ltmproject/Result.fxml"));
                Parent resultScreen = null;
                try {
                    resultScreen = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ResultController resultController = loader.getController();
                resultController.setPlayerNames(currentPlayerName.getText(), opponentPlayerName.getText());
                resultController.setOpponentExit();

                String resultMessage;
                boolean isWin = true;
                boolean isDraw = false;
                String scoreMessage = currentPlayerScore + " - " + opponentPlayerScore;


                primaryStage.setScene(new Scene(resultScreen));
                String trimmedCurrentPlayerName = currentPlayerName.getText().replace(" (me)", "");
                String trimmedOpponentPlayerName = opponentPlayerName.getText().replace(" (me)", "");

                // Set result message and call setResults based on game outcome
                resultMessage = "Bạn đã thắng!";

                resultController.setUpPlayerID(currentPlayerId, opponentPlayerId, trimmedCurrentPlayerName, trimmedOpponentPlayerName);
                resultController.setPrimaryStage(primaryStage);

                resultController.setResults(
                        resultMessage,
                        scoreMessage,
                        isWin,
                        isDraw,
                        this.currentPlayerId,
                        this.opponentPlayerId

                );

                System.out.println("Trimmed current player name: " + trimmedCurrentPlayerName);
                System.out.println("Trimmed opponent player name: " + trimmedOpponentPlayerName);

                primaryStage.show();

            });
        }
    }

    /**
     * Update the score of the opponent player in the UI after receive notify from
     * server.
     *
     * @param updatedScore The updated score of the opponent player
     */
    private synchronized void updateOpponentScore(int updatedScore) {
        System.out.println("Received score update: " + updatedScore);

        Platform.runLater(() -> {
            try {
                this.cnt++;
                System.out.println("Inside runLater - CNT: " + cnt);
                this.opponentPlayerScore = updatedScore;
                opponentPlayerScoreLabel.setText("Score: " + opponentPlayerScore);
                System.out.println("Opponent's score updated: " + opponentPlayerScore);
            } catch (Exception e) {
                System.err.println("Error updating opponent's score: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }



    /**
     * Update the timer every second.
     * If the time is up, end the game.
     */
    void updateTimer() {
        if (timeLeft > 0) {
            timeLeft--;
            timerLabel.setText("Thời gian còn lại: " + timeLeft + " giây");
        } else {
            endGame();
        }
    }

    /**
     * Spawn a new trash item on the screen.
     * The trash item will be randomly selected from the trash types.
     * It will take a random image from the resources.
     */
    private void spawnTrash() {
        boolean imageLoaded = false;
        while (!imageLoaded) {
            int randomTypeIndex = random.nextInt(trashTypes.length);
            currentTrashType = trashTypes[randomTypeIndex];

            int maxImages = trashImagesCount[randomTypeIndex];
            int randomImageIndex = random.nextInt(maxImages) + 1;
            String imageDirectory = "/com/n19/ltmproject/images/";
            String imagePath = imageDirectory + currentTrashType + "/" + currentTrashType + randomImageIndex + ".png";

            try {
                Image trashImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                trashImage.setImage(trashImg);
                imageLoaded = true;
            } catch (Exception e) {
                System.out.println("Lỗi: Không thể tải hình ảnh từ " + imagePath);
            }
        }
        resetTrashPosition();
    }

    /**
     * Initialize the trash item and trash cans.
     * Set the event handlers for dragging the trash item and dropping it into the
     * trash cans.
     */
    @FXML
    private void initialize() {
        trashImage.setOnMousePressed(event -> {
            isDragging = true;
            offsetX = event.getSceneX() - trashImage.getLayoutX();
            offsetY = event.getSceneY() - trashImage.getLayoutY();
            initialX = trashImage.getLayoutX();
            initialY = trashImage.getLayoutY();
        });

        trashImage.setOnMouseDragged(event -> {
            if (isDragging) {
                trashImage.setLayoutX(event.getSceneX() - offsetX);
                trashImage.setLayoutY(event.getSceneY() - offsetY);
            }
        });

        trashImage.setOnMouseReleased(event -> {
            isDragging = false;
            checkTrashDrop(event.getSceneX(), event.getSceneY());
        });

        trashCan1.setOnMouseClicked(event -> handleTrashDrop("glass"));
        trashCan2.setOnMouseClicked(event -> handleTrashDrop("metal"));
        trashCan3.setOnMouseClicked(event -> handleTrashDrop("organic"));
        trashCan4.setOnMouseClicked(event -> handleTrashDrop("paper"));
        trashCan5.setOnMouseClicked(event -> handleTrashDrop("plastic"));
    }

    /**
     * Check if the trash item is dropped into a trash can.
     * If the trash item is dropped into a trash can, handle the trash drop, should
     * increase the player score if correct.
     * Otherwise, reset the trash item position.
     *
     * @param dropX The X coordinate of the drop
     * @param dropY The Y coordinate of the drop
     */
    private void checkTrashDrop(double dropX, double dropY) {
        Map<String, ImageView> trashCanMap = Map.of(
                "glass", trashCan1,
                "metal", trashCan2,
                "organic", trashCan3,
                "paper", trashCan4,
                "plastic", trashCan5
        );

        for (Map.Entry<String, ImageView> entry : trashCanMap.entrySet()) {
            if (isWithinBounds(dropX, dropY, entry.getValue())) {
                handleTrashDrop(entry.getKey());
                return;
            }
        }

        resetTrashPosition();
    }

    /**
     * Check if the drop coordinates are within the bounds of the trash can.
     *
     * @param dropX    The X coordinate of the drop
     * @param dropY    The Y coordinate of the drop
     * @param trashCan The trash can image view
     * @return True if the drop coordinates are within the bounds of the trash can,
     *         false otherwise
     */
    private boolean isWithinBounds(double dropX, double dropY, ImageView trashCan) {
        double canX = trashCan.getLayoutX();
        double canY = trashCan.getLayoutY();
        double canWidth = trashCan.getFitWidth();
        double canHeight = trashCan.getFitHeight();

        return dropX >= canX && dropX <= canX + canWidth && dropY >= canY && dropY <= canY + canHeight;
    }

    /**
     * Reset the trash item position to the initial position.
     */
    private void resetTrashPosition() {
        trashImage.setLayoutX(250);
        trashImage.setLayoutY(180);
    }

    /**
     * Handle the trash drop into a trash can.
     * If the trash item is dropped into the correct trash can, increase the player
     * score.
     * Otherwise, show the feedback and spawn a new trash item.
     *
     * @param binType The type of the trash can
     */
    private void handleTrashDrop(String binType) {
        if (currentTrashType.equals(binType)) {
            currentPlayerScore++;
            currentPlayerScoreLabel.setText("Score: " + currentPlayerScore);
            showFeedback(true);
            sendScoreUpdateToServer(gameId, currentPlayerId, opponentPlayerId, currentPlayerScore);
        } else {
            showFeedback(false);
        }
        spawnTrash();
    }

    /**
     * Send a request to the server to update the score of the current player.
     * This will also notify the opponent player of the updated score.
     *
     * @param gameId             The current gameId
     * @param currentPlayerId    The ID of the current player
     * @param opponentPlayerId   The ID of the opponent player
     * @param currentPlayerScore The updated score of the current player
     */
    private void sendScoreUpdateToServer(long gameId, long currentPlayerId, long opponentPlayerId, int currentPlayerScore) {
        Map<String, Object> params = new HashMap<>();
        params.put("gameId", gameId);
        params.put("currentPlayerId", currentPlayerId);
        params.put("opponentPlayerId", opponentPlayerId);
        params.put("currentPlayerScore", currentPlayerScore);

        messageService.sendRequestNoResponse("updateScore", params);
    }

    /**
     * Show the feedback message after the trash item is dropped.
     * If the feedback is correct, show the correct feedback message.
     * Otherwise, show the incorrect feedback message.
     *
     * @param isCorrect True if the feedback is correct, false otherwise
     */
    private void showFeedback(boolean isCorrect) {
        // Set visibility based on whether the feedback is correct
        humanGameplay.setVisible(isCorrect);
        messageGameplay.setVisible(isCorrect);
        humanGameplay1.setVisible(!isCorrect);
        messageGameplay1.setVisible(!isCorrect);

        // Update feedback labels
        Label activeFeedbackLabel = isCorrect ? feedbackLabel : feedbackLabel1;
        Label inactiveFeedbackLabel = isCorrect ? feedbackLabel1 : feedbackLabel;
        String[] feedbackMessages = isCorrect ? correctFeedback : incorrectFeedback;
        String color = isCorrect ? "green" : "red";

        activeFeedbackLabel.setText(feedbackMessages[random.nextInt(feedbackMessages.length)]);
        activeFeedbackLabel.setStyle("-fx-text-fill: " + color + ";");
        inactiveFeedbackLabel.setText("");
    }



    //TODO after the game ends, send the result to the server to update the game with player score,
    // and update the player history with the game result
    // and update player status to ONLINE
    /**
     * End the game and show the result screen.
     */
    private void endGame() {
        timeline.stop();
        stopListening();

        //TODO duplicated code
        if (isInviter) {
            sendEndGame();
            boolean isWin = currentPlayerScore > opponentPlayerScore;
            boolean isDraw = currentPlayerScore == opponentPlayerScore;
            long winnerId = isWin ? currentPlayerId : opponentPlayerId;
            long loserId = isWin ? opponentPlayerId : currentPlayerId;
            sendMatchResult(winnerId, loserId, isWin, isDraw);
            showResultScreen();
        }
    }

    void stopListening() {
        running = false;
        serverHandler.sendMessage("STOP_LISTENING");
    }

    /**
     * Command to send the final result of the game to the server.
     */
    private void sendEndGame() {
        Map<String, Object> params = new HashMap<>();
        params.put("gameId", gameId);
        params.put("player1Id", currentPlayerId);
        params.put("player2Id", opponentPlayerId);
        params.put("player1Score", currentPlayerScore);
        params.put("player2Score", opponentPlayerScore);
        messageService.sendRequestNoResponse("endGameById", params);
    }

    /**
     * Send the match result to the server including winnerId, loserId, isWin, and isDraw.
     *
     * @param winnerId The ID of the winning player
     * @param loserId The ID of the losing player
     * @param isWin Indicates if the current player has won (1 for win, 0 for loss)
     * @param isDraw Indicates if the game ended in a draw (1 for draw, 0 for no draw)
     */
    private void sendMatchResult(long winnerId, long loserId, boolean isWin, boolean isDraw) {
        // Create a request map to hold the parameters
        Map<String, Object> params = new HashMap<>();
        params.put("winnerId", winnerId);
        params.put("loserId", loserId);
        params.put("isWin", isWin);
        params.put("isDraw", isDraw);

        // Send the message to the server
        messageService.sendRequestNoResponse("sendMatchResult", params);
    }

    /**
     * Show the result screen with the game outcome.
     * The result screen will display the result message, the score, and the game
     * outcome.
     */
    private void showResultScreen() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/n19/ltmproject/Result.fxml"));
                Parent resultScreen = loader.load();
                ResultController resultController = loader.getController();
                resultController.setPlayerNames(currentPlayerName.getText(), opponentPlayerName.getText());

                String resultMessage;
                boolean isWin = currentPlayerScore > opponentPlayerScore;
                boolean isDraw = currentPlayerScore == opponentPlayerScore;
                String scoreMessage = currentPlayerScore + " - " + opponentPlayerScore;

                // Set result message and call setResults based on game outcome
                if (isWin) {
                    resultMessage = "Bạn đã thắng!";
                } else if (isDraw) {
                    resultMessage = "Trận đấu hòa!";
                } else {
                    resultMessage = "Bạn đã thua!";
                }

            primaryStage.setScene(new Scene(resultScreen));
            String trimmedCurrentPlayerName = currentPlayerName.getText().replace(" (me)", "");
            String trimmedOpponentPlayerName = opponentPlayerName.getText().replace(" (me)", "");

            resultController.setUpPlayerID(currentPlayerId, opponentPlayerId, trimmedCurrentPlayerName, trimmedOpponentPlayerName);
            resultController.setPrimaryStage(primaryStage);

            resultController.setResults(
                    resultMessage,
                    scoreMessage,
                    isWin,
                    isDraw,
                    this.currentPlayerId,
                    this.opponentPlayerId
            );

            System.out.println("Trimmed current player name: " + trimmedCurrentPlayerName);
            System.out.println("Trimmed opponent player name: " + trimmedOpponentPlayerName);

            primaryStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Handle the exit button click.
     * Show the exit battle modal to confirm the exit.
     */
    @FXML
    private void handleExit() {
        showExitBattleModal();
    }

    // TODO implement logic after confirming exit
    /**
     * Show the exit battle modal to confirm the exit.
     */
    private void showExitBattleModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/n19/ltmproject/ExitBattle.fxml"));
            Parent exitBattleModal = loader.load();
            ExitBattleController exitBattleController = loader.getController();
            exitBattleController.setGamePlayController(this);
            exitBattleController.setGamePlayState(gameId, currentPlayerId, opponentPlayerId, currentPlayerScore, opponentPlayerScore, timeLeft, timeline, primaryStage);

            Stage modalStage = new Stage();
            modalStage.initOwner(primaryStage);
            modalStage.setScene(new Scene(exitBattleModal));
            modalStage.setTitle("Kết thúc trận đấu");

            modalStage.setOnCloseRequest(event -> {
                event.consume();
                modalStage.hide();
                startListeningToServer();
                System.out.println("Listening to server again");
            });

            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return to MainPage if confirmed exit.
     */
    void returnMainPage() {
        if (timeline != null) {
            timeline.stop();
        }
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com/n19/ltmproject/MainPage.fxml"));

                Parent MainPageViewParent = loader.load();
                Scene scene = new Scene(MainPageViewParent);

                MainPageController mainPageController = loader.getController();
                mainPageController.setPrimaryStage(primaryStage);

                primaryStage.setScene(scene);
                mainPageController.setupMainPage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
