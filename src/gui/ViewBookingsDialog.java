package gui;

import dao.ReservationDAO;
import model.BookedRoom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Active bookings, with checkout + receipt.
public class ViewBookingsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final ReservationDAO dao = new ReservationDAO();
    private final ReservationTable table = new ReservationTable();

    public ViewBookingsDialog(JFrame parent) {
        super(parent, "Current Bookings", true);
        setSize(1000, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(12, 12, 12, 12));
        add(scroll, BorderLayout.CENTER);

        JButton checkoutButton = UITheme.button("Check Out & Print Bill", UITheme.TEAL);
        checkoutButton.addActionListener(e -> checkOutSelected());
        JButton closeButton = UITheme.button("Close", UITheme.NAVY_LIGHT);
        closeButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.setBorder(new EmptyBorder(0, 0, 14, 0));
        buttons.add(checkoutButton);
        buttons.add(closeButton);
        add(buttons, BorderLayout.SOUTH);

        table.setReservations(dao.getCurrentBookings());
    }

    private void checkOutSelected() {
        BookedRoom selected = table.getSelectedReservation();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a booking first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Check out " + selected.getCustomerName() + " from Room " + selected.getRoomNumber() + "?",
            "Confirm Checkout", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (dao.checkOut(selected.getReservationId())) {
            new ReceiptDialog(this, selected).setVisible(true);
            table.setReservations(dao.getCurrentBookings());
        } else {
            JOptionPane.showMessageDialog(this, "Could not check out this booking.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
