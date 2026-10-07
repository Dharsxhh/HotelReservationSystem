package gui;

import dao.ReservationDAO;
import model.BookedRoom;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class CustomerBookingsDialog extends JDialog {
    private final ReservationDAO dao = new ReservationDAO();
    private final User user;
    private final JTextField search = new JTextField(25);
    private final ReservationTable table = new ReservationTable();

    public CustomerBookingsDialog(JFrame parent, User user) {
        super(parent, "My Bookings", true); this.user = user; setSize(1050, 520); setLocationRelativeTo(parent); setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT)); top.setBorder(new EmptyBorder(12, 12, 0, 12));
        top.add(new JLabel("Search my reservations:")); top.add(search); add(top, BorderLayout.NORTH); add(new JScrollPane(table), BorderLayout.CENTER);
        JButton modify = UITheme.button("Modify", UITheme.TEAL); modify.addActionListener(e -> modify());
        JButton cancel = UITheme.button("Cancel", UITheme.ERROR); cancel.addActionListener(e -> cancel());
        JButton receipt = UITheme.button("Receipt", UITheme.NAVY); receipt.addActionListener(e -> receipt());
        JButton close = UITheme.button("Close", UITheme.NAVY_LIGHT); close.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(); buttons.add(modify); buttons.add(cancel); buttons.add(receipt); buttons.add(close); add(buttons, BorderLayout.SOUTH);
        search.getDocument().addDocumentListener(new DocumentListener() { public void insertUpdate(DocumentEvent e){load();} public void removeUpdate(DocumentEvent e){load();} public void changedUpdate(DocumentEvent e){load();} });
        load();
    }
    private void load() { table.setReservations(dao.searchReservationsForUser(search.getText(), user.getId())); }
    private BookedRoom selected() { BookedRoom value = table.getSelectedReservation(); if (value == null) JOptionPane.showMessageDialog(this, "Select a booking first."); return value; }
    private void modify() { BookedRoom value = selected(); if (value == null || !value.isActive() || !value.getCheckIn().isAfter(java.time.LocalDate.now())) { if (value != null) JOptionPane.showMessageDialog(this, "Only future active bookings can be modified."); return; }
        EditReservationDialog editor = new EditReservationDialog(this, value); editor.setVisible(true); if (editor.isSaved() && dao.updateReservationForUser(value.getReservationId(), user.getId(), editor.getCustomerName(), editor.getContactNumber(), editor.getCheckIn(), editor.getCheckOut(), editor.getRoom().getRoomNumber())) load(); }
    private void cancel() { BookedRoom value = selected(); if (value == null) return; if (JOptionPane.showConfirmDialog(this, "Cancel reservation #" + value.getReservationId() + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { if (!dao.cancelReservationForUser(value.getReservationId(), user.getId())) JOptionPane.showMessageDialog(this, "This booking cannot be cancelled now."); load(); } }
    private void receipt() { BookedRoom value = selected(); if (value != null) new ReceiptDialog(this, value).setVisible(true); }
}
