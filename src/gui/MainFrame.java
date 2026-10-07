package gui;

import dao.ReservationDAO;
import dao.RoomDAO;
import model.Room;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final RoomDAO roomDAO = new RoomDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final DefaultListModel<Room> roomListModel = new DefaultListModel<>();
    private final JLabel totalRoomsValue = statValue();
    private final JLabel availableRoomsValue = statValue();
    private final JLabel occupiedRoomsValue = statValue();
    private final JLabel reservationValue = statValue();
    private final JLabel statusLabel = new JLabel("Ready");
    private final JList<Room> roomList = new JList<>(roomListModel);
    private final String loggedInUser;

    public MainFrame() {
        this("admin");
    }

    public MainFrame(String loggedInUser) {
        UITheme.install();
        this.loggedInUser = loggedInUser;
        setTitle("StayEase | Hotel Operations Dashboard");
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
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
        JLabel welcome = new JLabel("Good day, " + loggedInUser + "");
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
        cards.add(statCard("Available now", availableRoomsValue, UITheme.TEAL));
        cards.add(statCard("Occupied", occupiedRoomsValue, new Color(190, 123, 31)));
        cards.add(statCard("Reservations", reservationValue, new Color(105, 76, 150)));
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
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = new JLabel("Available rooms");
        title.setForeground(UITheme.TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel hint = new JLabel("Select a room to start a new booking");
        hint.setForeground(UITheme.MUTED);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        heading.add(title, BorderLayout.NORTH);
        heading.add(hint, BorderLayout.SOUTH);
        panel.add(heading, BorderLayout.NORTH);

        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setBackground(new Color(249, 251, 253));
        roomList.setSelectionBackground(new Color(218, 239, 237));
        roomList.setSelectionForeground(UITheme.TEXT);
        roomList.setFixedCellHeight(48);
        roomList.setBorder(new EmptyBorder(3, 8, 3, 8));
        roomList.setCellRenderer(new RoomRenderer());
        panel.add(new JScrollPane(roomList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = whitePanel(new BorderLayout(0, 12));
        panel.setPreferredSize(new Dimension(280, 0));
        JLabel title = new JLabel("Quick actions");
        title.setForeground(UITheme.TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        JButton book = actionButton("Book selected room", UITheme.TEAL);
        book.addActionListener(e -> openBooking());
        JButton current = actionButton("View current bookings", UITheme.NAVY);
        current.addActionListener(e -> openCurrentBookings());
        JButton search = actionButton("Search / modify / cancel", new Color(105, 76, 150));
        search.addActionListener(e -> openSearch());
        JButton history = actionButton("View booking history", new Color(190, 123, 31));
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
        JLabel database = new JLabel("● Oracle XE connected");
        database.setForeground(UITheme.TEAL);
        database.setFont(new Font("Segoe UI", Font.BOLD, 12));
        status.add(statusLabel, BorderLayout.WEST);
        status.add(database, BorderLayout.EAST);
        return status;
    }

    private void openBooking() {
        Room selectedRoom = roomList.getSelectedValue();
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Select an available room first.", "No Room Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BookingDialog dialog = new BookingDialog(this, selectedRoom);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            boolean success = reservationDAO.createReservation(dialog.getCustomerName(), dialog.getContactNumber(),
                dialog.getCheckIn(), dialog.getCheckOut(), dialog.getTotalPrice(), selectedRoom.getRoomNumber());
            if (success) {
                statusLabel.setText("Booking created for room " + selectedRoom.getRoomNumber());
                refreshDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Room may no longer be available.", "Booking Not Saved", JOptionPane.ERROR_MESSAGE);
                refreshDashboard();
            }
        }
    }

    private void openCurrentBookings() {
        new ViewBookingsDialog(this).setVisible(true);
        refreshDashboard();
    }

    private void openSearch() {
        new ReservationSearchDialog(this).setVisible(true);
        refreshDashboard();
    }

    private void refreshDashboard() {
        List<Room> allRooms = roomDAO.getAllRooms();
        List<Room> availableRooms = roomDAO.getAvailableRooms();
        roomListModel.clear();
        for (Room room : availableRooms) roomListModel.addElement(room);
        int occupied = Math.max(0, allRooms.size() - availableRooms.size());
        totalRoomsValue.setText(String.valueOf(allRooms.size()));
        availableRoomsValue.setText(String.valueOf(availableRooms.size()));
        occupiedRoomsValue.setText(String.valueOf(occupied));
        reservationValue.setText(String.valueOf(reservationDAO.getBookingHistory().size()));
        statusLabel.setText("Dashboard refreshed • " + availableRooms.size() + " rooms ready");
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

    private static class RoomRenderer extends JPanel implements ListCellRenderer<Room> {
        private final JLabel title = new JLabel();
        private final JLabel detail = new JLabel();

        RoomRenderer() {
            setLayout(new BorderLayout(12, 0));
            setOpaque(true);
            title.setFont(new Font("Segoe UI", Font.BOLD, 14));
            title.setForeground(UITheme.TEXT);
            detail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            detail.setForeground(UITheme.MUTED);
            add(title, BorderLayout.WEST);
            add(detail, BorderLayout.EAST);
            setBorder(new EmptyBorder(5, 8, 5, 8));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Room> list, Room room, int index,
                                                       boolean selected, boolean cellHasFocus) {
            title.setText("Room " + room.getRoomNumber() + "  •  " + room.getRoomType());
            detail.setText(room.getMaxGuests() + " guests   |   ₹" + String.format("%.2f", room.getPrice()) + "/night");
            setBackground(selected ? new Color(218, 239, 237) : new Color(249, 251, 253));
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
