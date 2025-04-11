import java.sql.*;
import java.util.Scanner;

public class Login {
    public static void loginAccount() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();
        if (check(username, password)) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }
        scanner.close();
    }

    public static boolean check(String name, String pass) {
        String query = "SELECT * FROM ACCOUNT WHERE username = '" + name + "' AND password = '" + pass + "'";
        try (Connection con = Connect.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}
