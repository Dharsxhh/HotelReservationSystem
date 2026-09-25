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
    
 // NEW METHOD: Fetch all booked rooms with their customer details
    public java.util.List<model.BookedRoom> getAllBookings() {
        java.util.List<model.BookedRoom> bookedRooms = new java.util.ArrayList<>();
        
        // This SQL joins our 3 tables together to get the full picture
        String sql = "SELECT r.room_number, r.room_type, res.customer_name, res.contact_number, res.check_in_date, res.check_out_date " +
                     "FROM rooms r " +
                     "JOIN reserved_rooms rr ON r.room_number = rr.room_number " +
                     "JOIN reservations res ON rr.reservation_id = res.reservation_id " +
                     "WHERE r.is_available = 0 " +
                     "ORDER BY r.room_number";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                bookedRooms.add(new model.BookedRoom(
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getString("customer_name"),
                    rs.getString("contact_number"),
                    rs.getDate("check_in_date"),
                    rs.getDate("check_out_date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bookings.");
            e.printStackTrace();
        }
        
        return bookedRooms;
        
        
    }
 // NEW METHOD: Frees up the room without deleting the customer's history
    public boolean checkoutRoom(String roomNumber) {
        String sql = "UPDATE rooms SET is_available = 1 WHERE room_number = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, roomNumber);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error during checkout process.");
            e.printStackTrace();
            return false;
        }
    }
 // NEW METHOD: Fetches EVERY booking ever made (Current and Past)
    public java.util.List<model.BookedRoom> getBookingHistory() {
        java.util.List<model.BookedRoom> history = new java.util.ArrayList<>();
        
        // Notice we removed the "WHERE r.is_available = 0" filter here
        String sql = "SELECT r.room_number, r.room_type, res.customer_name, res.contact_number, res.check_in_date, res.check_out_date " +
                     "FROM rooms r " +
                     "JOIN reserved_rooms rr ON r.room_number = rr.room_number " +
                     "JOIN reservations res ON rr.reservation_id = res.reservation_id " +
                     "ORDER BY res.check_in_date DESC"; // Orders by most recent check-in

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                history.add(new model.BookedRoom(
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getString("customer_name"),
                    rs.getString("contact_number"),
                    rs.getDate("check_in_date"),
                    rs.getDate("check_out_date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching booking history.");
            e.printStackTrace();
        }
        return history;
    }
}
