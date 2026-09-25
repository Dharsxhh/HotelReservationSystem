package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.ReservationDAO;
import model.BookedRoom;

public class HistoryDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;

    public HistoryDialog(JFrame parent) {
        super(parent, "Complete Hotel Booking History", true);
        setSize(850, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Setup the Table
        String[] columns = {"Room", "Type", "Guest Name(s)", "Contact", "Check-In", "Check-Out"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Fetch ALL data from Database
        ReservationDAO dao = new ReservationDAO();
        List<BookedRoom> history = dao.getBookingHistory();

        for (BookedRoom b : history) {
            Object[] row = {
                b.getRoomNumber(), b.getRoomType(), b.getCustomerName(), 
                b.getContact(), b.getCheckIn(), b.getCheckOut()
            };
            tableModel.addRow(row);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        closeButton.addActionListener(e -> dispose());
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}