package gui;

import dao.ReservationDAO;
import model.BookedRoom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ReservationSearchDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final ReservationDAO dao = new ReservationDAO();
    private final JTextField searchField = new JTextField(28);
    private final ReservationTable table = new ReservationTable();
    private final JLabel resultLabel = new JLabel(" ");

    public ReservationSearchDialog(JFrame parent) {
        super(parent, "Search, Modify or Cancel Reservations", true);
        setSize(1050, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBorder(new EmptyBorder(14, 12, 0, 12));
        searchPanel.add(new JLabel("Search by guest name, contact, room or reservation ID:"));
        searchPanel.add(searchField);
        resultLabel.setForeground(UITheme.MUTED);
        searchPanel.add(resultLabel);
        add(searchPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 12, 0, 12));
        add(scroll, BorderLayout.CENTER);

        JButton modifyButton = UITheme.button("Modify Selected", UITheme.TEAL);
        modifyButton.addActionListener(e -> modifySelected());
        JButton cancelButton = UITheme.button("Cancel Selected Reservation", UITheme.ERROR);
        cancelButton.addActionListener(e -> cancelSelected());
        JButton closeButton = UITheme.button("Close", UITheme.NAVY_LIGHT);
        closeButton.addActionListener(e -> dispose());
        JPanel buttons = new JPanel();
        buttons.setBorder(new EmptyBorder(0, 0, 12, 0));
        buttons.add(modifyButton);
        buttons.add(cancelButton);
        buttons.add(closeButton);
        add(buttons, BorderLayout.SOUTH);

        // Results update while you type.
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadResults(); }
            public void removeUpdate(DocumentEvent e) { loadResults(); }
            public void changedUpdate(DocumentEvent e) { loadResults(); }
        });
        loadResults();
    }

    private void loadResults() {
        java.util.List<BookedRoom> results = dao.searchReservations(searchField.getText());
        table.setReservations(results);
        resultLabel.setText(results.size() + (results.size() == 1 ? " result" : " results"));
    }

    // Returns the selected active booking, or null after telling the user why not.
    private BookedRoom selectedActiveBooking(String action) {
        BookedRoom selected = table.getSelectedReservation();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a reservation first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!selected.isActive()) {
            JOptionPane.showMessageDialog(this, "Reservation #" + selected.getReservationId() + " is already "
                + selected.getStatusLabel().toLowerCase() + ", so it can't be " + action + ".",
                "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return selected;
    }

    private void modifySelected() {
        BookedRoom selected = selectedActiveBooking("modified");
        if (selected == null) return;

        EditReservationDialog editor = new EditReservationDialog(this, selected);
        editor.setVisible(true);
        if (!editor.isSaved()) return;

        boolean updated = dao.updateReservation(selected.getReservationId(), editor.getCustomerName(),
            editor.getContactNumber(), editor.getCheckIn(), editor.getCheckOut(), editor.getRoom().getRoomNumber());
        if (updated) {
            JOptionPane.showMessageDialog(this, "Reservation #" + selected.getReservationId() + " updated.");
        } else {
            JOptionPane.showMessageDialog(this, "Could not update the reservation. The room may have just been booked.",
                "Not Saved", JOptionPane.ERROR_MESSAGE);
        }
        loadResults();
    }

    private void cancelSelected() {
        BookedRoom selected = selectedActiveBooking("cancelled");
        if (selected == null) return;

        int answer = JOptionPane.showConfirmDialog(this,
            "Cancel reservation #" + selected.getReservationId() + " for " + selected.getCustomerName() + "?\n"
                + "Room " + selected.getRoomNumber() + " will become free for those dates.",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;

        if (dao.cancelReservation(selected.getReservationId())) {
            JOptionPane.showMessageDialog(this, "Reservation #" + selected.getReservationId() + " cancelled.");
        } else {
            JOptionPane.showMessageDialog(this, "Could not cancel the reservation.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        loadResults();
    }
}
