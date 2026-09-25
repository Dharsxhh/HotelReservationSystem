package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.ReservationDAO;
import model.BookedRoom;

public class ViewBookingsDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    private DefaultTableModel tableModel;
    private JTable table;
    private ReservationDAO dao;

    public ViewBookingsDialog(JFrame parent) {
        super(parent, "Current Reservations", true);
        setSize(800, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        dao = new ReservationDAO();

        // Setup the Table
        String[] columns = {"Room", "Type", "Guest Name(s)", "Contact", "Check-In", "Check-Out"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevents accidental typing in the table cells
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadTableData();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // --- NEW: Checkout Button ---
        JButton checkoutButton = new JButton("Checkout Selected Room");
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutButton.setBackground(new Color(217, 83, 79)); // Professional red
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        
        checkoutButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String roomNumber = (String) tableModel.getValueAt(selectedRow, 0);
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Checkout guest from Room " + roomNumber + "?\nThis will make the room available for new bookings.", 
                    "Confirm Checkout", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    if (dao.checkoutRoom(roomNumber)) {
                        JOptionPane.showMessageDialog(this, "Checkout successful. Room " + roomNumber + " is now available.");
                        loadTableData(); // Instantly removes them from the visual list
                    } else {
                        JOptionPane.showMessageDialog(this, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a booking from the list first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        closeButton.addActionListener(e -> dispose());
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        bottomPanel.add(checkoutButton);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Helper method to fetch and refresh the table contents
    private void loadTableData() {
        tableModel.setRowCount(0); // Clear old data
        List<BookedRoom> bookings = dao.getAllBookings();
        for (BookedRoom b : bookings) {
            Object[] row = {
                b.getRoomNumber(), b.getRoomType(), b.getCustomerName(), 
                b.getContact(), b.getCheckIn(), b.getCheckOut()
            };
            tableModel.addRow(row);
        }
    }
}