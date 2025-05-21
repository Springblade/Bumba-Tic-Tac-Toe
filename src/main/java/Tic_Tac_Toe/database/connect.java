package Tic_Tac_Toe.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connect {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/tictactoe";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "999999";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
}
