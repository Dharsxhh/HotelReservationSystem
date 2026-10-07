package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class UITheme {
    public static final Color NAVY = new Color(15, 42, 68);
    public static final Color NAVY_LIGHT = new Color(32, 67, 98);
    public static final Color TEAL = new Color(14, 118, 110);
    public static final Color GOLD = new Color(212, 167, 44);
    public static final Color BACKGROUND = new Color(244, 247, 251);
    public static final Color TEXT = new Color(35, 49, 63);
    public static final Color MUTED = new Color(103, 116, 130);
    public static final Color WHITE = Color.WHITE;

    private UITheme() { }

    public static void install() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", WHITE);
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("List.font", new Font("Segoe UI", Font.PLAIN, 14));
    }

    public static JButton button(String text, Color background) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(WHITE);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 14, 10, 14));
        button.setOpaque(true);
        return button;
    }
}
