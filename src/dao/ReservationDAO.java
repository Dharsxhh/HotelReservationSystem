package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReservationDAO {

    public boolean createReservation(String customerName, String contact, String checkIn, String checkOut, double total, String roomNumber) {
        
        String insertReservationSQL = "INSERT INTO reservations (customer_name, contact_number, check_in_date, check_out_date, subtotal, total_rent) VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD'), ?, ?)";
        String insertReservedRoomSQL = "INSERT INTO reserved_rooms (reservation_id, room_number) VALUES (?, ?)";
        String updateRoomSQL = "UPDATE rooms SET is_available = 0 WHERE room_number = ?";

        Connection conn = null;

        try {
            conn = DatabaseHelper.getConnection();
            // Turn off auto-commit so we can run all three queries as one safe transaction
            conn.setAutoCommit(false); 

            // 1. Insert into reservations table and get the auto-generated ID back
            long reservationId = -1;
            try (PreparedStatement pstmt1 = conn.prepareStatement(insertReservationSQL, new String[]{"reservation_id"})) {
                pstmt1.setString(1, customerName);
                pstmt1.setString(2, contact);
                pstmt1.setString(3, checkIn);
                pstmt1.setString(4, checkOut);
                pstmt1.setDouble(5, total);
                pstmt1.setDouble(6, total); // total_rent is same as subtotal for now
                
                pstmt1.executeUpdate();
                
                try (ResultSet rs = pstmt1.getGeneratedKeys()) {
                    if (rs.next()) {
                        reservationId = rs.getLong(1);
                    }
                }
            }

            // 2. Link the room to this new reservation
            try (PreparedStatement pstmt2 = conn.prepareStatement(insertReservedRoomSQL)) {
                pstmt2.setLong(1, reservationId);
                pstmt2.setString(2, roomNumber);
                pstmt2.executeUpdate();
            }

            // 3. Mark the room as booked
            try (PreparedStatement pstmt3 = conn.prepareStatement(updateRoomSQL)) {
                pstmt3.setString(1, roomNumber);
                pstmt3.executeUpdate();
            }

            // If we made it here without errors, save it all!
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error saving reservation to database. Rolling back.");
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Cancel all changes if something failed
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
