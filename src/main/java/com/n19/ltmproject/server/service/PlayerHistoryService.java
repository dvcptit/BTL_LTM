package com.n19.ltmproject.server.service;

import com.n19.ltmproject.server.dao.PlayerHistoryDao;
import com.n19.ltmproject.server.model.dto.PlayerHistoryDto;

import java.util.List;

public class PlayerHistoryService {

    private final PlayerHistoryDao playerHistoryDao;

    public PlayerHistoryService() {
        this.playerHistoryDao = new PlayerHistoryDao();
    }

    public List<PlayerHistoryDto> getAllPlayerHistory() {
        return playerHistoryDao.getAllPlayerHistory();
    }

    public boolean updateGameHistory(long playerId, boolean isWin, boolean isDraw) {
        return playerHistoryDao.updateGameHistory(playerId, isWin, isDraw);
    }
    public PlayerHistoryDto getPlayerHistoryById(long playerId) {
        return playerHistoryDao.getPlayerHistoryById(playerId);
    }
}
