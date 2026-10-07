package gui;

import dao.RoomDAO;
import model.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomsDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final RoomDAO dao = new RoomDAO();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Room", "Type", "Price", "Capacity"}, 0) { public boolean isCellEditable(int r, int c) { return false; } };
    private final JTable table = new JTable(model);
    private List<Room> rooms;

    public RoomsDialog(JFrame parent) {
        super(parent, "Manage Rooms", true); setSize(720, 450); setLocationRelativeTo(parent); setLayout(new BorderLayout(10, 10));
        table.setRowHeight(28); add(new JScrollPane(table), BorderLayout.CENTER);
        JButton add = UITheme.button("Add", UITheme.TEAL); add.addActionListener(e -> edit(null));
        JButton edit = UITheme.button("Edit", UITheme.NAVY); edit.addActionListener(e -> selectedEdit());
        JButton delete = UITheme.button("Delete", UITheme.ERROR); delete.addActionListener(e -> delete());
        JButton close = UITheme.button("Close", UITheme.NAVY_LIGHT); close.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(); buttons.add(add); buttons.add(edit); buttons.add(delete); buttons.add(close); add(buttons, BorderLayout.SOUTH); load();
    }
    private void load() { rooms = dao.getAllRooms(); model.setRowCount(0); for (Room r : rooms) model.addRow(new Object[]{r.getRoomNumber(), r.getRoomType(), r.getPrice(), r.getMaxGuests()}); }
    private void selectedEdit() { int row = table.getSelectedRow(); if (row < 0) { JOptionPane.showMessageDialog(this, "Select a room first."); return; } edit(rooms.get(table.convertRowIndexToModel(row))); }
    private void edit(Room room) {
        JTextField number = new JTextField(room == null ? "" : room.getRoomNumber()); JComboBox<String> type = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite"}); if (room != null) type.setSelectedItem(room.getRoomType());
        JTextField price = new JTextField(room == null ? "" : String.valueOf(room.getPrice())); JComboBox<Integer> capacity = new JComboBox<>(new Integer[]{1,2,3}); if (room != null) capacity.setSelectedItem(room.getMaxGuests());
        JPanel form = new JPanel(new GridLayout(0,2,8,8)); form.add(new JLabel("Room number:")); form.add(number); form.add(new JLabel("Type:")); form.add(type); form.add(new JLabel("Price:")); form.add(price); form.add(new JLabel("Max guests:")); form.add(capacity);
        if (JOptionPane.showConfirmDialog(this, form, room == null ? "Add room" : "Edit room", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try { if (!number.getText().matches("\\d{1,10}") || Double.parseDouble(price.getText()) <= 0) throw new IllegalArgumentException(); boolean ok = room == null ? dao.addRoom(number.getText(), (String)type.getSelectedItem(), Double.parseDouble(price.getText()), (Integer)capacity.getSelectedItem()) : dao.updateRoom(room.getRoomNumber(), (String)type.getSelectedItem(), Double.parseDouble(price.getText()), (Integer)capacity.getSelectedItem()); if (!ok) JOptionPane.showMessageDialog(this, "Room number may already exist or the database rejected the change."); else load(); } catch (Exception e) { JOptionPane.showMessageDialog(this, "Use a numeric room number and a price greater than zero."); }
    }
    private void delete() { int row = table.getSelectedRow(); if (row < 0) { JOptionPane.showMessageDialog(this, "Select a room first."); return; } String number = rooms.get(table.convertRowIndexToModel(row)).getRoomNumber(); if (dao.hasReservations(number)) { JOptionPane.showMessageDialog(this, "Room " + number + " has bookings, so it can't be deleted."); return; } if (dao.deleteRoom(number)) load(); }
}
