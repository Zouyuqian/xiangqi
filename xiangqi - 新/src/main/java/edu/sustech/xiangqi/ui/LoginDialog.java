package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.model.UserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private final UserManager userManager;
    private boolean loginSuccess = false;

    public LoginDialog(Frame parent, UserManager userManager) {
        super(parent, "用户登录", true);
        this.userManager = userManager;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(350, 250);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // 标题
        JLabel titleLabel = new JLabel("中国象棋登录系统", JLabel.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // 主面板
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("密码:");
        passLabel.setFont(new Font("宋体", Font.PLAIN, 14));
        JPasswordField passField = new JPasswordField();

        mainPanel.add(userLabel);
        mainPanel.add(userField);
        mainPanel.add(passLabel);
        mainPanel.add(passField);

        // 占位符
        mainPanel.add(new JLabel());
        mainPanel.add(new JLabel());

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton loginButton = new JButton("登录");
        JButton registerButton = new JButton("注册");
        JButton guestButton = new JButton("游客登录");

        // 设置按钮样式
        loginButton.setPreferredSize(new Dimension(80, 30));
        registerButton.setPreferredSize(new Dimension(80, 30));
        guestButton.setPreferredSize(new Dimension(100, 30));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(guestButton);

        add(titleLabel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 事件处理
        loginButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入用户名！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userManager.loginUser(username, password)) {
                loginSuccess = true;
                JOptionPane.showMessageDialog(this, "登录成功！欢迎 " + username, "成功", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "用户名和密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (username.equals("guest")) {
                JOptionPane.showMessageDialog(this, "不能注册为guest用户！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userManager.registerUser(username, password)) {
                JOptionPane.showMessageDialog(this, "注册成功！请登录", "成功", JOptionPane.INFORMATION_MESSAGE);
                userField.setText("");
                passField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "用户名已存在！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        guestButton.addActionListener(e -> {
            userManager.loginAsGuest();
            loginSuccess = true;
            JOptionPane.showMessageDialog(this, "以游客身份登录！\n注意：游客无法保存游戏进度", "提示", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        // 回车键登录
        getRootPane().setDefaultButton(loginButton);
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }
}