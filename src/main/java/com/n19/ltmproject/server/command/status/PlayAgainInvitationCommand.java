package com.n19.ltmproject.server.command.status;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.handler.ClientHandler;
import com.n19.ltmproject.server.manager.ClientManager;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;

import java.util.HashMap;
import java.util.Map;

public class PlayAgainInvitationCommand implements Command {

    private final ClientManager clientManager;

    public PlayAgainInvitationCommand(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public Response execute(Request request) {
        System.out.println("PlayAgainInvitationCommand.execute()");

        String inviter = (String) request.getParams().get("username");
        String invitee = (String) request.getParams().get("opponent");
        long inviterId = ((Number) request.getParams().get("userId")).longValue();
        long inviteeId = ((Number) request.getParams().get("opponentId")).longValue();

        ClientHandler inviterHandler = clientManager.getClientByPlayerIdAndUsername(inviterId, inviter);
        ClientHandler inviteeHandler = clientManager.getClientByPlayerIdAndUsername(inviteeId, invitee);

        handleSendMessageToInvitee(inviteeHandler, inviter, inviterId);
//        handleSendMessageToInviter(inviterHandler, invitee, inviteeId);

        Map<String, Object> data = new HashMap<>();
        data.put("inviter", inviter);
        data.put("inviterId", inviterId);
        data.put("invitee", invitee);
        data.put("inviteeId", inviteeId);

        return Response.builder()
                .status("OK")
                .message("Lời mời từ " + inviter + " đến " + invitee + " đã được gửi thành công.")
                .data(data)
                .build();
    }

    private void handleSendMessageToInvitee(ClientHandler inviteeHandler, String inviter, long inviterId) {
        if (inviteeHandler != null) {
            inviteeHandler.sendMessage("PlayAgain");
        } else {
            System.out.println("Invitee handler is null.");
        }
    }

    private void handleSendMessageToInviter(ClientHandler inviterHandler, String invitee, long inviteeId) {
        if (inviterHandler != null) {
            inviterHandler.sendMessage("PlayAgain");
        } else {
            System.out.println("Inviter handler is null.");
        }
    }
}