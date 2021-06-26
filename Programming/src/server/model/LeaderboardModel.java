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

    public int getLIMITED_NUMBER_OF_TOP_PLAYER() {
        return this.LIMITED_NUMBER_OF_TOP_PLAYER;
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
        // if leaderboard is empty -> insert new data
        if (lstLeaderboardPlayer.size() <= 0) {
            System.out.println("im here");
            // get new top players
            lstLeaderboardPlayer = getNewTopPlayers();

            // insert new data to db
            insertNewTopPlayers(lstLeaderboardPlayer);
        } else {
            System.out.println("hmm size is not zero: " + lstLeaderboardPlayer.size());
        }

        return lstLeaderboardPlayer;
    }


    // if passed list is null, this method will find passed player username in RankPlayer table in db
    public LeaderboardPlayer getLeaderboardPlayerByUsername(String username, List<LeaderboardPlayer> leaderboardPlayerList) throws SQLException {
        // check username is null
        if (username == null || username.isEmpty()) {
            return null;
        }

        LeaderboardPlayer foundPlayer = null;

        // check if target user exists from leaderboard list (only if lb is not null)
        if (leaderboardPlayerList != null) {
            for (LeaderboardPlayer leaderboardPlayer : leaderboardPlayerList) {
                if (leaderboardPlayer.getUsername().equals(username)) {
                    foundPlayer = leaderboardPlayer;
                    break;
                }
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

    public void updateLeaderboard(RankPlayer rankPlayer) throws SQLException {
        // flow:
        // node 0:              check user in leaderboard (lb)
        // node 0.yes:          update
        // node 0.no:           check user's elo > the last in lb
        // node 0.no.yes:       update
        // node 0.no.no:        check # TopPlayer == 10
        // node 0.no.no.yes:    do nothing
        // node 0.no.no.no:     update

        // naive implementation

        // node 0: check user exists in lb
        // old rank in lb
        LeaderboardPlayer oldLeaderboardPlayer = getLeaderboardPlayerByUsername(rankPlayer.getUsername(), getLeaderBoardData(10));

        // node 0.yes: user exists in lb -> update
        if (oldLeaderboardPlayer != null) {
            // new rank in current top players
            LeaderboardPlayer newLeaderboardPlayer = getLeaderboardPlayerByUsername(rankPlayer.getUsername(), null);

            // no update only player exists in current top players and rank doesn't change
            if (newLeaderboardPlayer != null && newLeaderboardPlayer.getRank() == oldLeaderboardPlayer.getRank()) {
                // don't do anything
            } else {
                // update
                updateLeaderboard();
            }
        } else {
            // node 0.no: check user's elo > the last in db
            String sql = "select usr_elo from LeaderBoard order by usr_rank desc limit 1";
            Statement stm = T3DB.getConnection().createStatement();
            ResultSet res = stm.executeQuery(sql);
            while (res.next()) {
                // node 0.no.yes: if last one smaller than current player -> update
                if (res.getInt("usr_elo") < rankPlayer.getElo()) {
                    updateLeaderboard();
                    return;
                }

                // if current player can't make to leaderboard so don't need to update
            }

            // node 0.no.no:        check # TopPlayer == 10
            int noTopPlayers = getNoOfTopPlayers();
            // node 0.no.no.no:     update
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
            }
        }
    }

    private int getNoOfTopPlayers() throws SQLException {
        int noTopPlayers = 0;
        String sql = "select count(usr_rank) as total from LeaderBoard;";
        Statement stm = T3DB.getConnection().createStatement();
        ResultSet res = stm.executeQuery(sql);
        while(res.next()){
            noTopPlayers = res.getInt("total");
        }
        return noTopPlayers;
    }

    private List<LeaderboardPlayer> getNewTopPlayers() throws SQLException {
        String sql = "select username, no_match_played, no_match_won, elo from RankPlayer order by elo desc limit " + this.LIMITED_NUMBER_OF_TOP_PLAYER;
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

    private void insertNewTopPlayers(List<LeaderboardPlayer> leaderboardPlayerList) throws SQLException {
        int i;
        PreparedStatement preStm;
        for (i = 0; i < leaderboardPlayerList.size(); i++) {
            preStm = T3DB.getConnection().prepareStatement(
                    "insert into LeaderBoard (usr_rank, username, no_match_played, no_match_won, usr_elo) values (?, ?, ?, ?, ?)");
            preStm.setInt(1, i + 1);
            preStm.setString(2, leaderboardPlayerList.get(i).getUsername());
            preStm.setInt(3, leaderboardPlayerList.get(i).getNoPlayedMatch());
            preStm.setInt(4, leaderboardPlayerList.get(i).getNoWonMatch());
            preStm.setInt(5, leaderboardPlayerList.get(i).getElo());
            preStm.executeUpdate();
        }
    }

    // overload method
    private void updateLeaderboard() throws SQLException {
        // get new top players
        List<LeaderboardPlayer> lstLeaderboardPlayer = getNewTopPlayers();

        // delete all data in leaderboard
        PreparedStatement preStm = T3DB.getConnection().prepareStatement(
                "delete from LeaderBoard");
        preStm.executeUpdate();

        // insert new data to leaderboard
        insertNewTopPlayers(lstLeaderboardPlayer);

        // TODO: update rank for object of current player
    }

}
