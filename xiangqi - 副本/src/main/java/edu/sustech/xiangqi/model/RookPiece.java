package edu.sustech.xiangqi.model;

/**
 * 车
 */
public class RookPiece extends AbstractPiece {

    public RookPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        int currentRow = getRow();
        int currentCol = getCol();

        if (currentRow == targetRow && currentCol == targetCol) {
            return false;
        }

        // 车只能直线移动
        if (currentRow != targetRow && currentCol != targetCol) {
            return false;
        }

        // 检查路径上是否有其他棋子
        if (currentRow == targetRow) {
            // 横向移动
            int start = Math.min(currentCol, targetCol) + 1;
            int end = Math.max(currentCol, targetCol);
            for (int col = start; col < end; col++) {
                if (model.getPieceAt(currentRow, col) != null) {
                    return false;
                }
            }
        } else {
            // 纵向移动
            int start = Math.min(currentRow, targetRow) + 1;
            int end = Math.max(currentRow, targetRow);
            for (int row = start; row < end; row++) {
                if (model.getPieceAt(row, currentCol) != null) {
                    return false;
                }
            }
        }

        return true;
    }
}