package dao;

import model.User;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public void ensureDefaultAdmin() {
        String countSql = "SELECT COUNT(*) FROM app_users";
        String insertSql = "INSERT INTO app_users (username, password_hash, salt, full_name, phone, email, role) " +
                           "VALUES (?, ?, ?, ?, ?, ?, 'ADMIN')";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement count = conn.prepareStatement(countSql);
             ResultSet rs = count.executeQuery()) {
            rs.next();
            if (rs.getInt(1) > 0) return;
            String salt = PasswordUtil.newSalt();
            try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                insert.setString(1, "admin");
                insert.setString(2, PasswordUtil.hash("admin123", salt));
                insert.setString(3, salt);
                insert.setString(4, "Hotel Administrator");
                insert.setString(5, "0000000000");
                insert.setString(6, "admin@stayease.local");
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Could not create the default admin: " + e.getMessage());
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT user_id, username, password_hash, salt, full_name, phone, email, role " +
                     "FROM app_users WHERE username = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && PasswordUtil.matches(password, rs.getString("salt"), rs.getString("password_hash"))) {
                    return readUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
        }
        return null;
    }

    public User register(String fullName, String username, String phone, String email, String password) {
        String salt = PasswordUtil.newSalt();
        String sql = "INSERT INTO app_users (username, password_hash, salt, full_name, phone, email, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'CUSTOMER')";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"user_id"})) {
            pstmt.setString(1, username);
            pstmt.setString(2, PasswordUtil.hash(password, salt));
            pstmt.setString(3, salt);
            pstmt.setString(4, fullName);
            pstmt.setString(5, phone);
            pstmt.setString(6, email == null ? "" : email);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) return new User(keys.getLong(1), username, fullName, phone, email, "CUSTOMER");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) return null; // duplicate username
            System.err.println("Registration failed: " + e.getMessage());
        }
        return null;
    }

    public boolean updateProfile(long userId, String fullName, String phone, String email) {
        String sql = "UPDATE app_users SET full_name = ?, phone = ?, email = ? WHERE user_id = ?";
        try (Connection conn = DatabaseHelper.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullName); pstmt.setString(2, phone); pstmt.setString(3, email); pstmt.setLong(4, userId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) { return false; }
    }

    public boolean changePassword(long userId, String currentPassword, String newPassword) {
        String select = "SELECT password_hash, salt FROM app_users WHERE user_id = ?";
        String update = "UPDATE app_users SET password_hash = ?, salt = ? WHERE user_id = ?";
        try (Connection conn = DatabaseHelper.getConnection(); PreparedStatement find = conn.prepareStatement(select)) {
            find.setLong(1, userId);
            try (ResultSet rs = find.executeQuery()) {
                if (!rs.next() || !PasswordUtil.matches(currentPassword, rs.getString("salt"), rs.getString("password_hash"))) return false;
            }
            String salt = PasswordUtil.newSalt();
            try (PreparedStatement save = conn.prepareStatement(update)) {
                save.setString(1, PasswordUtil.hash(newPassword, salt)); save.setString(2, salt); save.setLong(3, userId);
                return save.executeUpdate() == 1;
            }
        } catch (SQLException e) { return false; }
    }

    private User readUser(ResultSet rs) throws SQLException {
        return new User(rs.getLong("user_id"), rs.getString("username"), rs.getString("full_name"),
            rs.getString("phone"), rs.getString("email"), rs.getString("role"));
    }
}
