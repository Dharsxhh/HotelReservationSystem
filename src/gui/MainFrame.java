package gui;

import dao.DatabaseHelper;
import dao.ReservationDAO;
import dao.RoomDAO;
import model.Room;
import util.Billing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final RoomDAO roomDAO = new RoomDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final JLabel totalRoomsValue = statValue();
    private final JLabel availableRoomsValue = statValue();
    private final JLabel occupiedRoomsValue = statValue();
    private final JLabel reservationValue = statValue();
    private final JLabel statusLabel = new JLabel("Ready");
    private final JLabel databaseLabel = new JLabel();
    private final String loggedInUser;

    // Rooms table + the filters above it
    private final DefaultTableModel roomTableModel = new DefaultTableModel(
            new String[]{"Room", "Type", "Sharing", "Price / night"}, 0) {
        private static final long serialVersionUID = 1L;
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable roomTable = new JTable(roomTableModel);
    private final List<Room> shownRooms = new ArrayList<>();
    private final JSpinner checkInSpinner = UITheme.dateSpinner(LocalDate.now());
    private final JSpinner checkOutSpinner = UITheme.dateSpinner(LocalDate.now().plusDays(1));
    private final JComboBox<String> sharingFilter =
        new JComboBox<>(new String[]{"All rooms", "Single sharing", "Double sharing", "Triple sharing"});
    private final JLabel roomsHint = new JLabel(" ");

    public MainFrame() {
        this("admin");
    }

    public MainFrame(String loggedInUser) {
        UITheme.install();
        this.loggedInUser = loggedInUser;
        setTitle("StayEase | Hotel Operations Dashboard");
        setSize(1150, 740);
        setMinimumSize(new Dimension(980, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BACKGROUND);

        add(createHeader(), BorderLayout.NORTH);
        add(createDashboard(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
        refreshDashboard();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.NAVY);
        header.setBorder(new EmptyBorder(18, 28, 18, 28));

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        JLabel logo = new JLabel("STAYEASE");
        logo.setForeground(UITheme.GOLD);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel title = new JLabel("Hotel Operations Dashboard");
        title.setForeground(UITheme.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        brand.add(logo);
        brand.add(Box.createVerticalStrut(3));
        brand.add(title);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userPanel.setOpaque(false);
        JLabel user = new JLabel("Signed in as  " + loggedInUser);
        user.setForeground(new Color(215, 226, 237));
        user.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton logout = UITheme.button("Sign out", UITheme.NAVY_LIGHT);
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        userPanel.add(user);
        userPanel.add(logout);

        header.add(brand, BorderLayout.WEST);
        header.add(userPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createDashboard() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(UITheme.BACKGROUND);
        root.setBorder(new EmptyBorder(22, 28, 18, 28));

        JPanel top = new JPanel(new BorderLayout(0, 15));
        top.setOpaque(false);
        JLabel welcome = new JLabel("Good day, " + loggedInUser);
        welcome.setForeground(UITheme.TEXT);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel subtitle = new JLabel("Here is your property snapshot for today.");
        subtitle.setForeground(UITheme.MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel welcomeText = new JPanel();
        welcomeText.setOpaque(false);
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));
        welcomeText.add(welcome);
        welcomeText.add(Box.createVerticalStrut(3));
        welcomeText.add(subtitle);
        top.add(welcomeText, BorderLayout.WEST);
        top.add(createStatCards(), BorderLayout.SOUTH);

        root.add(top, BorderLayout.NORTH);
        root.add(createWorkspace(), BorderLayout.CENTER);
        return root;
    }

    private JPanel createStatCards() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 0));
        cards.setOpaque(false);
        cards.add(statCard("Total rooms", totalRoomsValue, UITheme.NAVY));
        cards.add(statCard("Free tonight", availableRoomsValue, UITheme.TEAL));
        cards.add(statCard("Occupied tonight", occupiedRoomsValue, new Color(190, 123, 31)));
        cards.add(statCard("Active reservations", reservationValue, new Color(105, 76, 150)));
        return cards;
    }

    private JPanel statCard(String label, JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(8, 3));
        card.setBackground(UITheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            new EmptyBorder(12, 14, 12, 14)));
        JLabel name = new JLabel(label.toUpperCase());
        name.setForeground(UITheme.MUTED);
        name.setFont(new Font("Segoe UI", Font.BOLD, 11));
        card.add(name, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JPanel createWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(18, 0));
        workspace.setOpaque(false);
        workspace.add(createRoomsPanel(), BorderLayout.CENTER);
        workspace.add(createQuickActionsPanel(), BorderLayout.EAST);
        return workspace;
    }

    private JPanel createRoomsPanel() {
        JPanel panel = whitePanel(new BorderLayout(0, 12));

        // Title + filters: pick dates and room type, the table updates immediately.
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        filters.add(new JLabel("Check-in"));
        filters.add(checkInSpinner);
        filters.add(Box.createHorizontalStrut(6));
        filters.add(new JLabel("Check-out"));
        filters.add(checkOutSpinner);
        filters.add(Box.createHorizontalStrut(6));
        filters.add(sharingFilter);

        roomsHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel heading = new JPanel(new BorderLayout(0, 8));
        heading.setOpaque(false);
        heading.add(UITheme.title("Available rooms"), BorderLayout.NORTH);
        heading.add(filters, BorderLayout.CENTER);
        heading.add(roomsHint, BorderLayout.SOUTH);
        panel.add(heading, BorderLayout.NORTH);

        roomTable.setRowHeight(32);
        roomTable.setAutoCreateRowSorter(true);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.setSelectionBackground(new Color(218, 239, 237));
        roomTable.setSelectionForeground(UITheme.TEXT);
        // Double-click a room to book it.
        roomTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openBooking();
            }
        });
        JScrollPane scroll = new JScrollPane(roomTable);
        scroll.getViewport().setBackground(UITheme.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        checkInSpinner.addChangeListener(e -> loadRooms());
        checkOutSpinner.addChangeListener(e -> loadRooms());
        sharingFilter.addActionListener(e -> loadRooms());
        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = whitePanel(new BorderLayout(0, 12));
        panel.setPreferredSize(new Dimension(280, 0));
        panel.add(UITheme.title("Quick actions"), BorderLayout.NORTH);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        JButton book = actionButton("Book selected room", UITheme.TEAL);
        book.addActionListener(e -> openBooking());
        JButton current = actionButton("Current bookings / check out", UITheme.NAVY);
        current.addActionListener(e -> openCurrentBookings());
        JButton search = actionButton("Search / modify / cancel", new Color(105, 76, 150));
        search.addActionListener(e -> openSearch());
        JButton history = actionButton("Booking history & receipts", new Color(190, 123, 31));
        history.addActionListener(e -> new HistoryDialog(this).setVisible(true));
        JButton refresh = actionButton("Refresh dashboard", UITheme.NAVY_LIGHT);
        refresh.addActionListener(e -> refreshDashboard());

        for (JButton button : new JButton[]{book, current, search, history, refresh}) {
            actions.add(button);
            actions.add(Box.createVerticalStrut(10));
        }
        panel.add(actions, BorderLayout.CENTER);
        return panel;
    }

    private JButton actionButton(String text, Color color) {
        JButton button = UITheme.button(text, color);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return button;
    }

    private JPanel createStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(UITheme.WHITE);
        status.setBorder(new EmptyBorder(7, 28, 7, 28));
        statusLabel.setForeground(UITheme.MUTED);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        databaseLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        status.add(statusLabel, BorderLayout.WEST);
        status.add(databaseLabel, BorderLayout.EAST);
        return status;
    }

    private void openBooking() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a room from the table first.", "No Room Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Room room = shownRooms.get(roomTable.convertRowIndexToModel(row));
        BookingDialog dialog = new BookingDialog(this, room, UITheme.getDate(checkInSpinner), UITheme.getDate(checkOutSpinner));
        dialog.setVisible(true);
        if (!dialog.isConfirmed()) return;

        long reservationId = reservationDAO.createReservation(dialog.getCustomerName(), dialog.getContactNumber(),
            dialog.getCheckIn(), dialog.getCheckOut(), room.getRoomNumber());
        if (reservationId > 0) {
            JOptionPane.showMessageDialog(this,
                "Booking #" + reservationId + " confirmed for Room " + room.getRoomNumber() + ".\n"
                    + "Total: " + Billing.money(dialog.getTotal()) + " (including GST)\n"
                    + "Use the reservation number to find this booking later.",
                "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
            statusLabel.setText("Booking #" + reservationId + " created for room " + room.getRoomNumber());
        } else {
            JOptionPane.showMessageDialog(this, "Room " + room.getRoomNumber() + " was just booked for those dates, "
                + "or the database could not be reached.", "Booking Not Saved", JOptionPane.ERROR_MESSAGE);
        }
        refreshDashboard();
    }

    private void openCurrentBookings() {
        new ViewBookingsDialog(this).setVisible(true);
        refreshDashboard();
    }

    private void openSearch() {
        new ReservationSearchDialog(this).setVisible(true);
        refreshDashboard();
    }

    // Fills the rooms table with rooms free for the chosen dates and sharing type.
    private void loadRooms() {
        LocalDate checkIn = UITheme.getDate(checkInSpinner);
        LocalDate checkOut = UITheme.getDate(checkOutSpinner);
        roomTableModel.setRowCount(0);
        shownRooms.clear();

        if (!checkOut.isAfter(checkIn)) {
            roomsHint.setForeground(UITheme.ERROR);
            roomsHint.setText("Check-out must be after check-in.");
            return;
        }

        int wantedGuests = sharingFilter.getSelectedIndex();   // 0 = all, 1 = single, 2 = double, 3 = triple
        for (Room room : roomDAO.getAvailableRooms(checkIn, checkOut)) {
            if (wantedGuests != 0 && room.getMaxGuests() != wantedGuests) continue;
            shownRooms.add(room);
            roomTableModel.addRow(new Object[]{
                room.getRoomNumber(), room.getRoomType(), room.getSharingLabel(), Billing.money(room.getPrice())
            });
        }

        long nights = Billing.nights(checkIn, checkOut);
        roomsHint.setForeground(UITheme.MUTED);
        roomsHint.setText(shownRooms.size() + " room(s) free for " + nights + (nights == 1 ? " night" : " nights")
            + "  •  select one and click Book (or double-click)");
    }

    private void refreshDashboard() {
        boolean connected = DatabaseHelper.canConnect();
        databaseLabel.setText(connected ? "● Database connected" : "● Database not reachable - check db.properties");
        databaseLabel.setForeground(connected ? UITheme.TEAL : UITheme.ERROR);

        int totalRooms = roomDAO.getAllRooms().size();
        int occupied = reservationDAO.countOccupiedToday();
        totalRoomsValue.setText(String.valueOf(totalRooms));
        availableRoomsValue.setText(String.valueOf(Math.max(0, totalRooms - occupied)));
        occupiedRoomsValue.setText(String.valueOf(occupied));
        reservationValue.setText(String.valueOf(reservationDAO.countActiveReservations()));
        loadRooms();
        statusLabel.setText("Dashboard refreshed • " + shownRooms.size() + " rooms free for the selected dates");
    }

    private JPanel whitePanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(UITheme.WHITE);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        return panel;
    }

    private static JLabel statValue() {
        JLabel label = new JLabel("0");
        label.setForeground(UITheme.TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 25));
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
