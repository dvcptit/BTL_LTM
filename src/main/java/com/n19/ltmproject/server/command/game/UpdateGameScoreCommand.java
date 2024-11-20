package com.n19.ltmproject.server.command.game;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.handler.ClientHandler;
import com.n19.ltmproject.server.manager.ClientManager;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;

public class UpdateGameScoreCommand implements Command {

    private final ClientManager clientManager;

    public UpdateGameScoreCommand(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    /**
     * Get the long parameter from the request.
     * If the parameter is missing or not a number, return the default value.
     *
     * @param request The request
     * @param paramName The parameter name
     * @param defaultValue The default value
     * @return The long value of the parameter
     */
    private long getLongParam(Request request, String paramName, long defaultValue) {
        Object paramValue = request.getParams().get(paramName);
        if (paramValue instanceof Number) {
            return ((Number) paramValue).longValue();
        } else {
            System.out.println("Parameter " + paramName + " is missing or not a number. Using default value: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Execute the command to update the game score and notify the opponent player.
     *
     * @param request The request
     * @return The response
     */
    @Override
    public Response execute(Request request) {
        System.out.println("UpdateGameScoreCommand: execute");

        long gameId = getLongParam(request, "gameId", -1);
        //TODO remove if not needed
//        long currentPlayerId = getLongParam(request, "currentPlayerId", -1);
        long opponentPlayerId = getLongParam(request, "opponentPlayerId", -1);
        long currentPlayerScore = getLongParam(request, "currentPlayerScore", 0);

        sendUpdateToOpponentPlayer(gameId, opponentPlayerId, currentPlayerScore);

        return Response.builder()
                .status("OK")
                .message("Score updated successfully and notified the other player.")
                .data(null)
                .build();
    }

    /**
     * Send the updated score to the opponent player depend on the gameId and opponentPlayerId.
     *
     * @param gameId The game ID
     * @param opponentPlayerId The opponent player ID
     * @param currentPlayerScore The current player score to send to opponent to update
     */
    private void sendUpdateToOpponentPlayer(long gameId, long opponentPlayerId, long currentPlayerScore) {
        // Tìm kiếm ClientHandler tương ứng với playerId
        ClientHandler targetClient = clientManager.getClientByPlayerIdAndUsername(opponentPlayerId, null);
        if (targetClient != null) {
            String message = String.format("[UPDATE_SCORE] Game ID: %s, Opponent Score: %d", gameId, currentPlayerScore);
            targetClient.sendMessage(message);
            System.out.println("Notified Player ID: " + opponentPlayerId + " with new scores.");
        } else {
            System.out.println("Player ID: " + opponentPlayerId + " not found.");
        }
    }
}