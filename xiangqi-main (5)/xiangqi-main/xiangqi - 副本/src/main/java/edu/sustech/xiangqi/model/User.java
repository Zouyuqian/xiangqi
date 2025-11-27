package edu.sustech.xiangqi.model;

import java.io.Serializable;

public class User implements Serializable {
    private final String username;
    private final String password;
    private final boolean isGuest;
    private GameSave currentSave;

    public User(String username, String password, boolean isGuest) {
        this.username = username;
        this.password = password;
        this.isGuest = isGuest;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public boolean isGuest() { return isGuest; }
    public GameSave getCurrentSave() { return currentSave; }
    public void setCurrentSave(GameSave save) { this.currentSave = save; }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}