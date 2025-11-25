package edu.sustech.xiangqi.model;

/**
 * 马
 */
public class HorsePiece extends AbstractPiece {

    public HorsePiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        int currentRow = getRow();
        int currentCol = getCol();

        if (currentRow == targetRow && currentCol == targetCol) {
            return false;
        }

        int rowDiff = Math.abs(targetRow - currentRow);
        int colDiff = Math.abs(targetCol - currentCol);

        // 马走"日"字
        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) {
            return false;
        }

        // 检查是否蹩马腿
        if (rowDiff == 2) {
            // 竖向移动，检查中间的棋子
            int middleRow = currentRow + (targetRow - currentRow) / 2;
            if (model.getPieceAt(middleRow, currentCol) != null) {
                return false;
            }
        } else {
            // 横向移动，检查中间的棋子
            int middleCol = currentCol + (targetCol - currentCol) / 2;
            if (model.getPieceAt(currentRow, middleCol) != null) {
                return false;
            }
        }

        return true;
    }
}