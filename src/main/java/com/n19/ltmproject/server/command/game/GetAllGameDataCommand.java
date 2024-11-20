package com.n19.ltmproject.server.command.game;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.model.dto.GameHistoryDto;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;
import com.n19.ltmproject.server.service.GameService;

import java.util.List;

public class GetAllGameDataCommand implements Command {

    private final GameService gameService;

    public GetAllGameDataCommand() {
        this.gameService = new GameService();
    }

    @Override
    public Response execute(Request request) {
        System.out.println("GetAllGameDataCommand.execute() called");
        String playerId = (String) request.getParams().get("playerId");

        List<GameHistoryDto> games = gameService.getAllGameData(playerId);
        return Response.builder()
                .status("OK")
                .message("Game data fetched successfully")
                .data(games)
                .build();
    }
}
