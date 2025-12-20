package edu.sustech.xiangqi.model;

public enum GameMode {
    NORMAL("普通模式（无时间限制）"),
    TIME_LIMITED("时间限制模式（双方各5分钟）");

    private final String desc;

    GameMode(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}