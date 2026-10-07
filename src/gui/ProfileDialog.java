package gui;

import dao.UserDAO;
import model.User;
import util.Session;

import javax.swing.*;
import java.awt.*;

public class ProfileDialog extends JDialog {
    private final User user;
    private final UserDAO dao = new UserDAO();
    private final JTextField name = new JTextField();
    private final JTextField phone = new JTextField();
    private final JTextField email = new JTextField();
    private final JPasswordField currentPassword = new JPasswordField();
    private final JPasswordField newPassword = new JPasswordField();
    private final JPasswordField confirmPassword = new JPasswordField();

    public ProfileDialog(JFrame parent, User user) {
        super(parent, "My Profile", true); this.user = user; setSize(480, 430); setLocationRelativeTo(parent);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8)); form.setBorder(BorderFactory.createEmptyBorder(18, 20, 12, 20));
        name.setText(user.getFullName()); phone.setText(user.getPhone()); email.setText(user.getEmail());
        form.add(new JLabel("Full name:")); form.add(name); form.add(new JLabel("Phone (10 digits):")); form.add(phone); form.add(new JLabel("Email:")); form.add(email);
        form.add(new JLabel("Current password:")); form.add(currentPassword); form.add(new JLabel("New password:")); form.add(newPassword); form.add(new JLabel("Confirm new password:")); form.add(confirmPassword);
        add(form, BorderLayout.CENTER);
        JButton save = UITheme.button("Save profile", UITheme.TEAL); save.addActionListener(e -> save());
        JButton close = UITheme.button("Close", UITheme.NAVY_LIGHT); close.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(); buttons.add(save); buttons.add(close); add(buttons, BorderLayout.SOUTH);
    }

    private void save() {
        if (!name.getText().trim().matches("[\\p{L}\\p{M} .'&]+") || !phone.getText().trim().matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Enter a valid name and 10-digit phone number."); return;
        }
        String newPass = new String(newPassword.getPassword());
        if (!newPass.isEmpty()) {
            if (!newPass.matches("(?=.*[A-Za-z])(?=.*\\d).{6,}") || !newPass.equals(new String(confirmPassword.getPassword())) ||
                !dao.changePassword(user.getId(), new String(currentPassword.getPassword()), newPass)) {
                JOptionPane.showMessageDialog(this, "Password change failed. Check the current password and new password rules."); return;
            }
        }
        if (!dao.updateProfile(user.getId(), name.getText().trim(), phone.getText().trim(), email.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Profile could not be saved."); return;
        }
        Session.login(new User(user.getId(), user.getUsername(), name.getText().trim(), phone.getText().trim(), email.getText().trim(), user.getRole()));
        JOptionPane.showMessageDialog(this, "Profile updated successfully."); dispose();
    }
}
