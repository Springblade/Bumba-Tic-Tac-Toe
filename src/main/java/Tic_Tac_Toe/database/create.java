package Tic_Tac_Toe.database;

import java.sql.*;
import java.util.Scanner;

public class create {
    public static void createAccount() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();
        if (unique(username)){
            insert(username, password);
        }
        else {
            System.out.println("Username already exists. Please choose a different username.");
        }
        scanner.close();
    }

    public static void insert(String name, String pass) {
        String query = "INSERT INTO ACCOUNT (username, password) VALUES ('" + name + "', '" + pass + "')";
        String query2 = "INSERT INTO RANK VALUES ('" + name + "')";
        try (Connection con = connect.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute(query);
            stmt.execute(query2);
            con.setAutoCommit(false);
            con.commit();
        } catch (SQLException e) {}
    }

    public static boolean unique (String name) {
        String query = "SELECT * FROM ACCOUNT WHERE username = '" + name + "'";
        try (Connection con = connect.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return !rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}