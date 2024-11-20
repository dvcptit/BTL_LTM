package com.n19.ltmproject.client.model;

import com.n19.ltmproject.client.model.enums.PlayerStatus;
import lombok.*;

@Data
public class Player {

    private long id;
    private String username;
    private String password;
    private PlayerStatus status;

    public Player(long id, String username, String password, PlayerStatus status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
    }
}
