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

        // 检查将帅对面情况（同一列且中间无棋子）
        if (currentCol == targetCol && rowDiff > 1) {
            // 确保目标位置是对方将/帅
            AbstractPiece targetPiece = model.getPieceAt(targetRow, targetCol);
            if (targetPiece instanceof GeneralPiece && targetPiece.isRed() != isRed()) {
                // 检查中间是否有棋子阻挡
                boolean hasObstacle = false;
                int startRow = Math.min(currentRow, targetRow) + 1;
                int endRow = Math.max(currentRow, targetRow);

                for (int r = startRow; r < endRow; r++) {
                    if (model.getPieceAt(r, currentCol) != null) {
                        hasObstacle = true;
                        break;
                    }
                }
                if (!hasObstacle) {
                    return true; // 允许将帅对面吃子
                }
            }
        }

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