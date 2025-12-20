package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.model.ChessBoardModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReplayDialog extends JDialog {
    private final ChessBoardModel model;
    private final ChessBoardPanel boardPanel;
    private JLabel stepLabel;
    private JButton prevBtn;
    private JButton nextBtn;
    private JButton resetBtn;
    private JButton closeBtn;

    public ReplayDialog(Frame parent, ChessBoardModel model, ChessBoardPanel boardPanel) {
        super(parent, "棋局回放", true);
        this.model = model;
        this.boardPanel = boardPanel;
        initializeUI();
        updateButtonStatus();
    }

    private void initializeUI() {
        setSize(350, 150);
        setLocationRelativeTo(getParent());
        setResizable(false);

        stepLabel = new JLabel(getStepText());
        stepLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        stepLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        prevBtn = new JButton("上一步");
        nextBtn = new JButton("下一步");
        resetBtn = new JButton("重新回放");
        closeBtn = new JButton("关闭");

        Dimension btnSize = new Dimension(90, 30);
        prevBtn.setPreferredSize(btnSize);
        nextBtn.setPreferredSize(btnSize);
        resetBtn.setPreferredSize(btnSize);
        closeBtn.setPreferredSize(btnSize);

        prevBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.prevReplayStep();
                boardPanel.repaint();
                updateStepLabel();
                updateButtonStatus();
            }
        });

        nextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean hasNext = model.nextReplayStep();
                boardPanel.repaint();
                updateStepLabel();
                updateButtonStatus();
                if (!hasNext) {
                    JOptionPane.showMessageDialog(ReplayDialog.this, "已到达最后一步！", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.resetReplay();
                boardPanel.repaint();
                updateStepLabel();
                updateButtonStatus();
            }
        });

        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnPanel.add(prevBtn);
        btnPanel.add(nextBtn);
        btnPanel.add(resetBtn);
        btnPanel.add(closeBtn);

        setLayout(new BorderLayout(10, 10));
        add(stepLabel, BorderLayout.NORTH);
        add(btnPanel, BorderLayout.CENTER);
    }

    private void updateStepLabel() {
        stepLabel.setText(getStepText());
    }

    private String getStepText() {
        int currentStep = model.getReplayIndex() + 1;
        int totalStep = model.getTotalReplaySteps();
        return String.format("当前步数：%d / %d", currentStep, totalStep);
    }

    private void updateButtonStatus() {
        int replayIndex = model.getReplayIndex();
        int totalStep = model.getTotalReplaySteps();

        prevBtn.setEnabled(replayIndex > 0);
        nextBtn.setEnabled(replayIndex < totalStep - 1);
        resetBtn.setEnabled(replayIndex != -1);
    }
}