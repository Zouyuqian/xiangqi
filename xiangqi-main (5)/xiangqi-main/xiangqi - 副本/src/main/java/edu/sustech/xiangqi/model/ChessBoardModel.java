package edu.sustech.xiangqi.model;

import java.util.ArrayList;
import java.util.List;


public class ChessBoardModel {
    private final List<AbstractPiece> pieces;
    private boolean isRedTurn = true;
    private static final int ROWS = 10;
    private static final int COLS = 9;
    private String gameStatus = "红方回合";

    public ChessBoardModel() {
        pieces = new ArrayList<>();
        initializePieces();
    }

    // 从保存状态初始化
    public ChessBoardModel(GameSave save) {
        pieces = new ArrayList<>();
        loadFromSave(save);
    }

    private void initializePieces() {
        pieces.clear();

        // 黑方棋子（上方）
        pieces.add(new RookPiece("車", 0, 0, false));
        pieces.add(new HorsePiece("馬", 0, 1, false));
        pieces.add(new ElephantPiece("象", 0, 2, false));
        pieces.add(new AdvisorPiece("士", 0, 3, false));
        pieces.add(new GeneralPiece("將", 0, 4, false));
        pieces.add(new AdvisorPiece("士", 0, 5, false));
        pieces.add(new ElephantPiece("象", 0, 6, false));
        pieces.add(new HorsePiece("馬", 0, 7, false));
        pieces.add(new RookPiece("車", 0, 8, false));
        pieces.add(new CannonPiece("炮", 2, 1, false));
        pieces.add(new CannonPiece("炮", 2, 7, false));
        pieces.add(new SoldierPiece("卒", 3, 0, false));
        pieces.add(new SoldierPiece("卒", 3, 2, false));
        pieces.add(new SoldierPiece("卒", 3, 4, false));
        pieces.add(new SoldierPiece("卒", 3, 6, false));
        pieces.add(new SoldierPiece("卒", 3, 8, false));

        // 红方棋子（下方）
        pieces.add(new RookPiece("车", 9, 0, true));
        pieces.add(new HorsePiece("马", 9, 1, true));
        pieces.add(new ElephantPiece("相", 9, 2, true));
        pieces.add(new AdvisorPiece("仕", 9, 3, true));
        pieces.add(new GeneralPiece("帅", 9, 4, true));
        pieces.add(new AdvisorPiece("仕", 9, 5, true));
        pieces.add(new ElephantPiece("相", 9, 6, true));
        pieces.add(new HorsePiece("马", 9, 7, true));
        pieces.add(new RookPiece("车", 9, 8, true));
        pieces.add(new CannonPiece("炮", 7, 1, true));
        pieces.add(new CannonPiece("炮", 7, 7, true));
        pieces.add(new SoldierPiece("兵", 6, 0, true));
        pieces.add(new SoldierPiece("兵", 6, 2, true));
        pieces.add(new SoldierPiece("兵", 6, 4, true));
        pieces.add(new SoldierPiece("兵", 6, 6, true));
        pieces.add(new SoldierPiece("兵", 6, 8, true));

        isRedTurn = true;
        gameStatus = "红方回合";
    }

    public List<AbstractPiece> getPieces() {
        return new ArrayList<>(pieces);
    }

    public AbstractPiece getPieceAt(int row, int col) {
        for (AbstractPiece piece : pieces) {
            if (piece.getRow() == row && piece.getCol() == col) {
                return piece;
            }
        }
        return null;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    public boolean movePiece(AbstractPiece piece, int newRow, int newCol) {
        if (!isValidPosition(newRow, newCol)) {
            return false;
        }

        // 检查回合
        if (piece.isRed() != isRedTurn) {
            return false;
        }

        if (!piece.canMoveTo(newRow, newCol, this)) {
            return false;
        }

        // 处理吃子
        AbstractPiece targetPiece = getPieceAt(newRow, newCol);
        if (targetPiece != null) {
            if (targetPiece.isRed() == piece.isRed()) {
                return false;
            }
            pieces.remove(targetPiece);

            // 检查是否吃掉了将/帅
            if (targetPiece instanceof GeneralPiece) {
                gameStatus = (piece.isRed() ? "红方" : "黑方") + "胜利！";
                piece.moveTo(newRow, newCol);
                return true;
            }
        }

        piece.moveTo(newRow, newCol);
        switchTurn();
        return true;
    }

    public boolean isRedTurn() {
        return isRedTurn;
    }

    public void switchTurn() {
        isRedTurn = !isRedTurn;
        gameStatus = (isRedTurn ? "红方" : "黑方") + "回合";
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void restartGame() {
        initializePieces();
    }

    public static int getRows() {
        return ROWS;
    }

    public static int getCols() {
        return COLS;
    }

    // 保存游戏状态
    public GameSave createSave() {
        return new GameSave(pieces, isRedTurn, gameStatus);
    }

    // 从保存状态加载游戏
    public void loadFromSave(GameSave save) {
        pieces.clear();

        for (GameSave.PieceData pieceData : save.getPiecesData()) {
            AbstractPiece piece = createPieceFromData(pieceData);
            if (piece != null) {
                pieces.add(piece);
            }
        }

        isRedTurn = save.isRedTurn();
        gameStatus = save.getGameStatus();
    }

    // 根据保存的数据创建棋子对象
    private AbstractPiece createPieceFromData(GameSave.PieceData pieceData) {
        String type = pieceData.getType();
        String name = pieceData.getName();
        int row = pieceData.getRow();
        int col = pieceData.getCol();
        boolean isRed = pieceData.isRed();

        switch (type) {
            case "RookPiece":
                return new RookPiece(name, row, col, isRed);
            case "HorsePiece":
                return new HorsePiece(name, row, col, isRed);
            case "ElephantPiece":
                return new ElephantPiece(name, row, col, isRed);
            case "AdvisorPiece":
                return new AdvisorPiece(name, row, col, isRed);
            case "GeneralPiece":
                return new GeneralPiece(name, row, col, isRed);
            case "CannonPiece":
                return new CannonPiece(name, row, col, isRed);
            case "SoldierPiece":
                return new SoldierPiece(name, row, col, isRed);
            default:
                return null;
        }
    }

    // 检查游戏是否结束
    public boolean isGameOver() {
        return gameStatus.contains("胜利");
    }

    // 获取当前回合的玩家颜色
    public String getCurrentPlayerColor() {
        return isRedTurn ? "红方" : "黑方";
    }
}