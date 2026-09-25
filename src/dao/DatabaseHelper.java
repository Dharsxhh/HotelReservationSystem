package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    
    // Oracle database connection details
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE"; 
    private static final String USER = "SYSTEM";
    private static final String PASSWORD = "nehaa24"; 

    // Static block runs once to load the driver into memory
    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found. Did you add it to the Classpath?");
            e.printStackTrace();
        }
    }

    // Method for other classes to grab a database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // Quick test to verify everything works
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Successfully connected to the Oracle database!");
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect. Check your password and ensure Oracle is running.");
            e.printStackTrace();
        }
    }
}
