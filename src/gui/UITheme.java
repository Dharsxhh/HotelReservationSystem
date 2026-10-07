package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.prefs.Preferences;

public final class UITheme {
    public static final Color NAVY = new Color(15, 42, 68);
    public static final Color NAVY_LIGHT = new Color(32, 67, 98);
    public static final Color TEAL = new Color(14, 118, 110);
    public static final Color GOLD = new Color(212, 167, 44);
    public static final Color BACKGROUND = new Color(244, 247, 251);
    public static final Color TEXT = new Color(35, 49, 63);
    public static final Color MUTED = new Color(103, 116, 130);
    public static final Color ERROR = new Color(190, 60, 60);
    public static final Color WHITE = Color.WHITE;
    private static boolean installed;
    private static boolean darkMode;

    private UITheme() { }

    public static void install() {
        if (!installed) {
            darkMode = Preferences.userNodeForPackage(UITheme.class).getBoolean("darkMode", false);
            if (darkMode) FlatDarkLaf.setup(); else FlatLightLaf.setup();
            installed = true;
        }
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", WHITE);
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("List.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Spinner.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("Table.showHorizontalLines", true);
    }

    public static boolean isDarkMode() { return darkMode; }

    public static void toggleDarkMode(Window window) {
        darkMode = !darkMode;
        Preferences.userNodeForPackage(UITheme.class).putBoolean("darkMode", darkMode);
        if (darkMode) FlatDarkLaf.setup(); else FlatLightLaf.setup();
        SwingUtilities.updateComponentTreeUI(window);
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

    // Red text shown under a form instead of a pop-up when something is wrong.
    public static JLabel errorLabel() {
        JLabel label = new JLabel(" ");
        label.setForeground(ERROR);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        return label;
    }

    // ----- Date picker: a spinner showing yyyy-MM-dd; arrows move one day at a time -----

    public static JSpinner dateSpinner(LocalDate date) {
        Date value = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        JSpinner spinner = new JSpinner(new SpinnerDateModel(value, null, null, Calendar.DAY_OF_MONTH));
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }

    public static LocalDate getDate(JSpinner spinner) {
        return ((Date) spinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
