package edu.sustech.xiangqi.model;

/**
 * 炮
 */
public class CannonPiece extends AbstractPiece {

    public CannonPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        int currentRow = getRow();
        int currentCol = getCol();

        if (currentRow == targetRow && currentCol == targetCol) {
            return false;
        }

        // 炮只能直线移动
        if (currentRow != targetRow && currentCol != targetCol) {
            return false;
        }

        AbstractPiece targetPiece = model.getPieceAt(targetRow, targetCol);
        int piecesBetween = 0;

        // 计算路径上的棋子数量
        if (currentRow == targetRow) {
            // 横向移动
            int start = Math.min(currentCol, targetCol) + 1;
            int end = Math.max(currentCol, targetCol);
            for (int col = start; col < end; col++) {
                if (model.getPieceAt(currentRow, col) != null) {
                    piecesBetween++;
                }
            }
        } else {
            // 纵向移动
            int start = Math.min(currentRow, targetRow) + 1;
            int end = Math.max(currentRow, targetRow);
            for (int row = start; row < end; row++) {
                if (model.getPieceAt(row, currentCol) != null) {
                    piecesBetween++;
                }
            }
        }

        // 炮的移动规则：
        // - 移动时：路径上不能有棋子 (piecesBetween == 0)
        // - 吃子时：路径上必须正好有一个棋子 (piecesBetween == 1)
        if (targetPiece == null) {
            // 移动
            return piecesBetween == 0;
        } else {
            // 吃子
            return piecesBetween == 1 && targetPiece.isRed() != isRed();
        }
    }
}