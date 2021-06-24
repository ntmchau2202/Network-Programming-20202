package server.model;

import entity.Player.LeaderboardPlayer;
import entity.Player.RankPlayer;
import server.entity.database.T3DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardModel {
    private final int LIMITED_NUMBER_OF_TOP_PLAYER = 10;

    private LeaderboardModel() {

    }

    private static class LeaderboardModelSingleton
    {
        private static final LeaderboardModel INSTANCE = new LeaderboardModel();
    }

    public static LeaderboardModel getLeaderboardModelInstance() {
        return LeaderboardModel.LeaderboardModelSingleton.INSTANCE;
    }

    public List<LeaderboardPlayer> getLeaderBoardData(int noRecord) throws SQLException {
        String sql = "select * from LeaderBoard limit " + noRecord;
        Statement stm = T3DB.getConnection().createStatement();
        ResultSet res = stm.executeQuery(sql);
        List<LeaderboardPlayer> lstLeaderboardPlayer = new ArrayList<>();
        while (res.next()) {
            LeaderboardPlayer leaderboardPlayer = new LeaderboardPlayer(res.getInt("usr_rank"),
                    res.getString("username"),
                    res.getInt("no_match_played"), res.getInt("no_match_won"),
                    res.getInt("usr_elo"));
            lstLeaderboardPlayer.add(leaderboardPlayer);
        }
        return lstLeaderboardPlayer;
    }

    public LeaderboardPlayer getRankByUsername(String username, List<LeaderboardPlayer> leaderboardPlayerList) throws SQLException {
        // check username is null
        if (username == null || username.isEmpty()) {
            return null;
        }

        LeaderboardPlayer foundPlayer = null;
        // check if target user exists from leaderboard list
        for (LeaderboardPlayer leaderboardPlayer : leaderboardPlayerList) {
            if (leaderboardPlayer.getUsername().equals(username)) {
                foundPlayer = leaderboardPlayer;
                break;
            }
        }
        // if target user doesn't show up in leaderboard list (cause he's fking weak), then find him in the RankPlayer table
        if (foundPlayer != null) {
            String sql = "select username, no_match_played, no_match_won, elo from RankPlayer order by elo desc";
            Statement stm = T3DB.getConnection().createStatement();
            ResultSet res = stm.executeQuery(sql);
            while (res.next()) {
                if (res.getString("username").equals(username)) {
                    foundPlayer = new LeaderboardPlayer(res.getRow(),res.getString("username"),
                            res.getInt("no_match_played"), res.getInt("no_match_won"), res.getInt("elo"));
                }
            }
        }

        return foundPlayer;
    }

    public boolean updateLeaderboard(RankPlayer rankPlayer, List<LeaderboardPlayer> leaderboardPlayerList) throws SQLException {

        // get number of top players
        int noTopPlayers = 0;
        String sql = "select count(usr_rank) as total from LeaderBoard;";
        Statement stm = T3DB.getConnection().createStatement();
        ResultSet res = stm.executeQuery(sql);
        while(res.next()){
            noTopPlayers = res.getInt("total");
        }

        // if leaderboard is not full, shoot this player to the sky
        if (noTopPlayers < this.LIMITED_NUMBER_OF_TOP_PLAYER) {
            PreparedStatement preStm = T3DB.getConnection().prepareStatement(
                    "insert into LeaderBoard (usr_rank, username, no_match_played, no_match_won, usr_elo) values (?, ?, ?, ?, ?)");
            preStm.setInt(1, noTopPlayers + 1);
            preStm.setString(2, rankPlayer.getUsername());
            preStm.setInt(3, rankPlayer.getNoPlayedMatch());
            preStm.setInt(4, rankPlayer.getNoWonMatch());
            preStm.setInt(5, rankPlayer.getElo());
            preStm.executeUpdate();
            rankPlayer.updatePlayerRank(noTopPlayers + 1);
            return true;
        }

        sql = "select usr_elo from LeaderBoard order by usr_rank desc limit 1";
        stm = T3DB.getConnection().createStatement();
        res = stm.executeQuery(sql);
        while (res.next()) {
            // if last one smaller than current player -> update
            if (res.getInt("usr_elo") < rankPlayer.getElo() ) {
                // get new top players
                
                // delete all data in leaderboard

                // insert new data to leaderboard

                // TODO: update rank for object of current player
                break;
            }
        }

        // if current player can't make to leaderboard so don't need to update

        return true;
    }

}
