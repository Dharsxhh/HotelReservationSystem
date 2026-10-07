package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import model.BookedRoom;
import util.Billing;

// The reservations table used by Current Bookings, Search and History, so all three look the same.
// Click a column header to sort.
public class ReservationTable extends JTable {
    private static final long serialVersionUID = 1L;
    private static final String[] COLUMNS =
        {"ID", "Room", "Type", "Guest name(s)", "Contact", "Check-in", "Check-out", "Total", "Status"};

    private final DefaultTableModel model;
    private List<BookedRoom> reservations = new ArrayList<>();

    public ReservationTable() {
        model = new DefaultTableModel(COLUMNS, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        setModel(model);
        setRowHeight(28);
        setAutoCreateRowSorter(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getColumnModel().getColumn(0).setPreferredWidth(40);
        getColumnModel().getColumn(1).setPreferredWidth(50);
        getColumnModel().getColumn(3).setPreferredWidth(200);
    }

    public void setReservations(List<BookedRoom> list) {
        reservations = list;
        model.setRowCount(0);
        for (BookedRoom b : list) {
            model.addRow(new Object[]{
                b.getReservationId(), b.getRoomNumber(), b.getRoomType(), b.getCustomerName(), b.getContact(),
                b.getCheckIn(), b.getCheckOut(), Billing.money(b.getTotal()), b.getStatusLabel()
            });
        }
    }

    // The reservation in the selected row, or null when nothing is selected.
    public BookedRoom getSelectedReservation() {
        int row = getSelectedRow();
        return row < 0 ? null : reservations.get(convertRowIndexToModel(row));
    }
}
