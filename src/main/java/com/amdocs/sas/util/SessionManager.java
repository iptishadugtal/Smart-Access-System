package com.amdocs.sas.util;

public class SessionManager {
    private static SessionManager instance;
    private String role;
    private String username;
    private int userId;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setSession(String role, String username, int userId) {
        this.role = role;
        this.username = username;
        this.userId = userId;
    }

    public void displayCurrentUser() {
        System.out.println(" Logged in as: " + role + " | Username: " + username);
    }

    public void clearSession() {
        System.out.println(" Logged out from session: " + role);
        role = null;
        username = null;
        userId = -1;
    }
}
