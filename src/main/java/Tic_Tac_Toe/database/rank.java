package Tic_Tac_Toe.database;

import java.sql.*;

public class rank {
    public static void Ranking (){
        String query = "SELECT * FROM RANK ORDER BY elo DESC";
        try (Connection con = connect.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Rank\tUsername\tElo");
            int rank = 1;
            while (rs.next()) {
                String username = rs.getString("username");
                int elo = rs.getInt("elo");
                System.out.printf("%d\t%s\t\t%d%n", rank++, username, elo);
            }
        } catch (SQLException e) {}
    }
}