package edu.sustech.xiangqi;

import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.model.GameMode;
import edu.sustech.xiangqi.model.UserManager;
import edu.sustech.xiangqi.ui.ChessBoardPanel;
import edu.sustech.xiangqi.ui.LoginDialog;
import edu.sustech.xiangqi.ui.ModeSelectDialog;
import edu.sustech.xiangqi.ui.StatusPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class XiangqiApplication {
    private static UserManager userManager;
    private static Timer gameOverCheck; // 改为全局变量，方便重启

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            userManager = new UserManager();
            JFrame loginFrame = new JFrame();
            loginFrame.setAlwaysOnTop(true);
            LoginDialog loginDialog = new LoginDialog(loginFrame, userManager);
            loginDialog.setVisible(true);
            if (!loginDialog.isLoginSuccess()) {
                System.exit(0);
                return;
            }
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        ModeSelectDialog modeDialog = new ModeSelectDialog(null);
        modeDialog.setVisible(true);
        GameMode gameMode = modeDialog.getSelectedMode();

        JFrame frame = new JFrame("中国象棋 - 当前用户: " + userManager.getCurrentUser().getUsername());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChessBoardModel model = new ChessBoardModel(gameMode);
        ChessBoardPanel boardPanel = new ChessBoardPanel(model, userManager);
        StatusPanel statusPanel = new StatusPanel(model, userManager);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.EAST);

        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JButton restartButton = new JButton("重新开始");
        JButton saveButton = new JButton("保存游戏");
        JButton loadButton = new JButton("加载游戏");
        JButton exitButton = new JButton("退出游戏");
        JButton surrenderButton = new JButton("认输");

        Dimension btnSize = new Dimension(100, 30);
        restartButton.setPreferredSize(btnSize);
        saveButton.setPreferredSize(btnSize);
        loadButton.setPreferredSize(btnSize);
        exitButton.setPreferredSize(btnSize);
        surrenderButton.setPreferredSize(btnSize);

        boolean isGuest = userManager.getCurrentUser().isGuest();
        saveButton.setEnabled(!isGuest);
        loadButton.setEnabled(!isGuest);
        if (isGuest) {
            saveButton.setToolTipText("游客无法保存");
            loadButton.setToolTipText("游客无法加载");
        }

        // 重新开始按钮：修复弹窗逻辑，重启定时器
        restartButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(frame, "确定重新开始？", "确认", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                model.restartGame();
                boardPanel.repaint();
                JOptionPane.showMessageDialog(frame, "游戏已重启！", "提示", JOptionPane.INFORMATION_MESSAGE);

                // 修复核心：停止旧定时器，创建新定时器（确保游戏结束后弹窗生效）
                if (gameOverCheck != null && gameOverCheck.isRunning()) {
                    gameOverCheck.stop();
                }
                initGameOverCheck(frame, model, boardPanel); // 重启弹窗检测
            }
        });

        saveButton.addActionListener(e -> {
            if (boardPanel.saveGame()) {
                JOptionPane.showMessageDialog(frame, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        loadButton.addActionListener(e -> {
            if (boardPanel.loadGame()) {
                JOptionPane.showMessageDialog(frame, "加载成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        exitButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(frame, "确定退出？", "确认", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                model.stopTimer();
                if (gameOverCheck != null) gameOverCheck.stop();
                System.exit(0);
            }
        });

        // 投降按钮：统一弹窗顺序
        surrenderButton.addActionListener(e -> {
            if (model.isGameOver()) {
                JOptionPane.showMessageDialog(frame, "游戏已结束！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int result = JOptionPane.showConfirmDialog(frame,
                    model.getCurrentPlayerColor() + "确定认输？",
                    "认输",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                String winner = model.isRedTurn() ? "黑方" : "红方";
                model.setGameStatus(winner + "胜利（对方认输）！");

                // 1. 先弹出胜利提示
                JOptionPane.showMessageDialog(frame, model.getGameStatus(), "游戏结束", JOptionPane.INFORMATION_MESSAGE);

                // 2. 再弹出回放选项
                int replayChoice = JOptionPane.showConfirmDialog(
                        frame,
                        "是否查看棋局回放？",
                        "回放选项",
                        JOptionPane.YES_NO_OPTION
                );
                if (replayChoice == JOptionPane.YES_OPTION) {
                    boardPanel.showReplayDialog();
                }
                boardPanel.repaint();
            }
        });

        controlPanel.add(restartButton);
        controlPanel.add(saveButton);
        controlPanel.add(loadButton);
        controlPanel.add(surrenderButton);
        controlPanel.add(exitButton);

        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                model.stopTimer();
                if (gameOverCheck != null) gameOverCheck.stop();
                System.exit(0);
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        boardPanel.startTimerRefresh();

        // 初始化游戏结束弹窗检测
        initGameOverCheck(frame, model, boardPanel);
    }

    // 抽取为独立方法，方便重新开始时调用
    private static void initGameOverCheck(JFrame frame, ChessBoardModel model, ChessBoardPanel boardPanel) {
        gameOverCheck = new Timer(500, e -> {
            if (model.isGameOver() && !model.isReplaying()) {
                ((Timer) e.getSource()).stop(); // 仅触发一次

                // 1. 弹出胜利提示
                JOptionPane.showMessageDialog(frame, model.getGameStatus(), "游戏结束", JOptionPane.INFORMATION_MESSAGE);

                // 2. 弹出回放选项
                int replayChoice = JOptionPane.showConfirmDialog(
                        frame,
                        "是否查看棋局回放？",
                        "回放选项",
                        JOptionPane.YES_NO_OPTION
                );
                if (replayChoice == JOptionPane.YES_OPTION) {
                    boardPanel.showReplayDialog();
                }
            }
        });
        gameOverCheck.start();
    }
}