package gui;

import dao.UserDAO;
import model.User;
import util.Session;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JLabel loginMessage = new JLabel(" ");
    private final JTextField fullNameField = new JTextField();
    private final JTextField newUsernameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField newPasswordField = new JPasswordField();
    private final JPasswordField confirmPasswordField = new JPasswordField();
    private final JLabel registerMessage = new JLabel(" ");
    private JButton registerButton;

    public LoginFrame() {
        UITheme.install();
        userDAO.ensureDefaultAdmin();
        setTitle("StayEase | Sign in");
        setSize(900, 570);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));
        add(createBrandPanel());
        add(createAccountPanel());
    }

    private JPanel createBrandPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.NAVY);
        panel.setBorder(new EmptyBorder(50, 45, 50, 45));
        JLabel logo = new JLabel("STAYEASE"); logo.setForeground(UITheme.GOLD); logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JLabel title = new JLabel("<html>Smarter stays.<br>Simpler operations.</html>");
        title.setForeground(UITheme.WHITE); title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        JLabel description = new JLabel("<html>Book rooms, manage reservations and<br>keep every stay organized.</html>");
        description.setForeground(new Color(215, 226, 237)); description.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        panel.add(logo, BorderLayout.NORTH); panel.add(title, BorderLayout.CENTER); panel.add(description, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAccountPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BACKGROUND);
        wrapper.setBorder(new EmptyBorder(35, 35, 35, 35));
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Sign in", createLoginPanel());
        tabs.addTab("Create customer account", createRegisterPanel());
        wrapper.add(tabs, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createLoginPanel() {
        JPanel panel = formPanel();
        panel.add(heading("Welcome back", "Sign in to open your workspace"));
        panel.add(label("Username")); panel.add(usernameField);
        panel.add(label("Password")); panel.add(passwordField);
        JCheckBox showPassword = new JCheckBox("Show password"); showPassword.setOpaque(false);
        showPassword.addActionListener(e -> passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '•'));
        panel.add(showPassword);
        loginMessage.setForeground(UITheme.ERROR); panel.add(loginMessage);
        JButton signIn = UITheme.button("Sign in", UITheme.TEAL);
        signIn.addActionListener(e -> signIn());
        panel.add(signIn); getRootPane().setDefaultButton(signIn);
        usernameField.addActionListener(e -> signIn()); passwordField.addActionListener(e -> signIn());
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = formPanel();
        panel.add(heading("Create your account", "Customer accounts can manage their own stays"));
        panel.add(label("Full name")); panel.add(fullNameField);
        panel.add(label("Username (4-20 letters, digits or _ )")); panel.add(newUsernameField);
        panel.add(label("Phone (10 digits)")); panel.add(phoneField);
        panel.add(label("Email (optional)")); panel.add(emailField);
        panel.add(label("Password (6+ characters, letter and digit)")); panel.add(newPasswordField);
        panel.add(label("Confirm password")); panel.add(confirmPasswordField);
        registerMessage.setForeground(UITheme.ERROR); panel.add(registerMessage);
        registerButton = UITheme.button("Create account", UITheme.TEAL); registerButton.setEnabled(false);
        registerButton.addActionListener(e -> register()); panel.add(registerButton);
        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateRegisterState(); }
            public void removeUpdate(DocumentEvent e) { updateRegisterState(); }
            public void changedUpdate(DocumentEvent e) { updateRegisterState(); }
        };
        for (JTextField field : new JTextField[]{fullNameField, newUsernameField, phoneField, emailField, newPasswordField, confirmPasswordField}) {
            field.getDocument().addDocumentListener(listener);
        }
        return panel;
    }

    private JPanel formPanel() {
        JPanel panel = new JPanel(); panel.setBackground(UITheme.WHITE); panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); return panel;
    }

    private JLabel heading(String title, String subtitle) {
        JLabel label = new JLabel("<html><b>" + title + "</b><br><font size='3'>" + subtitle + "</font></html>");
        label.setForeground(UITheme.TEXT); label.setFont(new Font("Segoe UI", Font.PLAIN, 23));
        label.setBorder(new EmptyBorder(0, 0, 20, 0)); return label;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text); label.setForeground(UITheme.TEXT); label.setBorder(new EmptyBorder(7, 0, 3, 0)); return label;
    }

    private void signIn() {
        User user = userDAO.login(usernameField.getText().trim(), new String(passwordField.getPassword()));
        if (user == null) {
            loginMessage.setText("Invalid username or password."); passwordField.setText(""); passwordField.requestFocusInWindow(); return;
        }
        Session.login(user); dispose();
        if (user.isAdmin()) new MainFrame(user.getFullName()).setVisible(true);
        else new CustomerDashboard(user).setVisible(true);
    }

    private boolean validRegistration() {
        String username = newUsernameField.getText().trim(); String password = new String(newPasswordField.getPassword());
        String email = emailField.getText().trim();
        return !fullNameField.getText().trim().isEmpty() && username.matches("[A-Za-z0-9_]{4,20}") &&
            phoneField.getText().trim().matches("\\d{10}") && password.matches("(?=.*[A-Za-z])(?=.*\\d).{6,}") &&
            password.equals(new String(confirmPasswordField.getPassword())) &&
            (email.isEmpty() || email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"));
    }

    private void updateRegisterState() { if (registerButton != null) registerButton.setEnabled(validRegistration()); }

    private void register() {
        if (!validRegistration()) { registerMessage.setText("Check the highlighted account details."); return; }
        User user = userDAO.register(fullNameField.getText().trim(), newUsernameField.getText().trim(), phoneField.getText().trim(),
            emailField.getText().trim(), new String(newPasswordField.getPassword()));
        if (user == null) { registerMessage.setText("That username is already taken."); return; }
        Session.login(user); dispose(); new CustomerDashboard(user).setVisible(true);
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true)); }
}
