package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.ChessBoardModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessBoardPanel extends JPanel {
    private final ChessBoardModel model;
    private AbstractPiece selectedPiece = null;
    private static final int CELL_SIZE = 60;
    private static final int MARGIN = 40;
    private static final int BOARD_WIDTH = 8 * CELL_SIZE + 2 * MARGIN;
    private static final int BOARD_HEIGHT = 9 * CELL_SIZE + 2 * MARGIN;

    public ChessBoardPanel(ChessBoardModel model) {
        this.model = model;
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
        int x = e.getX() - MARGIN;
        int y = e.getY() - MARGIN;

        if (x < 0 || y < 0 || x >= 8 * CELL_SIZE || y >= 9 * CELL_SIZE) {
            selectedPiece = null;
            repaint();
            return;
        }

        int col = Math.round((float)x / CELL_SIZE);
        int row = Math.round((float)y / CELL_SIZE);

        if (!model.isValidPosition(row, col)) {
            selectedPiece = null;
            repaint();
            return;
        }

        AbstractPiece clickedPiece = model.getPieceAt(row, col);

        if (selectedPiece == null) {
            if (clickedPiece != null && clickedPiece.isRed() == model.isRedTurn()) {
                selectedPiece = clickedPiece;
                repaint();
            }
        } else {
            if (model.movePiece(selectedPiece, row, col)) {
                selectedPiece = null;
                repaint();

                if (model.getGameStatus().contains("胜利")) {
                    JOptionPane.showMessageDialog(this, model.getGameStatus(), "游戏结束", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (clickedPiece != null && clickedPiece.isRed() == model.isRedTurn()) {
                    selectedPiece = clickedPiece;
                } else {
                    selectedPiece = null;
                }
                repaint();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
        drawSelectedPiece(g);
        drawGameStatus(g);
    }

    private void drawBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));

        // 绘制横线
        for (int i = 0; i <= 9; i++) {
            int y = MARGIN + i * CELL_SIZE;
            g2d.drawLine(MARGIN, y, MARGIN + 8 * CELL_SIZE, y);
        }

        // 绘制竖线
        for (int i = 0; i <= 8; i++) {
            int x = MARGIN + i * CELL_SIZE;
            if (i == 0 || i == 8) {
                g2d.drawLine(x, MARGIN, x, MARGIN + 9 * CELL_SIZE);
            } else {
                g2d.drawLine(x, MARGIN, x, MARGIN + 4 * CELL_SIZE);
                g2d.drawLine(x, MARGIN + 5 * CELL_SIZE, x, MARGIN + 9 * CELL_SIZE);
            }
        }

        // 绘制九宫格斜线
        g2d.drawLine(MARGIN + 3 * CELL_SIZE, MARGIN, MARGIN + 5 * CELL_SIZE, MARGIN + 2 * CELL_SIZE);
        g2d.drawLine(MARGIN + 5 * CELL_SIZE, MARGIN, MARGIN + 3 * CELL_SIZE, MARGIN + 2 * CELL_SIZE);
        g2d.drawLine(MARGIN + 3 * CELL_SIZE, MARGIN + 7 * CELL_SIZE, MARGIN + 5 * CELL_SIZE, MARGIN + 9 * CELL_SIZE);
        g2d.drawLine(MARGIN + 5 * CELL_SIZE, MARGIN + 7 * CELL_SIZE, MARGIN + 3 * CELL_SIZE, MARGIN + 9 * CELL_SIZE);

        // 绘制楚河汉界
        g2d.setFont(new Font("宋体", Font.BOLD, 24));
        g2d.drawString("楚 河        汉 界", MARGIN + CELL_SIZE, MARGIN + 4 * CELL_SIZE + CELL_SIZE / 2);
    }

    private void drawPieces(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (AbstractPiece piece : model.getPieces()) {
            int centerX = MARGIN + piece.getCol() * CELL_SIZE;
            int centerY = MARGIN + piece.getRow() * CELL_SIZE;
            int radius = CELL_SIZE / 2 - 5;

            // 绘制棋子背景
            if (piece.isRed()) {
                g2d.setColor(new Color(200, 0, 0));
            } else {
                g2d.setColor(Color.BLACK);
            }
            g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            // 绘制棋子边框
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            // 绘制棋子文字
            g2d.setColor(Color.WHITE);
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
        if (selectedPiece != null) {
            Graphics2D g2d = (Graphics2D) g;
            int centerX = MARGIN + selectedPiece.getCol() * CELL_SIZE;
            int centerY = MARGIN + selectedPiece.getRow() * CELL_SIZE;
            int radius = CELL_SIZE / 2;

            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }
    }

    private void drawGameStatus(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("宋体", Font.BOLD, 16));
        g.drawString("状态: " + model.getGameStatus(), MARGIN, 20);
        g.drawString("当前回合: " + (model.isRedTurn() ? "红方" : "黑方"), MARGIN + 200, 20);
    }
}