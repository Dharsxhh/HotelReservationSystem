package gui;

import dao.ReservationDAO;
import model.BookedRoom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Every reservation ever made: booked, checked out and cancelled.
public class HistoryDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final ReservationTable table = new ReservationTable();

    public HistoryDialog(JFrame parent) {
        super(parent, "Booking History", true);
        setSize(1000, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(12, 12, 12, 12));
        add(scroll, BorderLayout.CENTER);

        JButton receiptButton = UITheme.button("View Receipt", UITheme.TEAL);
        receiptButton.addActionListener(e -> {
            BookedRoom selected = table.getSelectedReservation();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select a reservation first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            } else {
                new ReceiptDialog(this, selected).setVisible(true);
            }
        });
        JButton closeButton = UITheme.button("Close", UITheme.NAVY_LIGHT);
        closeButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.setBorder(new EmptyBorder(0, 0, 14, 0));
        buttons.add(receiptButton);
        buttons.add(closeButton);
        add(buttons, BorderLayout.SOUTH);

        table.setReservations(new ReservationDAO().getBookingHistory());
    }
}
