package com.n19.ltmproject.server.command.playerHistory;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.model.dto.PlayerHistoryDto;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;
import com.n19.ltmproject.server.service.PlayerHistoryService;

public class GetPlayerHistoryByIdCommand implements Command {
    private final PlayerHistoryService playerHistoryService;

    public GetPlayerHistoryByIdCommand() {
        this.playerHistoryService = new PlayerHistoryService();
    }

    @Override
    public Response execute(Request request) {
        System.out.println("GetPlayerHistoryByIdCommand.execute()");
        long playerId = ((Number) request.getParams().get("inviterId")).longValue();
        System.out.println("playerId History: "+ playerId);

        // Get PlayerHistoryDto by playerId
        PlayerHistoryDto playerHistory = playerHistoryService.getPlayerHistoryById(playerId);
        return Response.builder()
                .status("OK")
                .message("Player history retrieved successfully")
                .data(playerHistory)
                .build();
    }
}
