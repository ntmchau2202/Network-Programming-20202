package server.authentication;

import entity.Player.RankPlayer;
import server.entity.T3DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class T3Authenticator {
    public RankPlayer login(String username, String password) throws SQLException {
        RankPlayer loggedPlayer = null;
        PreparedStatement stm = T3DB.getConnection().prepareStatement(
                "select * from RankPlayer where username = ? and pwd = ?");
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
        return String.valueOf(System.currentTimeMillis()).substring(8, 13) + UUID.randomUUID().toString().substring(1,10);
    }

    public String getHashedPwd(String rawPwd) {
        return "";
    }
}
