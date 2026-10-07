package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.ReservationDAO;
import model.BookedRoom;

public class ReservationSearchDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final ReservationDAO dao = new ReservationDAO();
    private final JTextField searchField = new JTextField(25);
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<BookedRoom> results = new java.util.ArrayList<>();

    public ReservationSearchDialog(JFrame parent) {
        super(parent, "Search and Modify Reservations", true);
        setSize(1000, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search guest, contact, room, or reservation ID:"));
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);
        JButton showAllButton = new JButton("Show All");
        searchPanel.add(showAllButton);
        add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"Reservation ID", "Room", "Type", "Guest Name(s)", "Contact", "Check-In", "Check-Out"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton modifyButton = new JButton("Modify Selected Reservation");
        modifyButton.addActionListener(e -> modifySelected());
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttons = new JPanel();
        buttons.add(modifyButton);
        buttons.add(closeButton);
        add(buttons, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> loadResults(searchField.getText()));
        showAllButton.addActionListener(e -> {
            searchField.setText("");
            loadResults("");
        });
        searchField.addActionListener(e -> loadResults(searchField.getText()));
        loadResults("");
    }

    private void loadResults(String searchText) {
        results = dao.searchReservations(searchText);
        tableModel.setRowCount(0);
        for (BookedRoom reservation : results) {
            tableModel.addRow(new Object[]{
                reservation.getReservationId(), reservation.getRoomNumber(), reservation.getRoomType(),
                reservation.getCustomerName(), reservation.getContact(),
                reservation.getCheckIn(), reservation.getCheckOut()
            });
        }
    }

    private void modifySelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a reservation first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BookedRoom selected = results.get(table.convertRowIndexToModel(row));
        EditReservationDialog editor = new EditReservationDialog(this, selected);
        editor.setVisible(true);
        if (editor.isSaved()) {
            boolean updated = dao.updateReservation(
                selected.getReservationId(), editor.getCustomerName(), editor.getContactNumber(),
                editor.getCheckIn(), editor.getCheckOut(), editor.getTotal());
            JOptionPane.showMessageDialog(this,
                updated ? "Reservation updated successfully." : "Could not update the reservation.",
                updated ? "Success" : "Database Error",
                updated ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (updated) loadResults(searchField.getText());
        }
    }
}
