package edu.sustech.xiangqi.model;

import java.io.Serializable;

public class MoveRecord implements Serializable {
    private final String pieceType;
    private final String pieceName;
    private final boolean isRed;
    private final int startRow;
    private final int startCol;
    private final int targetRow;
    private final int targetCol;
    private final String capturedPieceName;

    public MoveRecord(AbstractPiece piece, int startRow, int startCol, int targetRow, int targetCol, AbstractPiece capturedPiece) {
        this.pieceType = piece.getClass().getSimpleName();
        this.pieceName = piece.getName();
        this.isRed = piece.isRed();
        this.startRow = startRow;
        this.startCol = startCol;
        this.targetRow = targetRow;
        this.targetCol = targetCol;
        this.capturedPieceName = capturedPiece != null ? capturedPiece.getName() : null;
    }

    public String getPieceType() { return pieceType; }
    public String getPieceName() { return pieceName; }
    public boolean isRed() { return isRed; }
    public int getStartRow() { return startRow; }
    public int getStartCol() { return startCol; }
    public int getTargetRow() { return targetRow; }
    public int getTargetCol() { return targetCol; }
    public String getCapturedPieceName() { return capturedPieceName; }
}