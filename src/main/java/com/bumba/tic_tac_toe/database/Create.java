package com.bumba.tic_tac_toe.database;

import java.sql.*;

public class Create {
    public static boolean createAccount(String username,String password) {
        if (unique(username)){
            insert(username, password);
            return true;
        }
        else {
            return false;
        }
    }

    public static void insert(String name, String pass) {
        String query = "INSERT INTO ACCOUNT (username, password) VALUES ('" + name + "', '" + pass + "')";
        String query2 = "INSERT INTO RANK VALUES ('" + name + "')";
        try (Connection con = Connect.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute(query);
            stmt.execute(query2);
            con.setAutoCommit(false);
            con.commit();
        } catch (SQLException e) {}
    }

    public static boolean unique (String name) {
        String query = "SELECT * FROM ACCOUNT WHERE username = '" + name + "'";
        try (Connection con = Connect.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return !rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}