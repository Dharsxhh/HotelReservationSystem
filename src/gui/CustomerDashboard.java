package gui;

import dao.ReservationDAO;
import dao.RoomDAO;
import model.BookedRoom;
import model.Room;
import model.User;
import util.Billing;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CustomerDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private final User user;
    private final RoomDAO roomDAO = new RoomDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final JSpinner checkIn = UITheme.dateSpinner(LocalDate.now().plusDays(1));
    private final JSpinner checkOut = UITheme.dateSpinner(LocalDate.now().plusDays(2));
    private final JComboBox<Integer> guests = new JComboBox<>(new Integer[]{1, 2, 3});
    private final JComboBox<String> type = new JComboBox<>(new String[]{"All types", "Standard", "Deluxe", "Suite"});
    private final JPanel cards = new JPanel(new GridLayout(0, 3, 16, 16));
    private final JLabel roomCount = new JLabel(" ");
    private final JLabel roomError = UITheme.errorLabel();
    private final JLabel upcoming = new JLabel();

    public CustomerDashboard(User user) {
        UITheme.install(); this.user = user;
        setTitle("StayEase | Customer Home"); setSize(1100, 720); setLocationRelativeTo(null); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLayout(new BorderLayout());
        add(header(), BorderLayout.NORTH); add(content(), BorderLayout.CENTER); refreshRooms();
    }

    private JPanel header() {
        JPanel header = new JPanel(new BorderLayout()); header.setBackground(UITheme.NAVY); header.setBorder(new EmptyBorder(16, 25, 16, 25));
        JLabel title = new JLabel("STAYEASE  ·  " + greeting() + ", " + firstName()); title.setForeground(Color.WHITE); title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); right.setOpaque(false);
        JButton theme = UITheme.button(UITheme.isDarkMode() ? "☀ Light" : "☾ Dark", UITheme.NAVY_LIGHT); theme.addActionListener(e -> { UITheme.toggleDarkMode(this); theme.setText(UITheme.isDarkMode() ? "☀ Light" : "☾ Dark"); });
        JButton profile = UITheme.button("Profile", UITheme.TEAL); profile.addActionListener(e -> new ProfileDialog(this, Session.current()).setVisible(true));
        JButton bookings = UITheme.button("My bookings", UITheme.TEAL); bookings.addActionListener(e -> new CustomerBookingsDialog(this, user).setVisible(true));
        JButton logout = UITheme.button("Sign out", UITheme.NAVY_LIGHT); logout.addActionListener(e -> signOut());
        right.add(theme); right.add(profile); right.add(bookings); right.add(logout); header.add(title, BorderLayout.WEST); header.add(right, BorderLayout.EAST); return header;
    }

    private JPanel content() {
        JPanel root = new JPanel(new BorderLayout(14, 14)); root.setBackground(UITheme.background()); root.setBorder(new EmptyBorder(20, 25, 20, 25));
        upcoming.setBorder(new EmptyBorder(12, 14, 12, 14)); upcoming.setOpaque(true); upcoming.setBackground(UITheme.card()); root.add(upcoming, BorderLayout.NORTH);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); top.setOpaque(false);
        top.add(new JLabel("Check-in")); top.add(checkIn); top.add(new JLabel("Check-out")); top.add(checkOut); top.add(new JLabel("Guests")); top.add(guests); top.add(type);
        JButton refresh = UITheme.button("Refresh", UITheme.TEAL); refresh.addActionListener(e -> refreshRooms()); top.add(refresh); top.add(roomCount); top.add(roomError);
        JPanel center = new JPanel(new BorderLayout(0, 12)); center.setOpaque(false); center.add(top, BorderLayout.NORTH); cards.setOpaque(false); center.add(new JScrollPane(cards), BorderLayout.CENTER); root.add(center, BorderLayout.CENTER);
        checkIn.addChangeListener(e -> refreshRooms()); checkOut.addChangeListener(e -> refreshRooms()); guests.addActionListener(e -> refreshRooms()); type.addActionListener(e -> refreshRooms()); return root;
    }

    private void refreshRooms() {
        LocalDate in = UITheme.getDate(checkIn), out = UITheme.getDate(checkOut); cards.removeAll();
        if (!out.isAfter(in)) { roomError.setText("Check-out must be after check-in."); cards.revalidate(); cards.repaint(); return; }
        roomError.setText(" "); int guestCount = (Integer) guests.getSelectedItem(); String wantedType = (String) type.getSelectedItem();
        List<Room> available = roomDAO.getAvailableRooms(in, out); int shown = 0;
        for (Room room : available) { if (room.getMaxGuests() < guestCount || (!"All types".equals(wantedType) && !wantedType.equals(room.getRoomType()))) continue; shown++; cards.add(new RoomCard(room, in, out, () -> book(room))); }
        roomCount.setText(shown + (shown == 1 ? " room available" : " rooms available")); if (shown == 0) cards.add(emptyState());
        cards.revalidate(); cards.repaint(); refreshUpcoming();
    }

    private JLabel emptyState() { JLabel label = new JLabel("No rooms free for these dates - try other dates", SwingConstants.CENTER); label.setForeground(UITheme.muted()); return label; }

    private void refreshUpcoming() {
        List<BookedRoom> bookings = reservationDAO.getCurrentBookingsForUser(user.getId());
        if (bookings.isEmpty()) upcoming.setText("No upcoming stays - pick dates below to book one");
        else { BookedRoom stay = bookings.get(0); upcoming.setText("Upcoming stay: Room " + stay.getRoomNumber() + " · " + stay.getCheckIn() + " to " + stay.getCheckOut() + " · " + Billing.money(stay.getTotal())); }
        upcoming.setForeground(UITheme.text());
    }

    private void book(Room room) {
        BookingDialog dialog = new BookingDialog(this, room, UITheme.getDate(checkIn), UITheme.getDate(checkOut), user.getFullName(), user.getPhone()); dialog.setVisible(true); if (!dialog.isConfirmed()) return;
        long id = reservationDAO.createReservation(dialog.getCustomerName(), dialog.getContactNumber(), dialog.getCheckIn(), dialog.getCheckOut(), room.getRoomNumber(), user.getId());
        if (id > 0) { Toast.show(this, "Reservation #" + id + " created successfully"); refreshRooms(); } else JOptionPane.showMessageDialog(this, "The room is no longer available.", "Booking failed", JOptionPane.ERROR_MESSAGE);
    }

    private String firstName() { String[] parts = user.getFullName().trim().split("\\s+"); return parts.length == 0 ? user.getFullName() : parts[0]; }
    private String greeting() { int hour = LocalTime.now().getHour(); return hour < 12 ? "Good morning" : hour < 17 ? "Good afternoon" : "Good evening"; }
    private void signOut() { Session.logout(); dispose(); new LoginFrame().setVisible(true); }
}
