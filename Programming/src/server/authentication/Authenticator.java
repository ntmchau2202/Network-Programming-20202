package server.authentication;

import entity.Player.Player;
import entity.Player.RankPlayer;
import server.entity.T3DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authenticator {
    public Player login(String username, String password) throws SQLException {
        Player loggedPlayer = null;
        PreparedStatement stm = T3DB.getConnection().prepareStatement(
                "select * from user where username = ? and password = ?");
        stm.setString(1, username);
        stm.setString(2, password);
        ResultSet res = stm.executeQuery();
        // TODO: throw exception if multiple results
        while (res.next()) {
            // TODO: generate session id
            loggedPlayer = new RankPlayer(res.getString("username"), genSessionID(), res.getInt("elo"));
        }
        return loggedPlayer;
    }

    public String genSessionID() {
        return "";
    }
}
