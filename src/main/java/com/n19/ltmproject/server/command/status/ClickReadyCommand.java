package com.n19.ltmproject.server.command.status;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.handler.ClientHandler;
import com.n19.ltmproject.server.manager.ClientManager;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;

import java.util.HashMap;
import java.util.Map;

public class ClickReadyCommand implements Command {
    private final ClientManager clientManager;

    public ClickReadyCommand(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public Response execute(Request request) {
        System.out.println("ClickReadyCommand.execute()");

        String opponent = (String) request.getParams().get("opponent");
        String username = (String) request.getParams().get("username");
        long userId = ((Number) request.getParams().get("userId")).longValue();
        long opponentId = ((Number) request.getParams().get("opponentId")).longValue();

        ClientHandler inviterHandler = clientManager.getClientByPlayerIdAndUsername(opponentId, opponent);
        ClientHandler inviteeHandler = clientManager.getClientByPlayerIdAndUsername(userId, username);
        handleSendMessageResult(inviterHandler, opponent);
        handleSendMessageResult(inviteeHandler, username);

        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("opponent", opponent);
        data.put("userId", userId);
        data.put("opponentId", opponentId);

        return Response.builder()
                .status("OK")
                .message("Bạn đã Ready thành công")
                .data(data)
                .build();
    }

    private void handleSendMessageResult(ClientHandler inviteeHandler, String opponent) {
        if (inviteeHandler != null) {
            inviteeHandler.sendMessage("ClickReady "+opponent);
        } else {
            System.out.println("Invitee handler is null.");
        }
    }

}

