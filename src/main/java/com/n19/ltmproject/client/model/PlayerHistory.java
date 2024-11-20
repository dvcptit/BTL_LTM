package com.n19.ltmproject.client.model;
import lombok.*;

@Data
@Deprecated
public class PlayerHistory {

    private long id;
    private int totalPoints;
    private int totalGames;
    private int wins;
    private int losses;
    private int draws;
    private long playerId;

    public PlayerHistory(long id, int totalPoints, int totalGames, int wins, int losses, int draws, long playerId) {
        this.id = id;
        this.totalPoints = totalPoints;
        this.totalGames = totalGames;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.playerId = playerId;
    }
}
