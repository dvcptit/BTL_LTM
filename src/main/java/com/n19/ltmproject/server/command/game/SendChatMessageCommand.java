package com.n19.ltmproject.server.command.game;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.handler.ClientHandler;
import com.n19.ltmproject.server.manager.ClientManager;
import com.n19.ltmproject.server.model.Player;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;
import com.n19.ltmproject.server.service.GameService;

public class SendChatMessageCommand implements Command {
    private final GameService gameService;
    private final ClientManager clientManager;

    public SendChatMessageCommand(ClientManager clientManager) {
        this.gameService = new GameService();
        this.clientManager = clientManager;
    }

    @Override
    public Response execute(Request request) {
        System.out.println("SendChatMessageCommand.execute() called");

        int currentPlayerId = Integer.parseInt((String) request.getParams().get("currentPlayerId"));
        int opponentPlayerId = Integer.parseInt((String) request.getParams().get("opponentPlayerId"));
        String message = (String) request.getParams().get("message");

        String res = gameService.sendChatMessageService(currentPlayerId, opponentPlayerId, message);

        ClientHandler player2Client = clientManager.getClientByPlayerIdAndUsername(opponentPlayerId,null);
        // Create a message to send to both players
        if (player2Client != null) {
            player2Client.sendMessage("Send message from user1: " + res);
        }

        System.out.println(res);
        if (res != null) {
            return Response.builder()
                    .status("OK")
                    .message(res)
                    .build();
        }

        return Response.builder()
                .status("FAILED")
                .message("Failed to send message!")
                .build();
    }
}
