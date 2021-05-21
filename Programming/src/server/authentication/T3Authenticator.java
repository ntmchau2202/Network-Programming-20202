package server.authentication;

import entity.Player.Player;
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
        String sessionID = genSessionID();
        // TODO: throw exception if multiple results
        while (res.next()) {
            // TODO: get rank
            loggedPlayer = new RankPlayer(res.getString("username"), sessionID , res.getInt("elo"),res.getInt("no_match_played"), res.getInt("no_match_won"));
//            PreparedStatement stmInsertSessionID = T3DB.getConnection().prepareStatement(
//            		"insert into SessionID (username, session_id) values (?, ?)");
//            stmInsertSessionID.setString(1, username);
//            stmInsertSessionID.setString(2, sessionID);
        }
        return loggedPlayer;
    }

    public String genSessionID() {
        return String.valueOf(System.currentTimeMillis()).substring(8, 13) + UUID.randomUUID().toString().substring(1,10);
    }

    public String getHashedPwd(String rawPwd) {
        return "";
    }

    public RankPlayer register(String username, String password) throws SQLException {
        RankPlayer loggedPlayer = null;
        // check user exists
        PreparedStatement stm = T3DB.getConnection().prepareStatement(
                "select * from RankPlayer where username = ?");
        stm.setString(1, username);
        ResultSet res = stm.executeQuery();
        // TODO: throw exception if multiple results
        int cnt = 0;
        while (res.next()) {
            cnt++;
        }
        // if found user, return null
        if (cnt != 0) {
            return null;
        }

        stm = T3DB.getConnection().prepareStatement(
                "insert into RankPlayer (username, pwd, elo) values (?, ?, 1500)");
        stm.setString(1, username);
        stm.setString(2, password);
        stm.executeUpdate();

        // query again to get logged player
        stm = T3DB.getConnection().prepareStatement(
                "select * from RankPlayer where username = ? and pwd = ?");
        stm.setString(1, username);
        stm.setString(2, password);
        res = stm.executeQuery();
        // TODO: throw exception if multiple results
        while (res.next()) {
            // TODO: get rank
            loggedPlayer = new RankPlayer(res.getString("username"), genSessionID(), res.getInt("elo"),res.getInt("no_match_played"), res.getInt("no_match_won"));
        }
        return loggedPlayer;
    }
    
//    public Player getOnlinePlayer(String sessionID) throws Exception {
//    	Player player = null;
//    	PreparedStatement stm = T3DB.getConnection().prepareStatement(
//    			"select username from SessionID where session_id = ?");
//    	stm.setString(1, sessionID);
//    	ResultSet res = stm.executeQuery();
//    	
//    }
}
