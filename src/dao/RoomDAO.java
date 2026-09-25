package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Room;

public class RoomDAO {

    // Retrieves all rooms (both available and booked)
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_number, room_type, price, is_available FROM rooms";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                String roomType = rs.getString("room_type");
                double price = rs.getDouble("price");
                boolean isAvailable = rs.getInt("is_available") == 1;

                rooms.add(new Room(roomNumber, roomType, price, isAvailable));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all rooms from Oracle.");
            e.printStackTrace();
        }
        return rooms;
    }

    // Retrieves ONLY rooms that are currently available for booking
    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_number, room_type, price, is_available FROM rooms WHERE is_available = 1";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                String roomType = rs.getString("room_type");
                double price = rs.getDouble("price");
                
                rooms.add(new Room(roomNumber, roomType, price, true));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available rooms from Oracle.");
            e.printStackTrace();
        }
        return rooms;
    }
}