package gui;

import dao.DatabaseHelper;
import dao.UserDAO;
import model.User;
import util.Session;
import util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();
    private final JTextField usernameField = field("e.g. admin");
    private final JPasswordField passwordField = new JPasswordField();
    private final JLabel loginMessage = messageLabel();
    private final JTextField fullNameField = field("e.g. Asha Kumar");
    private final JTextField newUsernameField = field("e.g. asha_k");
    private final JTextField phoneField = field("10 digit phone number");
    private final JTextField emailField = field("optional@email.com");
    private final JPasswordField newPasswordField = new JPasswordField();
    private final JPasswordField confirmPasswordField = new JPasswordField();
    private final JLabel[] registrationErrors = new JLabel[6];
    private final JLabel registerMessage = messageLabel();
    private JButton registerButton;
    private JButton signInButton;
    private int failedAttempts;
    private Timer lockoutTimer;

    public LoginFrame() {
        UITheme.install();
        userDAO.ensureDefaultAdmin();
        setTitle("StayEase | Sign in");
        setSize(940, 620);
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
        title.setForeground(Color.WHITE); title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        JLabel description = new JLabel("<html>Book rooms, manage reservations and<br>keep every stay organized.</html>");
        description.setForeground(new Color(215, 226, 237)); description.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        panel.add(logo, BorderLayout.NORTH); panel.add(title, BorderLayout.CENTER); panel.add(description, BorderLayout.SOUTH); return panel;
    }

    private JPanel createAccountPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10)); wrapper.setBackground(UITheme.background()); wrapper.setBorder(new EmptyBorder(25, 28, 25, 28));
        if (!DatabaseHelper.canConnect()) {
            JLabel banner = new JLabel("Can't reach the database - check db.properties and that Oracle is running");
            banner.setOpaque(true); banner.setBackground(new Color(255, 242, 190)); banner.setForeground(new Color(100, 75, 10)); banner.setBorder(new EmptyBorder(9, 10, 9, 10)); wrapper.add(banner, BorderLayout.NORTH);
        }
        JTabbedPane tabs = new JTabbedPane(); tabs.addTab("Sign in", createLoginPanel()); tabs.addTab("Create customer account", createRegisterPanel()); wrapper.add(tabs, BorderLayout.CENTER); return wrapper;
    }

    private JPanel createLoginPanel() {
        JPanel panel = formPanel(); GridBagConstraints gbc = baseConstraints(); int row = 0;
        row = addField(panel, gbc, row, "Username", usernameField, null);
        passwordField.putClientProperty("JPasswordField.showRevealButton", true);
        row = addField(panel, gbc, row, "Password", passwordField, null);
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; panel.add(loginMessage, gbc);
        signInButton = UITheme.button("Sign in", UITheme.TEAL); signInButton.addActionListener(e -> signIn());
        gbc.gridy = row++; panel.add(signInButton, gbc); getRootPane().setDefaultButton(signInButton);
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = formPanel(); GridBagConstraints gbc = baseConstraints(); int row = 0;
        JTextField[] fields = {fullNameField, newUsernameField, phoneField, emailField, newPasswordField, confirmPasswordField};
        String[] labels = {"Full name", "Username", "Phone", "Email", "Password", "Confirm password"};
        for (int i = 0; i < fields.length; i++) { registrationErrors[i] = messageLabel(); row = addField(panel, gbc, row, labels[i], fields[i], registrationErrors[i]); }
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; panel.add(registerMessage, gbc);
        registerButton = UITheme.button("Create account", UITheme.TEAL); registerButton.setEnabled(false); registerButton.addActionListener(e -> register());
        gbc.gridy = row++; panel.add(registerButton, gbc);
        DocumentListener listener = new DocumentListener() { public void insertUpdate(DocumentEvent e) { validateRegistration(); } public void removeUpdate(DocumentEvent e) { validateRegistration(); } public void changedUpdate(DocumentEvent e) { validateRegistration(); } };
        for (JTextField field : fields) field.getDocument().addDocumentListener(listener);
        validateRegistration(); return panel;
    }

    private JPanel formPanel() { JPanel panel = new JPanel(new GridBagLayout()); panel.setBackground(UITheme.card()); panel.setBorder(new EmptyBorder(25, 25, 25, 25)); return panel; }
    private GridBagConstraints baseConstraints() { GridBagConstraints gbc = new GridBagConstraints(); gbc.gridx = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1; gbc.insets = new Insets(3, 0, 3, 0); return gbc; }

    private int addField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field, JLabel error) {
        JLabel label = new JLabel(labelText); label.setForeground(UITheme.text()); gbc.gridy = row++; panel.add(label, gbc);
        field.setPreferredSize(new Dimension(300, 38)); field.putClientProperty("JTextField.showClearButton", true); gbc.gridy = row++; panel.add(field, gbc);
        if (error != null) { gbc.gridy = row++; panel.add(error, gbc); } return row;
    }

    private static JTextField field(String placeholder) { JTextField field = new JTextField(); field.putClientProperty("JTextField.placeholderText", placeholder); return field; }
    private static JLabel messageLabel() { JLabel label = new JLabel(" "); label.setForeground(UITheme.ERROR); label.setFont(new Font("Segoe UI", Font.PLAIN, 12)); return label; }

    private void signIn() {
        if (signInButton == null || !signInButton.isEnabled()) return;
        User user = userDAO.login(usernameField.getText().trim(), new String(passwordField.getPassword()));
        if (user == null) { failedAttempts++; passwordField.setText(""); loginMessage.setText("Invalid username or password."); if (failedAttempts >= 5) startLockout(); passwordField.requestFocusInWindow(); return; }
        failedAttempts = 0; Session.login(user); dispose(); if (user.isAdmin()) new MainFrame(user.getFullName()).setVisible(true); else new CustomerDashboard(user).setVisible(true);
    }

    private void startLockout() {
        signInButton.setEnabled(false); final int[] seconds = {30};
        lockoutTimer = new Timer(1000, e -> { seconds[0]--; loginMessage.setText("Too many attempts. Try again in " + seconds[0] + " seconds."); if (seconds[0] <= 0) { lockoutTimer.stop(); failedAttempts = 0; signInButton.setEnabled(true); loginMessage.setText(" "); } });
        loginMessage.setText("Too many attempts. Try again in 30 seconds."); lockoutTimer.start();
    }

    private void validateRegistration() {
        String[] values = {fullNameField.getText().trim(), newUsernameField.getText().trim(), phoneField.getText().trim(), emailField.getText().trim(), new String(newPasswordField.getPassword()), new String(confirmPasswordField.getPassword())};
        String[] errors = {Validator.checkGuestName(values[0]), Validator.checkUsername(values[1]), Validator.checkContact(values[2]), Validator.checkEmail(values[3]), Validator.checkPassword(values[4]), values[4].equals(values[5]) && !values[5].isEmpty() ? null : "Passwords don't match"};
        boolean valid = true;
        JComponent[] fields = {fullNameField, newUsernameField, phoneField, emailField, newPasswordField, confirmPasswordField};
        for (int i = 0; i < errors.length; i++) { registrationErrors[i].setText(errors[i] == null ? " " : errors[i]); fields[i].putClientProperty("JComponent.outline", errors[i] == null ? null : "error"); valid &= errors[i] == null; }
        registerButton.setEnabled(valid);
    }

    private void register() {
        if (!registerButton.isEnabled()) return;
        User user = userDAO.register(fullNameField.getText().trim(), newUsernameField.getText().trim(), phoneField.getText().trim(), emailField.getText().trim(), new String(newPasswordField.getPassword()));
        if (user == null) { registerMessage.setText("That username is already taken."); return; }
        Session.login(user); dispose(); new CustomerDashboard(user).setVisible(true);
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true)); }
}
