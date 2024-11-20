package com.n19.ltmproject.server.command.player;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.model.Player;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;
import com.n19.ltmproject.server.service.PlayerService;

import java.util.List;

public class GetAllPlayerCommand implements Command {

    private final PlayerService playerService;

    public GetAllPlayerCommand() {
        this.playerService = new PlayerService();
    }

    @Override
    public Response execute(Request request) {
        //TODO change to playerDto, not include password
        List<Player> players = playerService.getAllPlayers();
        return Response.builder()
                .status("OK")
                .message("All players retrieved successfully")
                .data(players)
                .build();
    }
}
