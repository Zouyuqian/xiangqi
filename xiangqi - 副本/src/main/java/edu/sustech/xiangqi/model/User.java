package edu.sustech.xiangqi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private final String username;
    private final String password;
    private final boolean isGuest;
    // 改为存档列表，支持多个存档
    private List<GameSave> saves = new ArrayList<>();

    public User(String username, String password, boolean isGuest) {
        this.username = username;
        this.password = password;
        this.isGuest = isGuest;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public boolean isGuest() { return isGuest; }
    public List<GameSave> getSaves() { return saves; }

    // 添加新存档
    public void addSave(GameSave save) {
        saves.add(save);
    }

    // 更新指定存档
    public void updateSave(int index, GameSave save) {
        if (index >= 0 && index < saves.size()) {
            saves.set(index, save);
        }
    }

    // 删除存档
    public void deleteSave(int index) {
        if (index >= 0 && index < saves.size()) {
            saves.remove(index);
        }
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}