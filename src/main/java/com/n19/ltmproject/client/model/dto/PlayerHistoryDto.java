package com.n19.ltmproject.client.model.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerHistoryDto {

    private long id;
    private String username;
    private int totalGames;
    private int wins;
    private int losses;
    private int draws;
    private int totalPoints;
}
