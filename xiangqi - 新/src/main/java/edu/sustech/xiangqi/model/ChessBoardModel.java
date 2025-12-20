package edu.sustech.xiangqi.model;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private GameMode gameMode;
    private int redRemainingTime;
    private int blackRemainingTime;
    private Timer timeTimer;
    private boolean isTimeOut = false;
    private List<MoveRecord> moveRecords = new ArrayList<>();
    private int replayIndex = -1;

    // 构造函数
    public ChessBoardModel(GameMode gameMode) {
        this.gameMode = gameMode;
        pieces = new ArrayList<>();
        moveRecords = new ArrayList<>();
        initializePieces();
        initTimer();
    }

    public ChessBoardModel() {
        this(GameMode.NORMAL);
    }

    public ChessBoardModel(GameSave save) {
        this.gameMode = save.getGameMode();
        pieces = new ArrayList<>();
        moveRecords = save.getMoveRecords();
        replayIndex = -1;
        loadFromSave(save);
        this.redRemainingTime = save.getRedRemainingTime();
        this.blackRemainingTime = save.getBlackRemainingTime();
        initTimer();
    }

    // 初始化棋子
    private void initializePieces() {
        pieces.clear();
        // 黑方棋子
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
        // 红方棋子
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

    // 定时器初始化
    private void initTimer() {
        if (gameMode == GameMode.TIME_LIMITED) {
            if (redRemainingTime <= 0) redRemainingTime = 300;
            if (blackRemainingTime <= 0) blackRemainingTime = 300;

            timeTimer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isGameOver() || isTimeOut || replayIndex != -1) {
                        timeTimer.stop();
                        return;
                    }
                    if (isRedTurn) {
                        redRemainingTime--;
                        if (redRemainingTime <= 0) {
                            timeOutHandle(true);
                        }
                    } else {
                        blackRemainingTime--;
                        if (blackRemainingTime <= 0) {
                            timeOutHandle(false);
                        }
                    }
                }
            });
            timeTimer.start();
        }
    }

    // 超时处理
    private void timeOutHandle(boolean isRedTimeOut) {
        isTimeOut = true;
        if (timeTimer != null) timeTimer.stop();
        String winner = isRedTimeOut ? "黑方" : "红方";
        gameStatus = winner + "胜利（对方超时）！";
    }

    // 切换回合
    public void switchTurn() {
        isRedTurn = !isRedTurn;
        if (!isGameOver() && !isTimeOut && replayIndex == -1) {
            gameStatus = (isRedTurn ? "红方" : "黑方") + "回合";
            if (gameMode == GameMode.TIME_LIMITED && timeTimer != null) {
                timeTimer.start();
            }
        }
    }

    // 重新开始
    public void restartGame() {
        initializePieces();
        moveRecords.clear();
        replayIndex = -1;
        isTimeOut = false;
        if (timeTimer != null) {
            timeTimer.stop();
        }
        initTimer();
    }

    // 创建存档
    public GameSave createSave() {
        return new GameSave(pieces, isRedTurn, gameStatus, gameMode, redRemainingTime, blackRemainingTime, moveRecords);
    }

    // 时间格式化
    public String getRedTimeStr() {
        return String.format("%02d:%02d", redRemainingTime / 60, redRemainingTime % 60);
    }

    public String getBlackTimeStr() {
        return String.format("%02d:%02d", blackRemainingTime / 60, blackRemainingTime % 60);
    }

    // 停止定时器
    public void stopTimer() {
        if (timeTimer != null) {
            timeTimer.stop();
        }
    }

    // 将军检测（公开方法）
    public boolean wouldBeInCheck(AbstractPiece piece, int newRow, int newCol) {
        int oldRow = piece.getRow();
        int oldCol = piece.getCol();
        AbstractPiece capturedPiece = getPieceAt(newRow, newCol);
        boolean captured = false;

        piece.moveTo(newRow, newCol);
        if (capturedPiece != null) {
            pieces.remove(capturedPiece);
            captured = true;
        }

        boolean inCheck = isInCheck(piece.isRed());

        piece.moveTo(oldRow, oldCol);
        if (captured) {
            pieces.add(capturedPiece);
        }
        return inCheck;
    }

    // 移动棋子（移除内部弹窗）
    public boolean movePiece(AbstractPiece piece, int newRow, int newCol) {
        if (replayIndex != -1) return false;
        if (!isValidPosition(newRow, newCol)) return false;
        if (piece.isRed() != isRedTurn) return false;
        if (!piece.canMoveTo(newRow, newCol, this)) return false;
        if (wouldBeInCheck(piece, newRow, newCol)) return false;

        AbstractPiece targetPiece = getPieceAt(newRow, newCol);
        int oldRow = piece.getRow();
        int oldCol = piece.getCol();

        if (targetPiece != null) {
            if (targetPiece.isRed() == piece.isRed()) return false;
            pieces.remove(targetPiece);
            if (targetPiece instanceof GeneralPiece) {
                gameStatus = (piece.isRed() ? "红方" : "黑方") + "胜利！";
                piece.moveTo(newRow, newCol);
                recordMove(piece, oldRow, oldCol, newRow, newCol, targetPiece);
                moveRecords.add(new MoveRecord(piece, oldRow, oldCol, newRow, newCol, targetPiece));
                stopTimer();
                return true;
            }
        }

        piece.moveTo(newRow, newCol);
        recordMove(piece, oldRow, oldCol, newRow, newCol, targetPiece);
        moveRecords.add(new MoveRecord(piece, oldRow, oldCol, newRow, newCol, targetPiece));
        switchTurn();

        if (isInCheck(!piece.isRed())) {
            if (isCheckmated(!piece.isRed())) {
                gameStatus = (piece.isRed() ? "红方" : "黑方") + "胜利（将死）！";
                stopTimer();
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

    // 将军检测
    public boolean isInCheck(boolean isRedKing) {
        AbstractPiece king = null;
        for (AbstractPiece piece : pieces) {
            if (piece instanceof GeneralPiece && piece.isRed() == isRedKing) {
                king = piece;
                break;
            }
        }
        if (king == null) return false;

        for (AbstractPiece piece : pieces) {
            if (piece.isRed() != isRedKing) {
                if (piece.canMoveTo(king.getRow(), king.getCol(), this)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 将死检测
    public boolean isCheckmated(boolean isRedKing) {
        if (!isInCheck(isRedKing)) return false;

        for (AbstractPiece piece : new ArrayList<>(pieces)) {
            if (piece.isRed() != isRedKing) continue;
            int originalRow = piece.getRow();
            int originalCol = piece.getCol();

            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (piece.canMoveTo(row, col, this)) {
                        AbstractPiece targetPiece = getPieceAt(row, col);
                        if (targetPiece != null && targetPiece.isRed() == piece.isRed()) continue;

                        piece.moveTo(row, col);
                        boolean captured = false;
                        if (targetPiece != null) {
                            pieces.remove(targetPiece);
                            captured = true;
                        }

                        boolean stillInCheck = isInCheck(isRedKing);

                        piece.moveTo(originalRow, originalCol);
                        if (captured) pieces.add(targetPiece);

                        if (!stillInCheck) return false;
                    }
                }
            }
        }
        return true;
    }

    // 回放相关方法
    public void resetReplay() {
        replayIndex = -1;
        initializePieces();
        stopTimer();
    }

    public boolean nextReplayStep() {
        if (moveRecords.isEmpty()) return false;
        if (replayIndex == -1) resetReplay();
        if (replayIndex >= moveRecords.size() - 1) return false;

        replayIndex++;
        executeReplayStep(replayIndex);
        return true;
    }

    public boolean prevReplayStep() {
        if (replayIndex <= 0) {
            resetReplay();
            return false;
        }

        replayIndex--;
        resetReplay();
        for (int i = 0; i <= replayIndex; i++) {
            executeReplayStep(i);
        }
        return true;
    }

    private void executeReplayStep(int index) {
        MoveRecord record = moveRecords.get(index);
        AbstractPiece piece = findPieceByRecord(record);
        if (piece == null) return;

        AbstractPiece targetPiece = getPieceAt(record.getTargetRow(), record.getTargetCol());
        if (targetPiece != null) pieces.remove(targetPiece);

        piece.moveTo(record.getTargetRow(), record.getTargetCol());
        gameStatus = record.isRed() ? "黑方回合" : "红方回合";
        isRedTurn = !record.isRed();
        lastMoveRecord = String.format("%s方%s从(%d,%d)%s(%d,%d)",
                record.isRed() ? "红" : "黑",
                record.getPieceName(),
                record.getStartRow(),
                record.getStartCol(),
                record.getCapturedPieceName() != null ? "吃" : "移至",
                record.getTargetRow(),
                record.getTargetCol());
    }

    private AbstractPiece findPieceByRecord(MoveRecord record) {
        for (AbstractPiece piece : pieces) {
            if (piece.getClass().getSimpleName().equals(record.getPieceType())
                    && piece.getName().equals(record.getPieceName())
                    && piece.isRed() == record.isRed()
                    && piece.getRow() == record.getStartRow()
                    && piece.getCol() == record.getStartCol()) {
                return piece;
            }
        }
        return null;
    }

    // 工具方法
    public List<AbstractPiece> getPieces() { return new ArrayList<>(pieces); }
    public AbstractPiece getPieceAt(int row, int col) {
        for (AbstractPiece piece : pieces) {
            if (piece.getRow() == row && piece.getCol() == col) return piece;
        }
        return null;
    }
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }
    public boolean isRedTurn() { return isRedTurn; }
    public String getGameStatus() { return gameStatus; }
    public static int getRows() { return ROWS; }
    public static int getCols() { return COLS; }
    public void loadFromSave(GameSave save) {
        pieces.clear();
        for (GameSave.PieceData pieceData : save.getPiecesData()) {
            AbstractPiece piece = createPieceFromData(pieceData);
            if (piece != null) pieces.add(piece);
        }
        isRedTurn = save.isRedTurn();
        gameStatus = save.getGameStatus();
        lastMoveRecord = "";
        lastMovedPiece = null;
    }
    private AbstractPiece createPieceFromData(GameSave.PieceData pieceData) {
        String type = pieceData.getType();
        String name = pieceData.getName();
        int row = pieceData.getRow();
        int col = pieceData.getCol();
        boolean isRed = pieceData.isRed();
        switch (type) {
            case "RookPiece": return new RookPiece(name, row, col, isRed);
            case "HorsePiece": return new HorsePiece(name, row, col, isRed);
            case "ElephantPiece": return new ElephantPiece(name, row, col, isRed);
            case "AdvisorPiece": return new AdvisorPiece(name, row, col, isRed);
            case "GeneralPiece": return new GeneralPiece(name, row, col, isRed);
            case "CannonPiece": return new CannonPiece(name, row, col, isRed);
            case "SoldierPiece": return new SoldierPiece(name, row, col, isRed);
            default: return null;
        }
    }
    public boolean isGameOver() { return gameStatus.contains("胜利"); }
    public String getCurrentPlayerColor() { return isRedTurn ? "红方" : "黑方"; }
    public String getLastMoveRecord() { return lastMoveRecord.isEmpty() ? "无" : lastMoveRecord; }
    public void setGameStatus(String status) {
        this.gameStatus = status;
        stopTimer();
    }
    public GameMode getGameMode() { return gameMode; }
    public int getRedRemainingTime() { return redRemainingTime; }
    public int getBlackRemainingTime() { return blackRemainingTime; }
    public boolean isReplaying() { return replayIndex != -1; }
    public int getReplayIndex() { return replayIndex; }
    public int getTotalReplaySteps() { return moveRecords.size(); }
}