package edu.sustech.xiangqi.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameSave implements Serializable {
    private final List<PieceData> piecesData;
    private final boolean isRedTurn;
    private final String gameStatus;
    private final long saveTime;
    private String saveName;
    private final GameMode gameMode;
    private final int redRemainingTime;
    private final int blackRemainingTime;
    private final List<MoveRecord> moveRecords; // 步数记录

    // 完整构造函数
    public GameSave(List<AbstractPiece> pieces, boolean isRedTurn, String gameStatus, GameMode gameMode, int redRemainingTime, int blackRemainingTime, List<MoveRecord> moveRecords) {
        this.piecesData = new ArrayList<>();
        for (AbstractPiece piece : pieces) {
            this.piecesData.add(new PieceData(piece));
        }
        this.isRedTurn = isRedTurn;
        this.gameStatus = gameStatus;
        this.saveTime = System.currentTimeMillis();
        this.saveName = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(saveTime));
        this.gameMode = gameMode;
        this.redRemainingTime = redRemainingTime;
        this.blackRemainingTime = blackRemainingTime;
        this.moveRecords = new ArrayList<>(moveRecords);
    }

    // 兼容旧构造
    public GameSave(List<AbstractPiece> pieces, boolean isRedTurn, String gameStatus) {
        this(pieces, isRedTurn, gameStatus, GameMode.NORMAL, 300, 300, new ArrayList<>());
    }

    public GameSave(List<AbstractPiece> pieces, boolean isRedTurn, String gameStatus, GameMode gameMode, int redRemainingTime, int blackRemainingTime) {
        this(pieces, isRedTurn, gameStatus, gameMode, redRemainingTime, blackRemainingTime, new ArrayList<>());
    }

    // Getter
    public List<PieceData> getPiecesData() { return piecesData; }
    public boolean isRedTurn() { return isRedTurn; }
    public String getGameStatus() { return gameStatus; }
    public long getSaveTime() { return saveTime; }
    public String getSaveName() { return saveName; }
    public void setSaveName(String saveName) { this.saveName = saveName; }
    public GameMode getGameMode() { return gameMode; }
    public int getRedRemainingTime() { return redRemainingTime; }
    public int getBlackRemainingTime() { return blackRemainingTime; }
    public List<MoveRecord> getMoveRecords() { return new ArrayList<>(moveRecords); }

    // 内部类
    public static class PieceData implements Serializable {
        private final String type;
        private final String name;
        private final int row;
        private final int col;
        private final boolean isRed;

        public PieceData(AbstractPiece piece) {
            this.type = piece.getClass().getSimpleName();
            this.name = piece.getName();
            this.row = piece.getRow();
            this.col = piece.getCol();
            this.isRed = piece.isRed();
        }

        public String getType() { return type; }
        public String getName() { return name; }
        public int getRow() { return row; }
        public int getCol() { return col; }
        public boolean isRed() { return isRed; }
    }
}