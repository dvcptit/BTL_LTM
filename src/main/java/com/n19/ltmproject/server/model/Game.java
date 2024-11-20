package com.n19.ltmproject.server.model;

import lombok.*;

import javax.persistence.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameId;

    private long player1Id;

    private long player2Id;

    private long player1Score;

    private long player2Score;

    private String startTime;

    private String endTime;
}
