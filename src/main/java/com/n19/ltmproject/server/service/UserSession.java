package com.n19.ltmproject.server.service;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class UserSession {

    private static final Map<String, Session> sessions = new HashMap<>();

    public static void addSession( String username) {
        Session session = new Session(username);
        sessions.put(username, session);
    }

    public static Session getSession(String username) {
        return sessions.get(username);
    }

    public static void removeSession(String username) {
        sessions.remove(username);
    }

    public static boolean isUserLoggedIn(String username) {
        return sessions.containsKey(username);
    }
    public static void setSession(UserSession otherSession) {
        sessions.clear();

        sessions.putAll(getAllSessions());
    }

    public static Map<String, Session> getAllSessions() {
        return sessions;
    }
}
