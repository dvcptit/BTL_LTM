package com.n19.ltmproject.server.service;


@Deprecated
public class Session {

    private static String username;

    public Session(){}

    public Session(String username){
        this.username=username;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Session.username = username;
    }
}
