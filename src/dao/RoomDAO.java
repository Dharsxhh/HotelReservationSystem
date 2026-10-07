package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Room;

public class RoomDAO {

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_number, room_type, price, max_guests FROM rooms ORDER BY room_number";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(readRoom(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rooms: " + e.getMessage());
        }
        return rooms;
    }

    // Rooms with no active (BOOKED) reservation overlapping the given dates.
    // Two stays overlap when one starts before the other ends.
    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.room_number, r.room_type, r.price, r.max_guests FROM rooms r " +
                     "WHERE NOT EXISTS (" +
                     "  SELECT 1 FROM reserved_rooms rr " +
                     "  JOIN reservations res ON res.reservation_id = rr.reservation_id " +
                     "  WHERE rr.room_number = r.room_number AND res.status = 'BOOKED' " +
                     "  AND res.check_in_date < ? AND res.check_out_date > ?) " +
                     "ORDER BY r.room_number";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(checkOut));
            pstmt.setDate(2, Date.valueOf(checkIn));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(readRoom(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available rooms: " + e.getMessage());
        }
        return rooms;
    }

    private Room readRoom(ResultSet rs) throws SQLException {
        return new Room(rs.getString("room_number"), rs.getString("room_type"),
                        rs.getDouble("price"), rs.getInt("max_guests"));
    }
}
