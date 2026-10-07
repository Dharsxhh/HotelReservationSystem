package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import model.Room;

public class DatabaseHelper {

    // Connection details are read from db.properties in the project folder,
    // so each teammate keeps their own password out of git.
    // Copy db.properties.example to db.properties and fill in your details.
    private static final Properties CONFIG = loadConfig();

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found. Is lib/ojdbc17.jar on the classpath?");
        }
    }

    private static Properties loadConfig() {
        Properties config = new Properties();
        config.setProperty("db.url", "jdbc:oracle:thin:@localhost:1521:XE");
        config.setProperty("db.user", "SYSTEM");
        config.setProperty("db.password", "");
        try (FileInputStream in = new FileInputStream("db.properties")) {
            config.load(in);
        } catch (IOException e) {
            System.err.println("db.properties not found - copy db.properties.example to db.properties and set your password.");
        }
        return config;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            CONFIG.getProperty("db.url"),
            CONFIG.getProperty("db.user"),
            CONFIG.getProperty("db.password"));
    }

    // Used by the dashboard to show whether the database is reachable.
    public static boolean canConnect() {
        try (Connection conn = getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    // Quick connection test: run this class on its own.
    public static void main(String[] args) {
        if (!canConnect()) {
            System.err.println("Failed to connect. Check db.properties and make sure Oracle is running.");
            return;
        }
        System.out.println("Successfully connected to the Oracle database!\n");
        for (Room room : new RoomDAO().getAllRooms()) {
            System.out.println(room);
        }
    }
}
