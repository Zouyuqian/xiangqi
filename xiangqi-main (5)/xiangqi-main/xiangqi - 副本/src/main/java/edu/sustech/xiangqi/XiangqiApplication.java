package edu.sustech.xiangqi;

import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.model.UserManager;
import edu.sustech.xiangqi.ui.ChessBoardPanel;
import edu.sustech.xiangqi.ui.LoginDialog;

import javax.swing.*;
import java.awt.*;

public class XiangqiApplication {
    private static UserManager userManager;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            userManager = new UserManager();

            // 显示登录对话框
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
        JFrame frame = new JFrame("中国象棋 - 当前用户: " + userManager.getCurrentUser().getUsername() +
                (userManager.getCurrentUser().isGuest() ? " (游客)" : ""));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ChessBoardModel model = new ChessBoardModel();
        ChessBoardPanel boardPanel = new ChessBoardPanel(model, userManager);

        // 控制面板
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton restartButton = new JButton("重新开始");
        JButton saveButton = new JButton("保存游戏");
        JButton loadButton = new JButton("加载游戏");
        JButton exitButton = new JButton("退出游戏");

        // 设置按钮样式
        restartButton.setPreferredSize(new Dimension(100, 30));
        saveButton.setPreferredSize(new Dimension(100, 30));
        loadButton.setPreferredSize(new Dimension(100, 30));
        exitButton.setPreferredSize(new Dimension(100, 30));

        // 游客不能保存/加载游戏
        boolean isGuest = userManager.getCurrentUser().isGuest();
        saveButton.setEnabled(!isGuest);
        loadButton.setEnabled(!isGuest);

        if (isGuest) {
            saveButton.setToolTipText("游客无法保存游戏");
            loadButton.setToolTipText("游客无法加载游戏");
        }

        restartButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(frame,
                    "确定要重新开始游戏吗？当前进度将丢失！",
                    "重新开始",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                model.restartGame();
                boardPanel.repaint();
                JOptionPane.showMessageDialog(frame, "游戏已重新开始！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        saveButton.addActionListener(e -> {
            if (boardPanel.saveGame()) {
                JOptionPane.showMessageDialog(frame, "游戏保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        loadButton.addActionListener(e -> {
            if (boardPanel.loadGame()) {
                JOptionPane.showMessageDialog(frame, "游戏加载成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        exitButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(frame,
                    "确定要退出游戏吗？",
                    "退出游戏",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        controlPanel.add(restartButton);
        controlPanel.add(saveButton);
        controlPanel.add(loadButton);
        controlPanel.add(exitButton);

        frame.setLayout(new BorderLayout());
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}