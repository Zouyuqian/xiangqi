package edu.sustech.xiangqi;

import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.ui.ChessBoardPanel;

import javax.swing.*;
import java.awt.*;

public class XiangqiApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("中国象棋");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ChessBoardModel model = new ChessBoardModel();
            ChessBoardPanel boardPanel = new ChessBoardPanel(model);

            // 添加控制面板
            JPanel controlPanel = new JPanel();
            JButton restartButton = new JButton("重新开始");
            JButton exitButton = new JButton("退出游戏");

            restartButton.addActionListener(e -> {
                model.restartGame();
                boardPanel.repaint();
                JOptionPane.showMessageDialog(frame, "游戏已重新开始！", "提示", JOptionPane.INFORMATION_MESSAGE);
            });

            exitButton.addActionListener(e -> System.exit(0));

            controlPanel.add(restartButton);
            controlPanel.add(exitButton);

            frame.setLayout(new BorderLayout());
            frame.add(boardPanel, BorderLayout.CENTER);
            frame.add(controlPanel, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}
