package com.n19.ltmproject.server.model;

import lombok.*;
import javax.persistence.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "player_history")
public class PlayerHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "player_id")
    private long playerId;

    @Column(name = "total_points")
    private int totalPoints;

    @Column(name = "total_games")
    private int totalGames;

    @Column(name = "wins")
    private int wins;

    @Column(name = "losses")
    private int losses;

    @Column(name = "draws")
    private int draws;
}
