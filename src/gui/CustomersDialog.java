package gui;

import dao.UserDAO;
import model.CustomerSummary;
import model.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomersDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final UserDAO dao = new UserDAO();
    private final JTextField search = new JTextField(25);
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Name", "Username", "Phone", "Email", "Joined", "Bookings"}, 0) { public boolean isCellEditable(int r, int c) { return false; } };
    private final JTable table = new JTable(model);
    private List<CustomerSummary> customers = new java.util.ArrayList<>();

    public CustomersDialog(JFrame parent) {
        super(parent, "Customer Accounts", true); setSize(900, 500); setLocationRelativeTo(parent); setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT)); top.add(new JLabel("Search customers:")); top.add(search); add(top, BorderLayout.NORTH);
        table.setRowHeight(28); add(new JScrollPane(table), BorderLayout.CENTER);
        JButton bookings = UITheme.button("View bookings", UITheme.TEAL); bookings.addActionListener(e -> viewBookings());
        JButton close = UITheme.button("Close", UITheme.NAVY_LIGHT); close.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(); bottom.add(bookings); bottom.add(close); add(bottom, BorderLayout.SOUTH);
        search.getDocument().addDocumentListener(new DocumentListener() { public void insertUpdate(DocumentEvent e){load();} public void removeUpdate(DocumentEvent e){load();} public void changedUpdate(DocumentEvent e){load();} });
        table.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(java.awt.event.MouseEvent e) { if (e.getClickCount() == 2) viewBookings(); } }); load();
    }
    private void load() { customers = dao.getCustomers(search.getText()); model.setRowCount(0); for (CustomerSummary c : customers) model.addRow(new Object[]{c.getName(), c.getUsername(), c.getPhone(), c.getEmail(), c.getJoined(), c.getBookings()}); }
    private void viewBookings() { int row = table.getSelectedRow(); if (row < 0) { JOptionPane.showMessageDialog(this, "Select a customer first."); return; } CustomerSummary c = customers.get(table.convertRowIndexToModel(row)); new CustomerBookingsDialog(this, new User(c.getId(), c.getUsername(), c.getName(), c.getPhone(), c.getEmail(), "CUSTOMER")).setVisible(true); }
}
