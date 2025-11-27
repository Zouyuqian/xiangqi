package edu.sustech.xiangqi.model;

/**
 * 士/仕
 */
public class AdvisorPiece extends AbstractPiece {

    public AdvisorPiece(String name, int row, int col, boolean isRed) {
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

        // 士/仕只能斜线移动一格
        if (rowDiff != 1 || colDiff != 1) {
            return false;
        }

        // 士/仕只能在九宫内移动
        if (isRed()) {
            // 红方九宫：行7-9，列3-5
            return targetRow >= 7 && targetRow <= 9 && targetCol >= 3 && targetCol <= 5;
        } else {
            // 黑方九宫：行0-2，列3-5
            return targetRow >= 0 && targetRow <= 2 && targetCol >= 3 && targetCol <= 5;
        }
    }
}