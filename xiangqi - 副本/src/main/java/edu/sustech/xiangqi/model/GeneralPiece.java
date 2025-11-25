package edu.sustech.xiangqi.model;

/**
 * 帅/将
 */
public class GeneralPiece extends AbstractPiece {

    public GeneralPiece(String name, int row, int col, boolean isRed) {
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

        // 将/帅只能在九宫内移动
        if (isRed()) {
            // 红方九宫：行7-9，列3-5
            if (targetRow < 7 || targetRow > 9 || targetCol < 3 || targetCol > 5) {
                return false;
            }
        } else {
            // 黑方九宫：行0-2，列3-5
            if (targetRow < 0 || targetRow > 2 || targetCol < 3 || targetCol > 5) {
                return false;
            }
        }

        // 只能移动一步（横或竖）
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }
}