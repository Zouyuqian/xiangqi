package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.model.GameMode;
import edu.sustech.xiangqi.model.UserManager;
import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    private final ChessBoardModel model;
    private final UserManager userManager;
    private JLabel modeLabel;
    private JLabel redTimeLabel;
    private JLabel blackTimeLabel;
    private JLabel statusLabel;
    private JLabel turnLabel;
    private JLabel lastMoveLabel;
    private JLabel replayStatusLabel;
    private JLabel userLabel;

    public StatusPanel(ChessBoardModel model, UserManager userManager) {
        this.model = model;
        this.userManager = userManager;
        setPreferredSize(new Dimension(200, 0));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        initializeUI();
        startRefreshTimer();
    }

    private void initializeUI() {
        setLayout(new GridLayout(9, 1, 0, 15));

        modeLabel = new JLabel("模式: " + model.getGameMode().toString());
        modeLabel.setFont(new Font("宋体", Font.PLAIN, 14));

        redTimeLabel = new JLabel();
        redTimeLabel.setFont(new Font("宋体", Font.PLAIN, 14));

        blackTimeLabel = new JLabel();
        blackTimeLabel.setFont(new Font("宋体", Font.PLAIN, 14));

        statusLabel = new JLabel("状态: " + model.getGameStatus());
        statusLabel.setFont(new Font("宋体", Font.BOLD, 14));

        turnLabel = new JLabel("当前回合: " + (model.isRedTurn() ? "红方" : "黑方"));
        turnLabel.setFont(new Font("宋体", Font.PLAIN, 14));

        lastMoveLabel = new JLabel("上一步: " + model.getLastMoveRecord());
        lastMoveLabel.setFont(new Font("宋体", Font.PLAIN, 14));

        replayStatusLabel = new JLabel("");
        replayStatusLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        replayStatusLabel.setForeground(Color.RED);

        String userInfo = "用户: " + userManager.getCurrentUser().getUsername() +
                (userManager.getCurrentUser().isGuest() ? " (游客)" : " (注册用户)");
        userLabel = new JLabel(userInfo);
        userLabel.setFont(new Font("宋体", Font.PLAIN, 14));

        add(modeLabel);
        if (model.getGameMode() == GameMode.TIME_LIMITED) {
            add(redTimeLabel);
            add(blackTimeLabel);
        }
        add(statusLabel);
        add(turnLabel);
        add(lastMoveLabel);
        add(replayStatusLabel);
        add(userLabel);
    }

    private void updateTimeLabels() {
        if (model.getGameMode() == GameMode.TIME_LIMITED) {
            Color redColor = model.getRedRemainingTime() <= 30 ? Color.RED : Color.BLACK;
            redTimeLabel.setForeground(redColor);
            redTimeLabel.setText("红方时间: " + model.getRedTimeStr());

            Color blackColor = model.getBlackRemainingTime() <= 30 ? Color.RED : Color.BLACK;
            blackTimeLabel.setForeground(blackColor);
            blackTimeLabel.setText("黑方时间: " + model.getBlackTimeStr());
        }
    }

    public void refreshStatus() {
        statusLabel.setText("状态: " + model.getGameStatus());
        turnLabel.setText("当前回合: " + (model.isRedTurn() ? "红方" : "黑方"));
        lastMoveLabel.setText("上一步: " + model.getLastMoveRecord());
        updateTimeLabels();

        if (model.isReplaying()) {
            replayStatusLabel.setText("当前状态：棋局回放");
        } else {
            replayStatusLabel.setText("");
        }
        repaint();
    }

    private void startRefreshTimer() {
        int delay = (model.getGameMode() == GameMode.TIME_LIMITED) ? 1000 : 500;
        new Timer(delay, e -> refreshStatus()).start();
    }
}