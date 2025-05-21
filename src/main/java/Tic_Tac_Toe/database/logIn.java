package Tic_Tac_Toe.database;

import java.sql.*;

public class logIn {
    public static String loginAccount(String username, String password) {
        if (check(username, password)) {
            return "Success";
        } else {
            return "Invalid username or password.";
        }
    }

    public static boolean check(String name, String pass) {
        String query = "SELECT * FROM ACCOUNT WHERE username = '" + name + "' AND password = '" + pass + "'";
        try (Connection con = connect.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}
