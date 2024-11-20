package com.n19.ltmproject.server.service;

import com.n19.ltmproject.server.dao.GameDao;
import com.n19.ltmproject.server.model.Game;
import com.n19.ltmproject.server.model.dto.GameHistoryDto;

import java.util.List;

public class GameService {

    private final GameDao gameDao;

    public GameService() {
        this.gameDao = new GameDao();
    }

    public List<GameHistoryDto> getAllGameData(String playerId) {
        return gameDao.getAllGameData(playerId);
    }

    public Game createNewGame(long player1Id, long player2Id) {
        return gameDao.createNewGame(player1Id, player2Id);
    }

    public Game endGameById(long gameId, long player1Id, long player2Id, long player1Score, long player2Score) {
        return gameDao.endGameById(gameId,player1Score, player2Score);
    }

    public String sendChatMessageService(long currentPlayerId,long opponentPlayerId, String message) {
        return gameDao.SendChatMessageDao(currentPlayerId, opponentPlayerId, message);
    }
}
