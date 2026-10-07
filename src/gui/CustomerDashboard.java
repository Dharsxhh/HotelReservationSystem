package gui;

import dao.ReservationDAO;
import dao.RoomDAO;
import model.Room;
import model.User;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CustomerDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private final User user;
    private final RoomDAO roomDAO = new RoomDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final DefaultTableModel roomsModel = new DefaultTableModel(new String[]{"Room", "Type", "Sharing", "Price / night"}, 0) {
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable roomsTable = new JTable(roomsModel);
    private final JSpinner checkIn = UITheme.dateSpinner(LocalDate.now().plusDays(1));
    private final JSpinner checkOut = UITheme.dateSpinner(LocalDate.now().plusDays(2));
    private List<Room> rooms = new java.util.ArrayList<>();

    public CustomerDashboard(User user) {
        UITheme.install(); this.user = user;
        setTitle("StayEase | Customer Home"); setSize(1000, 650); setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLayout(new BorderLayout());
        add(header(), BorderLayout.NORTH); add(content(), BorderLayout.CENTER); refreshRooms();
    }

    private JPanel header() {
        JPanel header = new JPanel(new BorderLayout()); header.setBackground(UITheme.NAVY); header.setBorder(new EmptyBorder(18, 25, 18, 25));
        JLabel title = new JLabel("STAYEASE  •  Guest Home"); title.setForeground(UITheme.WHITE); title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); right.setOpaque(false);
        JLabel name = new JLabel("Welcome, " + user.getFullName()); name.setForeground(new Color(220,230,240));
        JButton theme = UITheme.button(UITheme.isDarkMode() ? "Light mode" : "Dark mode", UITheme.NAVY_LIGHT);
        theme.addActionListener(e -> { UITheme.toggleDarkMode(this); theme.setText(UITheme.isDarkMode() ? "Light mode" : "Dark mode"); });
        JButton bookings = UITheme.button("My bookings", UITheme.TEAL); bookings.addActionListener(e -> new CustomerBookingsDialog(this, user).setVisible(true));
        JButton profile = UITheme.button("Profile", UITheme.TEAL); profile.addActionListener(e -> new ProfileDialog(this, Session.current()).setVisible(true));
        JButton logout = UITheme.button("Sign out", UITheme.NAVY_LIGHT); logout.addActionListener(e -> signOut());
        right.add(name); right.add(theme); right.add(profile); right.add(bookings); right.add(logout); header.add(title, BorderLayout.WEST); header.add(right, BorderLayout.EAST); return header;
    }

    private JPanel content() {
        JPanel root = new JPanel(new BorderLayout(12, 12)); root.setBackground(UITheme.BACKGROUND); root.setBorder(new EmptyBorder(22, 25, 22, 25));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); top.setOpaque(false);
        top.add(new JLabel("Find a room: check-in")); top.add(checkIn); top.add(new JLabel("check-out")); top.add(checkOut);
        JButton refresh = UITheme.button("Search availability", UITheme.TEAL); refresh.addActionListener(e -> refreshRooms()); top.add(refresh); root.add(top, BorderLayout.NORTH);
        roomsTable.setRowHeight(32); roomsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        root.add(new JScrollPane(roomsTable), BorderLayout.CENTER);
        JButton book = UITheme.button("Book selected room", UITheme.TEAL); book.addActionListener(e -> bookSelected());
        JButton history = UITheme.button("My bookings", UITheme.NAVY); history.addActionListener(e -> new CustomerBookingsDialog(this, user).setVisible(true));
        JPanel bottom = new JPanel(); bottom.add(book); bottom.add(history); root.add(bottom, BorderLayout.SOUTH); return root;
    }

    private void refreshRooms() {
        roomsModel.setRowCount(0); rooms = roomDAO.getAvailableRooms(UITheme.getDate(checkIn), UITheme.getDate(checkOut));
        for (Room room : rooms) roomsModel.addRow(new Object[]{room.getRoomNumber(), room.getRoomType(), room.getSharingLabel(), room.getPrice()});
    }

    private void bookSelected() {
        int row = roomsTable.getSelectedRow(); if (row < 0) { JOptionPane.showMessageDialog(this, "Select a room first."); return; }
        Room room = rooms.get(roomsTable.convertRowIndexToModel(row));
        BookingDialog dialog = new BookingDialog(this, room, UITheme.getDate(checkIn), UITheme.getDate(checkOut), user.getFullName(), user.getPhone());
        dialog.setVisible(true); if (!dialog.isConfirmed()) return;
        long id = reservationDAO.createReservation(dialog.getCustomerName(), dialog.getContactNumber(), dialog.getCheckIn(), dialog.getCheckOut(), room.getRoomNumber(), user.getId());
        if (id > 0) JOptionPane.showMessageDialog(this, "Reservation #" + id + " created successfully.");
        else JOptionPane.showMessageDialog(this, "The room is no longer available.", "Booking failed", JOptionPane.ERROR_MESSAGE);
        refreshRooms();
    }

    private void signOut() { Session.logout(); dispose(); new LoginFrame().setVisible(true); }
}
