package gui;

import javax.swing.*;
import java.awt.*;

public final class Toast {
    private Toast() { }

    public static void show(Window owner, String message) {
        JWindow window = new JWindow(owner);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 35, 45));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        JLabel label = new JLabel(message); label.setForeground(Color.WHITE); panel.add(label);
        window.add(panel); window.pack();
        Point location = owner.getLocationOnScreen();
        window.setLocation(location.x + owner.getWidth() - window.getWidth() - 22, location.y + owner.getHeight() - window.getHeight() - 45);
        window.setAlwaysOnTop(true); window.setVisible(true);
        new Timer(3000, e -> window.dispose()).start();
    }
}
