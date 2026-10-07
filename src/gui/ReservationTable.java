package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
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
        getColumnModel().getColumn(8).setCellRenderer(new StatusRenderer());
    }

    public void setReservations(List<BookedRoom> list) {
        reservations = list;
        model.setRowCount(0);
        if (list == null || list.isEmpty()) setToolTipText("No reservations found"); else setToolTipText(null);
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

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (model.getRowCount() == 0) {
            graphics.setColor(UITheme.muted());
            String text = "No reservations yet - bookings will appear here";
            FontMetrics metrics = graphics.getFontMetrics();
            graphics.drawString(text, (getWidth() - metrics.stringWidth(text)) / 2, getHeight() / 2);
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            String status = String.valueOf(value);
            if ("Booked".equals(status)) { label.setBackground(new Color(211, 240, 235)); label.setForeground(UITheme.TEAL); }
            else if ("Cancelled".equals(status)) { label.setBackground(new Color(250, 220, 220)); label.setForeground(UITheme.ERROR); }
            else { label.setBackground(new Color(225, 229, 234)); label.setForeground(UITheme.muted()); }
            label.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            return label;
        }
    }
}
