package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.BookedRoom;
import util.Billing;

public class ReservationDAO {

    // Every reservation list in the app uses this same SELECT, with a different WHERE / ORDER BY.
    private static final String SELECT_RESERVATIONS =
        "SELECT res.reservation_id, r.room_number, r.room_type, r.price, res.customer_name, " +
        "res.contact_number, res.check_in_date, res.check_out_date, res.subtotal, res.total_rent, res.status " +
        "FROM reservations res " +
        "JOIN reserved_rooms rr ON res.reservation_id = rr.reservation_id " +
        "JOIN rooms r ON rr.room_number = r.room_number ";

    // ---------- Create ----------

    // Saves a new booking and returns its reservation ID, or -1 if the room is
    // already booked for those dates (or the database failed).
    public long createReservation(String customerName, String contact, LocalDate checkIn, LocalDate checkOut,
                                  String roomNumber) {
        String insertReservation = "INSERT INTO reservations (customer_name, contact_number, check_in_date, " +
                                   "check_out_date, subtotal, total_rent, status) VALUES (?, ?, ?, ?, ?, ?, 'BOOKED')";
        String insertRoomLink = "INSERT INTO reserved_rooms (reservation_id, room_number) VALUES (?, ?)";

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Lock the room row so two desks can't book the same room at the same moment.
                double price = lockRoomAndGetPrice(conn, roomNumber);
                if (!isRoomFree(conn, roomNumber, checkIn, checkOut, 0)) {
                    conn.rollback();
                    return -1;
                }

                long nights = Billing.nights(checkIn, checkOut);
                long reservationId;
                try (PreparedStatement pstmt = conn.prepareStatement(insertReservation, new String[]{"reservation_id"})) {
                    pstmt.setString(1, customerName);
                    pstmt.setString(2, contact);
                    pstmt.setDate(3, Date.valueOf(checkIn));
                    pstmt.setDate(4, Date.valueOf(checkOut));
                    pstmt.setDouble(5, Billing.subtotal(price, nights));
                    pstmt.setDouble(6, Billing.total(price, nights));
                    pstmt.executeUpdate();
                    try (ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Could not create a reservation ID.");
                        reservationId = keys.getLong(1);
                    }
                }

                try (PreparedStatement pstmt = conn.prepareStatement(insertRoomLink)) {
                    pstmt.setLong(1, reservationId);
                    pstmt.setString(2, roomNumber);
                    pstmt.executeUpdate();
                }

                conn.commit();
                return reservationId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error saving reservation: " + e.getMessage());
            return -1;
        }
    }

    // ---------- Read ----------

    public List<BookedRoom> getCurrentBookings() {
        return query(SELECT_RESERVATIONS + "WHERE res.status = 'BOOKED' ORDER BY res.check_in_date, r.room_number");
    }

    public List<BookedRoom> getBookingHistory() {
        return query(SELECT_RESERVATIONS + "ORDER BY res.check_in_date DESC, res.reservation_id DESC");
    }

    // Matches guest name, contact number, room number or reservation ID (partial text is fine).
    public List<BookedRoom> searchReservations(String searchText) {
        String pattern = "%" + (searchText == null ? "" : searchText.trim().toLowerCase()) + "%";
        String sql = SELECT_RESERVATIONS +
                     "WHERE LOWER(res.customer_name) LIKE ? OR res.contact_number LIKE ? " +
                     "OR r.room_number LIKE ? OR TO_CHAR(res.reservation_id) LIKE ? " +
                     "ORDER BY res.reservation_id DESC";
        return query(sql, pattern, pattern, pattern, pattern);
    }

    // Is this room free for these dates? ignoreReservationId lets an edit ignore its own booking (use 0 for none).
    public boolean isRoomFree(String roomNumber, LocalDate checkIn, LocalDate checkOut, long ignoreReservationId) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            return isRoomFree(conn, roomNumber, checkIn, checkOut, ignoreReservationId);
        } catch (SQLException e) {
            System.err.println("Error checking room availability: " + e.getMessage());
            return false;
        }
    }

    // Active bookings whose stay includes today.
    public int countOccupiedToday() {
        return count("SELECT COUNT(*) FROM reservations WHERE status = 'BOOKED' " +
                     "AND check_in_date <= TRUNC(SYSDATE) AND check_out_date > TRUNC(SYSDATE)");
    }

    public int countActiveReservations() {
        return count("SELECT COUNT(*) FROM reservations WHERE status = 'BOOKED'");
    }

    // ---------- Update ----------

    // Changes guest details, dates and/or room of an active booking, and recalculates the price.
    // Returns false if the new room is taken for those dates or the booking is no longer active.
    public boolean updateReservation(long reservationId, String customerName, String contact,
                                     LocalDate checkIn, LocalDate checkOut, String roomNumber) {
        String updateReservation = "UPDATE reservations SET customer_name = ?, contact_number = ?, check_in_date = ?, " +
                                   "check_out_date = ?, subtotal = ?, total_rent = ? " +
                                   "WHERE reservation_id = ? AND status = 'BOOKED'";
        String updateRoomLink = "UPDATE reserved_rooms SET room_number = ? WHERE reservation_id = ?";

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            try {
                double price = lockRoomAndGetPrice(conn, roomNumber);
                if (!isRoomFree(conn, roomNumber, checkIn, checkOut, reservationId)) {
                    conn.rollback();
                    return false;
                }

                long nights = Billing.nights(checkIn, checkOut);
                int updated;
                try (PreparedStatement pstmt = conn.prepareStatement(updateReservation)) {
                    pstmt.setString(1, customerName);
                    pstmt.setString(2, contact);
                    pstmt.setDate(3, Date.valueOf(checkIn));
                    pstmt.setDate(4, Date.valueOf(checkOut));
                    pstmt.setDouble(5, Billing.subtotal(price, nights));
                    pstmt.setDouble(6, Billing.total(price, nights));
                    pstmt.setLong(7, reservationId);
                    updated = pstmt.executeUpdate();
                }
                if (updated != 1) {
                    conn.rollback();
                    return false;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(updateRoomLink)) {
                    pstmt.setString(1, roomNumber);
                    pstmt.setLong(2, reservationId);
                    pstmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            return false;
        }
    }

    // Cancelling keeps the row (status CANCELLED) so it still shows in history.
    // Only active bookings can be cancelled, so an old stay can never free a room someone is using now.
    public boolean cancelReservation(long reservationId) {
        return changeStatus(reservationId, "CANCELLED");
    }

    public boolean checkOut(long reservationId) {
        return changeStatus(reservationId, "CHECKED_OUT");
    }

    // ---------- Helpers ----------

    private boolean changeStatus(long reservationId, String newStatus) {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ? AND status = 'BOOKED'";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setLong(2, reservationId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error changing reservation status: " + e.getMessage());
            return false;
        }
    }

    private double lockRoomAndGetPrice(Connection conn, String roomNumber) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT price FROM rooms WHERE room_number = ? FOR UPDATE")) {
            pstmt.setString(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) throw new SQLException("Room " + roomNumber + " does not exist.");
                return rs.getDouble("price");
            }
        }
    }

    private boolean isRoomFree(Connection conn, String roomNumber, LocalDate checkIn, LocalDate checkOut,
                               long ignoreReservationId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserved_rooms rr " +
                     "JOIN reservations res ON res.reservation_id = rr.reservation_id " +
                     "WHERE rr.room_number = ? AND res.status = 'BOOKED' AND res.reservation_id <> ? " +
                     "AND res.check_in_date < ? AND res.check_out_date > ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNumber);
            pstmt.setLong(2, ignoreReservationId);
            pstmt.setDate(3, Date.valueOf(checkOut));
            pstmt.setDate(4, Date.valueOf(checkIn));
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 0;
            }
        }
    }

    private List<BookedRoom> query(String sql, String... params) {
        List<BookedRoom> list = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new BookedRoom(
                        rs.getLong("reservation_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price"),
                        rs.getString("customer_name"),
                        rs.getString("contact_number"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        rs.getDouble("subtotal"),
                        rs.getDouble("total_rent"),
                        rs.getString("status")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading reservations: " + e.getMessage());
        }
        return list;
    }

    private int count(String sql) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("Error counting reservations: " + e.getMessage());
            return 0;
        }
    }
}
