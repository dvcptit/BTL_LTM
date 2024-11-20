package com.n19.ltmproject.server.service;

import com.n19.ltmproject.server.dao.AuthDao;
import com.n19.ltmproject.server.model.Player;

public class AuthService {

    private final AuthDao authDao;
    public AuthService() {
        this.authDao = new AuthDao();
    }

    public Player loginPlayerService(String username, String password) {
        return authDao.loginPlayerDao(username, password);
    }

    public String signUpPlayerService(String username, String password, String confirmPassword) {
        return authDao.signUpPlayerDao(username, password, confirmPassword);
    }

    public String logoutPlayerService(String username) {
        return authDao.logoutPlayerDao(username);
    }
}
