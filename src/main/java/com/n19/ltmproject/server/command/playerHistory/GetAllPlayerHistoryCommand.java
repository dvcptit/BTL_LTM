package com.n19.ltmproject.server.command.playerHistory;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.model.dto.PlayerHistoryDto;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;
import com.n19.ltmproject.server.service.PlayerHistoryService;

import java.util.List;

public class GetAllPlayerHistoryCommand implements Command {
    private final PlayerHistoryService playerHistoryService;

    public GetAllPlayerHistoryCommand() {
        this.playerHistoryService = new PlayerHistoryService();
    }

    @Override
    public Response execute(Request request) {

        List<PlayerHistoryDto> players = playerHistoryService.getAllPlayerHistory();

        return Response.builder()
                .status("OK")
                .message("All players history retrieved successfully")
                .data(players)
                .build();
    }
}
