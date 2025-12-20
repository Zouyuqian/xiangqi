package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.model.GameMode;
import javax.swing.*;
import java.awt.*;

public class ModeSelectDialog extends JDialog {
    private GameMode selectedMode;

    public ModeSelectDialog(Frame parent) {
        super(parent, "选择游戏模式", true);
        initializeUI();
    }

    private void initializeUI() {
        setSize(300, 150);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("请选择游戏模式");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        ButtonGroup group = new ButtonGroup();
        JRadioButton normalBtn = new JRadioButton(GameMode.NORMAL.toString());
        JRadioButton timeLimitedBtn = new JRadioButton(GameMode.TIME_LIMITED.toString());
        normalBtn.setSelected(true);
        group.add(normalBtn);
        group.add(timeLimitedBtn);

        JButton confirmBtn = new JButton("确认");
        confirmBtn.addActionListener(e -> {
            selectedMode = normalBtn.isSelected() ? GameMode.NORMAL : GameMode.TIME_LIMITED;
            dispose();
        });

        panel.add(titleLabel);
        panel.add(normalBtn);
        panel.add(timeLimitedBtn);
        add(panel, BorderLayout.CENTER);
        add(confirmBtn, BorderLayout.SOUTH);
    }

    public GameMode getSelectedMode() {
        return selectedMode;
    }
}