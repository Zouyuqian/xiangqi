package edu.sustech.xiangqi.model;

import java.util.ArrayList;
import java.util.List;


public class ChessBoardModel {
    private final List<AbstractPiece> pieces;
    private boolean isRedTurn = true;
    private static final int ROWS = 10;
    private static final int COLS = 9;
    private String gameStatus = "红方回合";
    private String lastMoveRecord = "";
    private AbstractPiece lastMovedPiece = null;

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
        lastMoveRecord = "";
        lastMovedPiece = null;
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

        // 检查移动后是否会导致自将军
        if (wouldBeInCheck(piece, newRow, newCol)) {
            return false;
        }

        // 处理吃子
        AbstractPiece targetPiece = getPieceAt(newRow, newCol);
        int oldRow = piece.getRow();
        int oldCol = piece.getCol();

        if (targetPiece != null) {
            if (targetPiece.isRed() == piece.isRed()) {
                return false;
            }
            pieces.remove(targetPiece);

            // 检查是否吃掉了将/帅
            if (targetPiece instanceof GeneralPiece) {
                gameStatus = (piece.isRed() ? "红方" : "黑方") + "胜利！";
                piece.moveTo(newRow, newCol);
                recordMove(piece, oldRow, oldCol, newRow, newCol, targetPiece);
                return true;
            }
        }

        piece.moveTo(newRow, newCol);
        recordMove(piece, oldRow, oldCol, newRow, newCol, targetPiece);
        switchTurn();

        // 检查移动后是否将军或将死
        if (isInCheck(!piece.isRed())) {
            if (isCheckmated(!piece.isRed())) {
                gameStatus = (piece.isRed() ? "红方" : "黑方") + "胜利（将死）！";
            } else {
                gameStatus = (isRedTurn ? "红方" : "黑方") + "回合（被将军）";
            }
        }

        return true;
    }

    // 记录移动
    private void recordMove(AbstractPiece piece, int oldRow, int oldCol, int newRow, int newCol, AbstractPiece capturedPiece) {
        String color = piece.isRed() ? "红" : "黑";
        String action = capturedPiece != null ? "吃" : "移至";
        lastMoveRecord = String.format("%s方%s从(%d,%d)%s(%d,%d)",
                color, piece.getName(), oldRow, oldCol, action, newRow, newCol);
        lastMovedPiece = piece;
    }

    // 检查当前是否将军
    public boolean isInCheck(boolean isRedKing) {
        AbstractPiece king = null;

        // 找到当前方的将/帅
        for (AbstractPiece piece : pieces) {
            if (piece instanceof GeneralPiece && piece.isRed() == isRedKing) {
                king = piece;
                break;
            }
        }

        if (king == null) return false; // 帅/将已经被吃掉

        // 检查对方所有棋子是否能攻击到帅/将
        for (AbstractPiece piece : pieces) {
            if (piece.isRed() != isRedKing) { // 对方棋子
                if (piece.canMoveTo(king.getRow(), king.getCol(), this)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 移动后检查是否将军
    private boolean wouldBeInCheck(AbstractPiece piece, int newRow, int newCol) {
        // 保存当前状态
        int oldRow = piece.getRow();
        int oldCol = piece.getCol();
        AbstractPiece capturedPiece = getPieceAt(newRow, newCol);
        boolean captured = false;

        // 模拟移动
        piece.moveTo(newRow, newCol);
        if (capturedPiece != null) {
            pieces.remove(capturedPiece);
            captured = true;
        }

        // 检查是否将军
        boolean inCheck = isInCheck(piece.isRed());

        // 恢复状态
        piece.moveTo(oldRow, oldCol);
        if (captured) {
            pieces.add(capturedPiece);
        }

        return inCheck;
    }

    // 检查是否将死
    public boolean isCheckmated(boolean isRedKing) {
        if (!isInCheck(isRedKing)) {
            return false; // 没有将军，不算将死
        }

        // 尝试所有可能的移动，看是否能解除将军
        for (AbstractPiece piece : new ArrayList<>(pieces)) {
            if (piece.isRed() != isRedKing) {
                continue; // 只检查当前方的棋子
            }

            int originalRow = piece.getRow();
            int originalCol = piece.getCol();

            // 尝试移动到棋盘上的所有位置
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (piece.canMoveTo(row, col, this)) {
                        AbstractPiece targetPiece = getPieceAt(row, col);
                        if (targetPiece != null && targetPiece.isRed() == piece.isRed()) {
                            continue; // 不能吃自己的棋子
                        }

                        // 模拟移动
                        piece.moveTo(row, col);
                        boolean captured = false;
                        if (targetPiece != null) {
                            pieces.remove(targetPiece);
                            captured = true;
                        }

                        // 检查是否解除将军
                        boolean stillInCheck = isInCheck(isRedKing);

                        // 恢复状态
                        piece.moveTo(originalRow, originalCol);
                        if (captured) {
                            pieces.add(targetPiece);
                        }

                        if (!stillInCheck) {
                            return false; // 找到可以解除将军的移动，不是将死
                        }
                    }
                }
            }
        }

        return true; // 所有移动都无法解除将军，将死
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
        lastMoveRecord = "";
        lastMovedPiece = null;
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

    public String getLastMoveRecord() {
        return lastMoveRecord.isEmpty() ? "无" : lastMoveRecord;
    }

    public void setGameStatus(String status) {
        this.gameStatus = status;
    }
}