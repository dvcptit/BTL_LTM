package com.n19.ltmproject.server.command.game;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.handler.ClientHandler;
import com.n19.ltmproject.server.manager.ClientManager;
import com.n19.ltmproject.server.model.Game;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;
import com.n19.ltmproject.server.model.enums.PlayerStatus;
import com.n19.ltmproject.server.service.GameService;
import com.n19.ltmproject.server.service.PlayerService;
import com.google.gson.Gson;

import java.util.HashMap;

public class EndGameById implements Command {

    private final GameService gameService;
    private final PlayerService playerService;
    private final ClientManager clientManager;
    private final Gson gson = new Gson();

    public EndGameById(ClientManager clientManager) {
        this.gameService = new GameService();
        this.playerService = new PlayerService();
        this.clientManager = clientManager;
    }

    @Override
    public Response execute(Request request) {
        long gameId = ((Number) request.getParams().get("gameId")).longValue();
        long player1Id = ((Number) request.getParams().get("player1Id")).longValue();
        long player2Id = ((Number) request.getParams().get("player2Id")).longValue();
        long player1Score = ((Number) request.getParams().get("player1Score")).longValue();
        long player2Score = ((Number) request.getParams().get("player2Score")).longValue();

        Game game = gameService.endGameById(gameId, player1Id, player2Id, player1Score, player2Score);
        playerService.updatePlayerStatusById(game.getPlayer1Id(), PlayerStatus.ONLINE);
        playerService.updatePlayerStatusById(game.getPlayer2Id(), PlayerStatus.ONLINE);

        ClientHandler player2Handler = clientManager.getClientByPlayerIdAndUsername(player2Id, null);


        if (player2Handler != null) {
            String endGameMessage = createJsonMessage("GAME_END",
                    "Game " + gameId + " đã kết thúc. Điểm số: Player 1: " + player1Score + ", Player 2: " + player2Score,
                    new HashMap<String, Object>() {{
                        put("gameId", gameId);
                        put("player1Id", player1Id);
                        put("player1Score", player1Score);
                        put("player2Id", player2Id);
                        put("player2Score", player2Score);
                    }});

            player2Handler.sendMessage(endGameMessage);
        }

        return Response.builder()
                .status("OK")
                .message("Game ended successfully")
                .data(game)
                .build();
    }

    private String createJsonMessage(String status, String message, Object data) {
        Response response = Response.builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
        return gson.toJson(response);
    }
}
