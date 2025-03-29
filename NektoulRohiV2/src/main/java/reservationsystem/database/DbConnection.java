package reservationsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbConnection {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/reservationDB";
    private static final String USER = "reservationDB";
    private static final String PASSWORD = "reservationDB";

    private DbConnection() {}

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create and return the connection
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the reservationsystem.model.database", e);
        }
    }

}
