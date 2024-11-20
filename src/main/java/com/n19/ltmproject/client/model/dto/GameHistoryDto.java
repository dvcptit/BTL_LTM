package com.n19.ltmproject.client.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameHistoryDto {
    private long id;
    private long player1Id;
    private String player1Name;
    private long player1Score;
    private long player2Id;
    private String player2Name;
    private long player2Score;
    private String startTime;
    private String endTime;
}
