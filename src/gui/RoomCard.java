package gui;

import model.Room;
import util.Billing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class RoomCard extends JPanel {
    private final Room room;

    public RoomCard(Room room, LocalDate checkIn, LocalDate checkOut, Runnable bookAction) {
        this.room = room;
        setLayout(new BorderLayout(8, 8)); setBackground(UITheme.card()); setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UITheme.border()), new EmptyBorder(14, 14, 14, 14)));
        JPanel band = new JPanel(); band.setPreferredSize(new Dimension(0, 7)); band.setBackground("Suite".equals(room.getRoomType()) ? UITheme.GOLD : "Deluxe".equals(room.getRoomType()) ? UITheme.TEAL : UITheme.NAVY_LIGHT); add(band, BorderLayout.NORTH);
        JPanel details = new JPanel(); details.setOpaque(false); details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Room " + room.getRoomNumber() + " · " + room.getRoomType()); title.setForeground(UITheme.text()); title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel sharing = new JLabel(room.getSharingLabel()); sharing.setForeground(UITheme.muted());
        JLabel price = new JLabel(Billing.money(room.getPrice()) + " / night"); price.setForeground(UITheme.TEAL); price.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel total = new JLabel("Stay total: " + Billing.money(Billing.total(room.getPrice(), Billing.nights(checkIn, checkOut)))); total.setForeground(UITheme.text());
        details.add(title); details.add(Box.createVerticalStrut(5)); details.add(sharing); details.add(Box.createVerticalStrut(7)); details.add(price); details.add(total); add(details, BorderLayout.CENTER);
        JButton book = UITheme.button("Book this room", UITheme.TEAL); book.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); book.addActionListener(e -> bookAction.run()); add(book, BorderLayout.SOUTH);
    }
}
