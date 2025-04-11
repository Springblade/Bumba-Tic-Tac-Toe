import java.sql.*;

public class Elo_mod {
    public static void eloUpdate(String username, String result) {
        int adjustElo = 0;
        try (Connection con = Connect.getConnection();
            Statement stmt = con.createStatement()) {
            int currentElo = getCurrentElo(username);
            if (result.equals("win")) {
                adjustElo = currentElo + 10;
            } else if (result.equals("lose")) {
                adjustElo = currentElo - 10;
            }
            String query = "UPDATE RANK SET elo = " + adjustElo + " WHERE username = '" + username + "'";
            stmt.executeUpdate(query);
            con.setAutoCommit(false);
            con.commit();
        } catch (SQLException e) {}
    }

    public static int getCurrentElo(String username){
        int currentElo = 0;
        String query = "SELECT elo FROM RANK WHERE username = '" + username + "'";
        try (Connection con = Connect.getConnection();
            Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                currentElo = rs.getInt("elo");
            }
        } catch (SQLException e) {}
        return currentElo;
    }
}