package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.model.GameMode;
import edu.sustech.xiangqi.model.GameSave;
import edu.sustech.xiangqi.model.User;
import edu.sustech.xiangqi.model.UserManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChessBoardPanel extends JPanel {
    private final ChessBoardModel model;
    private final UserManager userManager;
    private AbstractPiece selectedPiece = null;
    private final List<Point2D> validMoves = new ArrayList<>();
    private static final int CELL_SIZE = 60;
    private static final int MARGIN = 40;
    private static final int BOARD_WIDTH = 8 * CELL_SIZE + 2 * MARGIN;
    private static final int BOARD_HEIGHT = 9 * CELL_SIZE + 2 * MARGIN;

    public ChessBoardPanel(ChessBoardModel model, UserManager userManager) {
        this.model = model;
        this.userManager = userManager;
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(new Color(220, 179, 92));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }

    private void handleMouseClick(MouseEvent e) {
        if (model.isGameOver() || model.isReplaying()) {
            if (model.isGameOver() && !model.isReplaying()) {
                JOptionPane.showMessageDialog(this, "游戏已结束，可通过回放查看棋局！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }

        int x = e.getX() - MARGIN;
        int y = e.getY() - MARGIN;

        if (x < 0 || y < 0 || x >= 8 * CELL_SIZE || y >= 9 * CELL_SIZE) {
            selectedPiece = null;
            validMoves.clear();
            repaint();
            return;
        }

        int col = Math.round((float) x / CELL_SIZE);
        int row = Math.round((float) y / CELL_SIZE);

        if (!model.isValidPosition(row, col)) {
            selectedPiece = null;
            validMoves.clear();
            repaint();
            return;
        }

        AbstractPiece clickedPiece = model.getPieceAt(row, col);

        if (selectedPiece == null) {
            if (clickedPiece != null && clickedPiece.isRed() == model.isRedTurn()) {
                selectedPiece = clickedPiece;
                calculateValidMoves(selectedPiece);
                repaint();
            }
        } else {
            if (model.movePiece(selectedPiece, row, col)) {
                selectedPiece = null;
                validMoves.clear();
                repaint();
                // 恢复将军提示（排除胜利状态，避免重复弹窗）
                if (model.getGameStatus().contains("将军") && !model.getGameStatus().contains("胜利")) {
                    JOptionPane.showMessageDialog(this, model.getGameStatus(), "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (clickedPiece != null && clickedPiece.isRed() == model.isRedTurn()) {
                    selectedPiece = clickedPiece;
                    calculateValidMoves(selectedPiece);
                } else {
                    selectedPiece = null;
                    validMoves.clear();
                }
                repaint();
            }
        }
    }

    private void calculateValidMoves(AbstractPiece piece) {
        validMoves.clear();
        int rows = ChessBoardModel.getRows();
        int cols = ChessBoardModel.getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!(piece.getRow() == r && piece.getCol() == c)
                        && piece.canMoveTo(r, c, model)
                        && !model.wouldBeInCheck(piece, r, c)) {
                    validMoves.add(new Point2D.Double(r, c));
                }
            }
        }
    }

    public boolean saveGame() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser.isGuest()) {
            JOptionPane.showMessageDialog(this, "游客无法保存游戏！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            GameSave newSave = model.createSave();
            List<GameSave> saves = currentUser.getSaves();
            String[] options = new String[saves.size() + 1];
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (int i = 0; i < saves.size(); i++) {
                GameSave save = saves.get(i);
                options[i] = String.format("存档%d（%s）", i + 1, sdf.format(save.getSaveTime()));
            }
            options[saves.size()] = "新建存档";

            int choice = JOptionPane.showOptionDialog(
                    this, "选择存档位置", "保存游戏",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[saves.size()]
            );

            if (choice == saves.size()) currentUser.addSave(newSave);
            else if (choice >= 0 && choice < saves.size()) currentUser.updateSave(choice, newSave);
            else return false;

            userManager.saveCurrentUser();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "保存失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean loadGame() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser.isGuest()) {
            JOptionPane.showMessageDialog(this, "游客无法加载游戏！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        List<GameSave> saves = currentUser.getSaves();
        if (saves.isEmpty()) {
            JOptionPane.showMessageDialog(this, "无存档！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String[] options = new String[saves.size() + 1];
        for (int i = 0; i < saves.size(); i++) {
            GameSave save = saves.get(i);
            options[i] = String.format("存档%d（%s）", i + 1, sdf.format(save.getSaveTime()));
        }
        options[saves.size()] = "删除存档";

        int choice = JOptionPane.showOptionDialog(
                this, "选择存档", "加载游戏",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );

        if (choice == saves.size()) {
            deleteSaveDialog(saves);
            return false;
        } else if (choice >= 0 && choice < saves.size()) {
            int result = JOptionPane.showConfirmDialog(
                    this, "确定加载？当前进度将丢失！", "确认",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                try {
                    model.loadFromSave(saves.get(choice));
                    selectedPiece = null;
                    validMoves.clear();
                    repaint();
                    return true;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "加载失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return false;
    }

    private void deleteSaveDialog(List<GameSave> saves) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String[] options = new String[saves.size()];
        for (int i = 0; i < saves.size(); i++) {
            GameSave save = saves.get(i);
            options[i] = String.format("存档%d（%s）", i + 1, sdf.format(save.getSaveTime()));
        }

        int choice = JOptionPane.showOptionDialog(
                this, "选择要删除的存档", "删除存档",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]
        );

        if (choice >= 0 && choice < saves.size()) {
            int result = JOptionPane.showConfirmDialog(
                    this, "确定删除？无法恢复！", "确认",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                userManager.getCurrentUser().deleteSave(choice);
                userManager.saveCurrentUser();
                JOptionPane.showMessageDialog(this, "存档已删除", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawValidMoves(g);
        drawPieces(g);
        drawSelectedPiece(g);
    }

    private void drawBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));

        // 横线
        for (int i = 0; i <= 9; i++) {
            int y = MARGIN + i * CELL_SIZE;
            g2d.drawLine(MARGIN, y, MARGIN + 8 * CELL_SIZE, y);
        }

        // 竖线
        for (int i = 0; i <= 8; i++) {
            int x = MARGIN + i * CELL_SIZE;
            if (i == 0 || i == 8) {
                g2d.drawLine(x, MARGIN, x, MARGIN + 9 * CELL_SIZE);
            } else {
                g2d.drawLine(x, MARGIN, x, MARGIN + 4 * CELL_SIZE);
                g2d.drawLine(x, MARGIN + 5 * CELL_SIZE, x, MARGIN + 9 * CELL_SIZE);
            }
        }

        // 九宫格斜线
        g2d.drawLine(MARGIN + 3 * CELL_SIZE, MARGIN, MARGIN + 5 * CELL_SIZE, MARGIN + 2 * CELL_SIZE);
        g2d.drawLine(MARGIN + 5 * CELL_SIZE, MARGIN, MARGIN + 3 * CELL_SIZE, MARGIN + 2 * CELL_SIZE);
        g2d.drawLine(MARGIN + 3 * CELL_SIZE, MARGIN + 7 * CELL_SIZE, MARGIN + 5 * CELL_SIZE, MARGIN + 9 * CELL_SIZE);
        g2d.drawLine(MARGIN + 5 * CELL_SIZE, MARGIN + 7 * CELL_SIZE, MARGIN + 3 * CELL_SIZE, MARGIN + 9 * CELL_SIZE);

        // 楚河汉界
        g2d.setFont(new Font("宋体", Font.BOLD, 24));
        g2d.drawString("楚 河        汉 界", MARGIN + CELL_SIZE, MARGIN + 4 * CELL_SIZE + CELL_SIZE / 2);
    }

    private void drawValidMoves(Graphics g) {
        if (selectedPiece == null || validMoves.isEmpty() || model.isReplaying()) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2d.setColor(new Color(0, 200, 0));
        int radius = 15;

        for (Point2D p : validMoves) {
            int row = (int) p.getX();
            int col = (int) p.getY();
            int centerX = MARGIN + col * CELL_SIZE;
            int centerY = MARGIN + row * CELL_SIZE;
            g2d.fillOval(centerX - radius / 2, centerY - radius / 2, radius, radius);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    private void drawPieces(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (AbstractPiece piece : model.getPieces()) {
            int centerX = MARGIN + piece.getCol() * CELL_SIZE;
            int centerY = MARGIN + piece.getRow() * CELL_SIZE;
            int radius = CELL_SIZE / 2 - 5;

            g2d.setColor(piece.isRed() ? new Color(200, 0, 0) : Color.BLACK);
            g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            g2d.setFont(new Font("宋体", Font.BOLD, 20));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(piece.getName());
            int textHeight = fm.getAscent();
            g2d.drawString(piece.getName(),
                    centerX - textWidth / 2,
                    centerY + textHeight / 2 - 5);
        }
    }

    private void drawSelectedPiece(Graphics g) {
        if (selectedPiece == null || model.isReplaying()) return;
        Graphics2D g2d = (Graphics2D) g;
        int centerX = MARGIN + selectedPiece.getCol() * CELL_SIZE;
        int centerY = MARGIN + selectedPiece.getRow() * CELL_SIZE;
        int radius = CELL_SIZE / 2;
        g2d.setColor(Color.YELLOW);
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    public void startTimerRefresh() {
        if (model.getGameMode() == GameMode.TIME_LIMITED) {
            new Timer(1000, e -> repaint()).start();
        }
    }

    // 显示回放对话框
    public void showReplayDialog() {
        if (model.getTotalReplaySteps() == 0) {
            JOptionPane.showMessageDialog(this, "无步数记录，无法回放！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ReplayDialog replayDialog = new ReplayDialog((Frame) SwingUtilities.getWindowAncestor(this), model, this);
        replayDialog.setVisible(true);
    }
}