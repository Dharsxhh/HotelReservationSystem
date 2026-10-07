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
    private static final long serialVersionUID = 1L;
    private final ReservationDAO dao = new ReservationDAO();
    private final User user;
    private final JTextField search = new JTextField(25);
    private final ReservationTable table = new ReservationTable();

    public CustomerBookingsDialog(Window parent, User user) {
        super(parent, "My Bookings", ModalityType.APPLICATION_MODAL); this.user = user; setSize(1050, 520); setLocationRelativeTo(parent); setLayout(new BorderLayout(10, 10));
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
    private void modify() {
        BookedRoom value = selected(); if (value == null) return;
        if (!value.isActive()) { JOptionPane.showMessageDialog(this, "This booking is no longer active."); return; }
        if (!value.getCheckIn().isAfter(java.time.LocalDate.now())) { JOptionPane.showMessageDialog(this, "Bookings can only be changed before the check-in date."); return; }
        EditReservationDialog editor = new EditReservationDialog(this, value, true); editor.setVisible(true);
        if (!editor.isSaved()) return;
        if (editor.getRoom() != null && !dao.isRoomFree(editor.getRoom().getRoomNumber(), editor.getCheckIn(), editor.getCheckOut(), value.getReservationId())) {
            JOptionPane.showMessageDialog(this, "Room is booked for those dates."); return;
        }
        if (!dao.updateReservationForUser(value.getReservationId(), user.getId(), editor.getCustomerName(), editor.getContactNumber(), editor.getCheckIn(), editor.getCheckOut(), editor.getRoom().getRoomNumber())) {
            JOptionPane.showMessageDialog(this, "This booking is no longer active."); return;
        }
        Toast.show(this, "Reservation updated successfully"); load();
    }
    private void cancel() {
        BookedRoom value = selected(); if (value == null) return;
        if (!value.isActive()) { JOptionPane.showMessageDialog(this, "This booking is no longer active."); return; }
        if (!value.getCheckIn().isAfter(java.time.LocalDate.now())) { JOptionPane.showMessageDialog(this, "Bookings can only be changed before the check-in date."); return; }
        if (JOptionPane.showConfirmDialog(this, "Cancel reservation #" + value.getReservationId() + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (!dao.cancelReservationForUser(value.getReservationId(), user.getId())) JOptionPane.showMessageDialog(this, "This booking is no longer active."); else Toast.show(this, "Reservation cancelled");
            load();
        }
    }
    private void receipt() { BookedRoom value = selected(); if (value != null) new ReceiptDialog(this, value).setVisible(true); }
}
