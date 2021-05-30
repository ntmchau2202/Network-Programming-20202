package entity.Player;

import server.entity.T3DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardPlayerList {
    private static List<LeaderboardPlayer> lstLeaderboardPlayer;
    private static LeaderboardPlayerList leaderboardPlayerListInstance;
    
    private LeaderboardPlayerList() {
        lstLeaderboardPlayer = new ArrayList<>();
    }

    public static LeaderboardPlayerList getLeaderboardPlayerListInstance() throws SQLException {
        if (leaderboardPlayerListInstance == null) {
            leaderboardPlayerListInstance = new LeaderboardPlayerList();
        }
//        synchronizeLeaderboardPlayerWithDb();
        return leaderboardPlayerListInstance;
    }

    public List<LeaderboardPlayer> getLeaderboardPlayerList() {
        return lstLeaderboardPlayer;
    }
    /**
     * this method gets list of LeaderboardPlayer from db
     *
     * @return lstLeaderboardPlayer list of LeaderboardPlayer
     * @throws SQLException exception when failing in getting data from db
     */
    private static List<LeaderboardPlayer> getLstLeaderboardPlayerFromDb() throws SQLException {
//        rank() over(order by elo  desc) as rank
        String sql = "select username, no_match_played, no_match_won, elo from RankPlayer order by elo desc limit 10";
        Statement stm = T3DB.getConnection().createStatement();
        ResultSet res = stm.executeQuery(sql);
        List<LeaderboardPlayer> lstLeaderboardPlayer = new ArrayList<>();
        while (res.next()) {
            LeaderboardPlayer leaderboardPlayer = new LeaderboardPlayer(res.getRow(),res.getString("username"),
                     res.getInt("no_match_played"), res.getInt("no_match_won"), res.getInt("elo"));
            lstLeaderboardPlayer.add(leaderboardPlayer);
        }
        return lstLeaderboardPlayer;
    }
    public void synchronizeLeaderboardPlayerWithDb() throws SQLException {
        lstLeaderboardPlayer = getLstLeaderboardPlayerFromDb();
    }
}
