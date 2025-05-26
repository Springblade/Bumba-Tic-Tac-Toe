package com.bumba.tic_tac_toe.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LogIn {
    public static boolean loginAccount(String username, String password) {
        if (check(username, password)) {
            return true;
        } else {
            return false;
        }
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
