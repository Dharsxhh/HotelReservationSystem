package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String SAMPLE_USERNAME = "admin";
    private static final String SAMPLE_PASSWORD = "admin123";

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JLabel messageLabel = new JLabel(" ");

    public LoginFrame() {
        UITheme.install();
        setTitle("StayEase | Staff Login");
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        add(createBrandPanel());
        add(createLoginPanel());
        getRootPane().setDefaultButton(createLoginButton());
    }

    private JPanel createBrandPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.NAVY);
        panel.setBorder(new EmptyBorder(50, 45, 50, 45));

        JLabel logo = new JLabel("STAYEASE");
        logo.setForeground(UITheme.GOLD);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel title = new JLabel("Smarter stays.\nSimpler operations.");
        title.setText("<html>Smarter stays.<br>Simpler operations.</html>");
        title.setForeground(UITheme.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));

        JLabel description = new JLabel("<html>Manage rooms, reservations, guests and<br>front-desk activity from one calm workspace.</html>");
        description.setForeground(new Color(215, 226, 237));
        description.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        copy.add(title);
        copy.add(Box.createVerticalStrut(18));
        copy.add(description);

        JLabel footer = new JLabel("HOTEL OPERATIONS PLATFORM  •  2026");
        footer.setForeground(new Color(166, 190, 210));
        footer.setFont(new Font("Segoe UI", Font.BOLD, 11));

        panel.add(logo, BorderLayout.NORTH);
        panel.add(copy, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BACKGROUND);
        JPanel card = new JPanel();
        card.setBackground(UITheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 231, 238)),
            new EmptyBorder(28, 32, 28, 32)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Welcome back");
        heading.setForeground(UITheme.TEXT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 25));
        JLabel subtitle = new JLabel("Sign in to open the front desk");
        subtitle.setForeground(UITheme.MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));
        card.add(label("Username"));
        card.add(Box.createVerticalStrut(5));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(15));
        card.add(label("Password"));
        card.add(Box.createVerticalStrut(5));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        messageLabel.setForeground(new Color(190, 60, 60));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(messageLabel);
        card.add(Box.createVerticalStrut(12));
        JButton loginButton = createLoginButton();
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(18));
        JLabel hint = new JLabel("Sample login: admin / admin123");
        hint.setForeground(UITheme.MUTED);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(hint);

        panel.add(card);
        return panel;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UITheme.TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton createLoginButton() {
        JButton loginButton = UITheme.button("Sign in", UITheme.TEAL);
        loginButton.addActionListener(e -> login());
        return loginButton;
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (SAMPLE_USERNAME.equals(username) && SAMPLE_PASSWORD.equals(password)) {
            dispose();
            new MainFrame(username).setVisible(true);
        } else {
            messageLabel.setText("Invalid login. Try admin / admin123");
            passwordField.selectAll();
            passwordField.requestFocusInWindow();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
