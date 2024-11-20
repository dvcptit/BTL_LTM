package com.n19.ltmproject.server.command.game;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;
import com.n19.ltmproject.server.service.PlayerHistoryService;

public class SendMatchResult implements Command {
    private final PlayerHistoryService playerHistoryService;

    public SendMatchResult() {
        this.playerHistoryService = new PlayerHistoryService();
    }

    @Override
    public Response execute(Request request) {
        long winnerId = ((Number) request.getParams().get("winnerId")).longValue();
        long loserId = ((Number) request.getParams().get("loserId")).longValue();
        boolean isWin = (Boolean) request.getParams().get("isWin");
        boolean isDraw = (Boolean) request.getParams().get("isDraw");


        // Check win status
        if (isWin) {
            // Update game history for the winner
            boolean isWinnerUpdated = playerHistoryService.updateGameHistory(winnerId, true, false); // Winner
            boolean isLoserUpdated = playerHistoryService.updateGameHistory(loserId, false, false); // Loser

            if (isWinnerUpdated && isLoserUpdated) {
                return Response.builder()
                        .status("OK")
                        .message("Match result sent successfully: Winner")
                        .data(null)
                        .build();
            } else {
                return Response.builder()
                        .status("ERROR")
                        .message("Failed to update game history for the winner.")
                        .build();
            }
        } else if (isDraw) {
            // Update game history for a draw
            boolean isPlayer1Updated = playerHistoryService.updateGameHistory(winnerId, false, true);
            boolean isPlayer2Updated = playerHistoryService.updateGameHistory(loserId, false, true);

            if (isPlayer1Updated && isPlayer2Updated) {
                return Response.builder()
                        .status("OK")
                        .message("Match result sent successfully: Draw")
                        .data(null)
                        .build();
            } else {
                return Response.builder()
                        .status("ERROR")
                        .message("Failed to update game history for the draw.")
                        .build();
            }
        } else {
            // Handle loss scenario
            boolean isLoserUpdated = playerHistoryService.updateGameHistory(loserId, false, false);
            boolean isWinnerHistoryUpdated = playerHistoryService.updateGameHistory(winnerId, true, false);

            if (isLoserUpdated && isWinnerHistoryUpdated) {
                return Response.builder()
                        .status("OK")
                        .message("Match result sent successfully: Loss")
                        .data(null)
                        .build();
            } else {
                return Response.builder()
                        .status("ERROR")
                        .message("Failed to update game history for the loss.")
                        .build();
            }
        }
    }
}