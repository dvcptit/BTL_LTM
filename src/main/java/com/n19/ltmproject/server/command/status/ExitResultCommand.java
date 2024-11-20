package com.n19.ltmproject.server.command.status;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.handler.ClientHandler;
import com.n19.ltmproject.server.manager.ClientManager;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;

import java.util.HashMap;
import java.util.Map;

public class ExitResultCommand implements Command {
    private final ClientManager clientManager;

    public ExitResultCommand(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public Response execute(Request request) {
        System.out.println("ExitResultCommand.execute()");

        String opponent = (String) request.getParams().get("opponent");
        String username = (String) request.getParams().get("username");
        long userId = ((Number) request.getParams().get("userId")).longValue();
        long opponentId = ((Number) request.getParams().get("opponentId")).longValue();

        ClientHandler inviterHandler = clientManager.getClientByPlayerIdAndUsername(opponentId, opponent);

        handleSendMessageResult(inviterHandler, opponent);

        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("opponent", opponent);
        data.put("userId", userId);
        data.put("opponentId", opponentId);

        return Response.builder()
                .status("OK")
                .message("Bạn đã thoát thành công")
                .data(data)
                .build();
    }

    private void handleSendMessageResult(ClientHandler inviteeHandler, String opponent) {
        if (inviteeHandler != null) {
            inviteeHandler.sendMessage("EXITRESULT "+opponent);
        } else {
            System.out.println("Invitee handler is null.");
        }
    }

}
