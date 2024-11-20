package com.n19.ltmproject.server.command.auth;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.handler.ClientHandler;
import com.n19.ltmproject.server.model.Player;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;
import com.n19.ltmproject.server.service.AuthService;

public class LoginCommand implements Command {

    //TODO need another approach to handle player's username

    private final AuthService authService;
    private final ClientHandler clientHandler; // Tham chiếu đến ClientHandler

    public LoginCommand(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.authService = new AuthService();
    }

    @Override
    public Response execute(Request request) {
        System.out.println("LoginCommand.execute() called");

        String username = (String) request.getParams().get("username");
        String password = (String) request.getParams().get("password");

        Player player = authService.loginPlayerService(username, password);


        if (player != null) {
            clientHandler.setUsername(username);
            clientHandler.setPlayerId(player.getId());
            return Response.builder()
                    .status("OK")
                    .message("Login successful")
                    .data(player)
                    .build();
        }

        return Response.builder()
                .status("FAILED")
                .message("Invalid username or password")
                .build();
    }

}