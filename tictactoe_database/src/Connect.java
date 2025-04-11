import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Connect {
    private static final String DB_URL = "jdbc:postgresql://localhost:type your port/type your database name";
    private static final String DB_USERNAME = "type your username";
    private static final String DB_PASSWORD = "type your password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
}
