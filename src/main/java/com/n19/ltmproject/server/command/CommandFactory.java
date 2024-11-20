package com.n19.ltmproject.server.command;

import com.n19.ltmproject.server.command.auth.LoginCommand;
import com.n19.ltmproject.server.command.auth.LogoutCommand;
import com.n19.ltmproject.server.command.auth.SignupCommand;
import com.n19.ltmproject.server.command.game.*;
import com.n19.ltmproject.server.command.player.GetAllPlayerCommand;
import com.n19.ltmproject.server.command.playerHistory.GetAllPlayerHistoryCommand;
import com.n19.ltmproject.server.command.playerHistory.GetPlayerHistoryByIdCommand;
import com.n19.ltmproject.server.command.status.*;
import com.n19.ltmproject.server.handler.ClientHandler;
import com.n19.ltmproject.server.manager.ClientManager;

/**
 * CommandFactory class is a factory class that creates Command objects based on the action string.
 * It is kinda like controller in MVC pattern.
 * It will take the action string from the request and return the appropriate Command object.
 */
public class CommandFactory {

    public static Command getCommand(String action, ClientHandler clientHandler, ClientManager clientManager) {
        return switch (action) {
            case "login" -> new LoginCommand(clientHandler);
            case "signUp" -> new SignupCommand();
            case "getAllGameData" -> new GetAllGameDataCommand();
            case "startNewGame" -> new StartNewGame(clientManager);
            case "endGameById" -> new EndGameById(clientManager);
            case "exitGameById" -> new ExitGameById(clientManager);
            case "exitResult" -> new ExitResultCommand(clientManager);
            case "clickReady" -> new ClickReadyCommand(clientManager);
            case "sendMatchResult" -> new SendMatchResult();
            case "getAllPlayer" -> new GetAllPlayerCommand();
            case "getPlayerHistoryById" -> new GetPlayerHistoryByIdCommand();
            case "getAllPlayerHistory" -> new GetAllPlayerHistoryCommand();
            case "updateScore" -> new UpdateGameScoreCommand(clientManager);
            case "logout" -> new LogoutCommand();
            case "invitation" -> new InvitationCommand(clientManager);
            case "playagain" -> new PlayAgainInvitationCommand(clientManager);
            case "refuseInvitation" -> new RefuseInvitationCommand(clientHandler, clientManager);
            case "userJoinedRoom" -> new UserJoinedRoomCommand(clientHandler, clientManager);
            case "sendChatMessage" -> new SendChatMessageCommand(clientManager);
            default -> throw new IllegalArgumentException("Unknown action: " + action);
        };
    }
}