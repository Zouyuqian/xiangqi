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
    private String saveName; // 新增：存档名称

    public GameSave(List<AbstractPiece> pieces, boolean isRedTurn, String gameStatus) {
        this.piecesData = new ArrayList<>();
        for (AbstractPiece piece : pieces) {
            this.piecesData.add(new PieceData(piece));
        }
        this.isRedTurn = isRedTurn;
        this.gameStatus = gameStatus;
        this.saveTime = System.currentTimeMillis();
        // 自动生成存档名称（包含时间）
        this.saveName = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(saveTime));
    }

    // Getters and Setters
    public List<PieceData> getPiecesData() { return piecesData; }
    public boolean isRedTurn() { return isRedTurn; }
    public String getGameStatus() { return gameStatus; }
    public long getSaveTime() { return saveTime; }
    public String getSaveName() { return saveName; }
    public void setSaveName(String saveName) { this.saveName = saveName; }

    // 内部类保持不变
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