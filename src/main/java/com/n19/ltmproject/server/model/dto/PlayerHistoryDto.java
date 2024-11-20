package com.n19.ltmproject.server.model.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerHistoryDto {

    private String username;
    private int totalGames;
    private int wins;
    private int draws;
    private int losses;
    private int totalPoints;
}
