package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Room;

public class RoomDAO {

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        // Added max_guests to the query
        String sql = "SELECT room_number, room_type, price, is_available, max_guests FROM rooms";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                String roomType = rs.getString("room_type");
                double price = rs.getDouble("price");
                boolean isAvailable = rs.getInt("is_available") == 1;
                int maxGuests = rs.getInt("max_guests"); // Fetch the new column

                rooms.add(new Room(roomNumber, roomType, price, isAvailable, maxGuests));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all rooms from Oracle.");
            e.printStackTrace();
        }
        return rooms;
    }

    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        // Added max_guests to the query
        String sql = "SELECT room_number, room_type, price, is_available, max_guests FROM rooms WHERE is_available = 1";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                String roomType = rs.getString("room_type");
                double price = rs.getDouble("price");
                int maxGuests = rs.getInt("max_guests"); // Fetch the new column
                
                rooms.add(new Room(roomNumber, roomType, price, true, maxGuests));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available rooms from Oracle.");
            e.printStackTrace();
        }
        return rooms;
    }
}