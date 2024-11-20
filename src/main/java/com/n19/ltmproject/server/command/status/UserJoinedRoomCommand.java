package com.n19.ltmproject.server.command.status;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.handler.ClientHandler;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.manager.ClientManager;
import com.n19.ltmproject.server.model.dto.Response;
import com.google.gson.Gson;

import java.util.HashMap;

public class UserJoinedRoomCommand implements Command {

    private final ClientHandler clientHandler;
    private final ClientManager clientManager;
    private final Gson gson = new Gson();

    public UserJoinedRoomCommand(ClientHandler clientHandler, ClientManager clientManager) {
        this.clientHandler = clientHandler;
        this.clientManager = clientManager;
    }

    @Override
    public Response execute(Request request) {
        String invitedPlayerName = (String) request.getParams().get("username");
        String inviterName = (String) request.getParams().get("inviterName");
        long invitedPlayerId = ((Number) request.getParams().get("inviteeId")).longValue();
        long inviterId = ((Number) request.getParams().get("inviterId")).longValue();

        ClientHandler invitee = clientManager.getClientByPlayerIdAndUsername(invitedPlayerId, invitedPlayerName);
        ClientHandler inviter = clientManager.getClientByPlayerIdAndUsername(inviterId, inviterName);

        if (inviter != null && invitee != null) {
            String inviteMessage = createJsonMessage("SUCCESS",
                    "[JOINED] User " + invitedPlayerName + " với playerId " + invitedPlayerId +
                            " đã tham gia phòng với " + inviterName + " playerId " + inviterId + ".",
                    new HashMap<String, Object>() {{
                        put("inviteeName", invitedPlayerName);
                        put("inviteeId", invitedPlayerId);
                        put("inviterName", inviterName);
                        put("inviterId", inviterId);
                    }});

            inviter.sendMessage(inviteMessage);
            invitee.sendMessage(inviteMessage);
        }

        return Response.builder()
                .status("SUCCESS")
                .message("Thành công!")
                .data(new HashMap<String, Object>() {{
                    put("username", invitedPlayerName);
                    put("inviteeId", invitedPlayerId);
                    put("inviterName", inviterName);
                    put("inviterId", inviterId);
                }})
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
