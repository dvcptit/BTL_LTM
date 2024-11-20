package com.n19.ltmproject.server.command.game;

import com.n19.ltmproject.server.model.enums.PlayerStatus;
import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.handler.ClientHandler;
import com.n19.ltmproject.server.manager.ClientManager; // Ensure you have this import
import com.n19.ltmproject.server.model.Game;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;
import com.n19.ltmproject.server.service.GameService;
import com.n19.ltmproject.server.service.PlayerService;

public class StartNewGame implements Command {

    private final GameService gameService;
    private final PlayerService playerService;
    private final ClientManager clientManager; // Add ClientManager

    public StartNewGame(ClientManager clientManager) {
        this.playerService = new PlayerService();
        this.gameService = new GameService();
        this.clientManager = clientManager; // Initialize ClientManager
    }

    @Override
    public Response execute(Request request) {
        System.out.println(request.toString());

        long player1Id = ((Number) request.getParams().get("player1Id")).longValue();
        long player2Id = ((Number) request.getParams().get("player2Id")).longValue();

        // Create a new game
        Game game = gameService.createNewGame(player1Id, player2Id);

        // Update player statuses to IN_GAME
        playerService.updatePlayerStatusById(player1Id, PlayerStatus.IN_GAME);
        playerService.updatePlayerStatusById(player2Id, PlayerStatus.IN_GAME);

        ClientHandler player2Client = clientManager.getClientByPlayerIdAndUsername(player2Id,null);

        // Create a message to send to both players
        String gameStartMessage = "New game started! Game ID: " + game.getGameId();

        if (player2Client != null) {
            player2Client.sendMessage(gameStartMessage);
        }

        // Return the response
        return Response.builder()
                .status("OK")
                .message("New game started successfully")
                .data(game)
                .build();
    }
}
