package server.model;

import entity.Player.RankPlayer;
import server.entity.database.T3DB;
import server.entity.match.MatchMode;
import server.entity.match.MatchResult;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RankPlayerModel {
    private RankPlayerModel() {

    }

    private static class RankPlayerModelSingleton
    {
        private static final RankPlayerModel INSTANCE = new RankPlayerModel();
    }

    public static RankPlayerModel getRankPlayerModelInstance() {
        return RankPlayerModel.RankPlayerModelSingleton.INSTANCE;
    }

    public boolean updateRankPlayerInfo(RankPlayer rankPlayer, MatchResult matchResult, MatchMode matchMode) throws SQLException {
        // rule: win + 100, lose - 100
        int modifyElo = 0;
        if (matchMode == MatchMode.Ranked) {
            if (matchResult == MatchResult.Win) {
                modifyElo = 100;
            } else if (matchResult == MatchResult.Draw) {
                modifyElo = 0;
            } else {
                modifyElo = -100;
            }
        }
        int modifyNoWonMatch = matchResult == MatchResult.Win ? 1 : 0;

        // update into obj
        rankPlayer.updatePlayerInfo(rankPlayer.getRank(), rankPlayer.getElo() + modifyElo, rankPlayer.getNoPlayedMatch() + 1, rankPlayer.getNoWonMatch() + modifyNoWonMatch);

        // update into RankPlayer table
        PreparedStatement stm = T3DB.getConnection().prepareStatement(
                "UPDATE RankPlayer SET no_match_played = ?, no_match_won = ?, elo = ? WHERE username=?");
        stm.setInt(1, rankPlayer.getNoPlayedMatch());
        stm.setInt(2, rankPlayer.getNoWonMatch());
        stm.setInt(3, rankPlayer.getElo());
        stm.setString(4, rankPlayer.getUsername());
        stm.executeUpdate();

        // TODO: check if player info has been updated successfully: query again and compare result
        return true;
    }

}
