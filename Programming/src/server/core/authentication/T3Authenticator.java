package server.core.authentication;

import entity.Player.GuestPlayer;
import entity.Player.RankPlayer;
import server.entity.database.T3DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class T3Authenticator {
    public RankPlayer login(String username, String password) throws SQLException {
        RankPlayer loggedPlayer = null;

        // return null if user has already logged in
        String foundSessionID = getSessionIDByUsername(username);
        if (foundSessionID != null && !foundSessionID.isEmpty()) {
            return null;
        }

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
        }
        if (loggedPlayer != null) {
            storeSessionID(username, sessionID);
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
        String sessionID = genSessionID();
        // TODO: throw exception if multiple results
        while (res.next()) {
            // TODO: get rank
            loggedPlayer = new RankPlayer(res.getString("username"), sessionID, res.getInt("elo"),res.getInt("no_match_played"), res.getInt("no_match_won"));
        }
        if (loggedPlayer != null) {
            storeSessionID(username, sessionID);
        }
        return loggedPlayer;
    }

    public String getSessionIDByUsername(String username) throws SQLException {
        String sessionID = "";
        PreparedStatement stm = T3DB.getConnection().prepareStatement(
                "select * from SessionID where username = ?");
        stm.setString(1, username);
        ResultSet res = stm.executeQuery();
        // TODO: throw exception if multiple results
        while (res.next()) {
            // TODO: get rank
            sessionID = res.getString("session_id");
        }
        return sessionID;
    }

    public boolean storeSessionID(String username, String sessionID) throws SQLException {
        // return false if user has already logged in
        String foundSessionID = getSessionIDByUsername(username);
        if (foundSessionID != null && !foundSessionID.isEmpty()) {
            return false;
        }

        PreparedStatement stm = T3DB.getConnection().prepareStatement(
                "insert into SessionID (username, session_id) values (?, ?)");
        stm.setString(1, username);
        stm.setString(2, sessionID);
        stm.executeUpdate();

        // query again to get logged player
        foundSessionID = getSessionIDByUsername(username);
        return foundSessionID.equals(sessionID);
    }

    public GuestPlayer createGuestPlayer() throws SQLException {
        String lastDisplayName = "";
        String newGuestDisplayName = "";
        PreparedStatement stm = T3DB.getConnection().prepareStatement(
                "select * from GuestPlayer order by displayname desc limit 1");
        ResultSet res = stm.executeQuery();
        // TODO: throw exception if multiple results
        while (res.next()) {
            lastDisplayName = res.getString("displayname");
        }
        if (!lastDisplayName.isEmpty()) {
            String result = "";
            Pattern p = Pattern.compile("[0-9]+$");
            Matcher m = p.matcher(lastDisplayName);
            if(m.find()) {
                result = m.group();
            }
            int anonID = Integer.parseInt(result);
            newGuestDisplayName = "anon" + Integer.toString(anonID + 1);
        } else {
            newGuestDisplayName = "anon1";
        }

        // gen sessionID
        String sessionID = genSessionID();

        // store guest player
        stm = T3DB.getConnection().prepareStatement(
                "insert into GuestPlayer (displayname) values (?)");
        stm.setString(1, newGuestDisplayName);
        stm.executeUpdate();

        // store sessionID
        storeSessionID(newGuestDisplayName, sessionID);

        return new GuestPlayer(newGuestDisplayName, sessionID);
    }

//    public boolean logout(String username, String sessionID) {
//        // return false if user has already logged in
//        String foundSessionID = getSessionIDByUsername(username);
//        if (foundSessionID != null && !foundSessionID.isEmpty()) {
//            return false;
//        }
//
//        PreparedStatement stm = T3DB.getConnection().prepareStatement(
//                "insert into SessionID (username, session_id) values (?, ?)");
//        stm.setString(1, username);
//        stm.setString(2, sessionID);
//        stm.executeUpdate();
//
//        // query again to get logged player
//        foundSessionID = getSessionIDByUsername(username);
//        return foundSessionID.equals(sessionID);
//    }
    
//    public Player getOnlinePlayer(String sessionID) throws Exception {
//    	Player player = null;
//    	PreparedStatement stm = T3DB.getConnection().prepareStatement(
//    			"select username from SessionID where session_id = ?");
//    	stm.setString(1, sessionID);
//    	ResultSet res = stm.executeQuery();
//    	
//    }
}
