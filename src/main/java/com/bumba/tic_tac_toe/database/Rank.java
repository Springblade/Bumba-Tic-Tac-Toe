package com.bumba.tic_tac_toe.database;

import java.sql.*;
import java.util.*;

public class Rank {
    public static List<String> Ranking (){
        List<String> rankResult = new ArrayList<String>();
        String query = "SELECT * FROM RANK ORDER BY elo DESC";
        try (Connection con = Connect.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Rank\tUsername\tElo");

            while (rs.next()) {
                String username = rs.getString("username");
                int elo = rs.getInt("elo");
                rankResult.add(username+"_"+elo);
            }
        } catch (SQLException e) {}
        return rankResult;
    }
}